/*
 * Copyright 2009 Michael Burton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package roboguice.inject;

import android.app.Application;
import android.content.Context;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Stack;

/**
 * Scopes the injector based on the current context.
 *
 * Any usage of this class must call #enter(Context) before performing any operations with the
 * injector, and do so within a synchronized block on the ContextScope.class, eg:
 *
 * synchronized(ContextScope.class) {
 *     scope.enter(context);
 *
 *     // do something, eg.
 *     // injector.injectMembers(this);
 * }
 *
 * If you're using ContextScopedRoboInjector (which is the RoboGuice default), this is done for you automatically.
 *
 * If you're trying to use a Provider, you must either use ContextScopedProvider instead, or do your own synchronization
 * and scope.enter() call.
 *
 * @see ContextScopedRoboInjector
 * @author Mike Burton
 */
public class ContextScope implements Scope {
    protected static LinkedHashSet<String> deadToMe = new LinkedHashSet<String>();

    protected HashMap<Context, Map<Key<?>, Object>> scopedObjects = new HashMap<Context, Map<Key<?>, Object>>();
    protected ThreadLocal<Stack<Context>> contextThreadLocal = new ThreadLocal<Stack<Context>>();
    protected Application application;

    public ContextScope(Application application) {
        this.application = application;
        enter(application);
    }

    /**
     * You MUST perform any injector operations inside a synchronized(ContextScope.class) block that starts with
     * scope.enter(context) if working in a multithreaded environment
     *
     * @see ContextScope
     * @see ContextScopedRoboInjector
     * @see ContextScopedProvider
     * @param context the context to enter
     */
    public void enter(Context context) {

        // BUG synchronizing on ContextScope.class may be overly conservative (except we need it for deadToMe)
        synchronized (ContextScope.class) {
            if( deadToMe.contains(context.toString()) )
                throw new IllegalStateException(String.format("Attempt to enter scope for %s after onDestroy has already been called",context));

            final Stack<Context> stack = getContextStack();
            final Map<Key<?>,Object> map = getOrCreateScopedObjectMap(context);

            // Mark this thread as for this context
            stack.push(context);

            // Add the context to the scope for key Context, Activity, etc.
            Class<?> c = context.getClass();
            do {
                map.put(Key.get(c), context);
                c = c.getSuperclass();
            } while( c!=Object.class );
        }

    }

    public void exit(Context context) {
        synchronized (ContextScope.class) {
            final Stack<Context> stack = getContextStack();

            if( stack.pop()!=context )
                throw new IllegalArgumentException(String.format("Scope for %s must be opened before it can be closed",context));
        }
    }

    /**
     * MUST be called when a context is created
     * This is necessary because it appears that android sometimes re-uses instances of activities after onDestroy has been called.
     * This causes the deadToMe check to fail, since deadToMe detects that we're opening a scope on a previously destroyed activity.
     * To get around this, we clear this context from the deadToMe list onCreate.
     * This is ugly because we have to do it statically, and thus make deadToMe static, because we can't get the ContextScope
     * from the injector because doing so would open a new scope, but we have to clear this context before we enter the new scope.
     * @param context
     */
    public static void onCreate(Context context) {
        synchronized (ContextScope.class) {
            deadToMe.remove(context.toString());
        }
    }

    /**
     * MUST be called when a context is destroyed, otherwise will leak memory
     */
    public void onDestroy(Context context) {
        synchronized (ContextScope.class) {
            //noinspection StatementWithEmptyBody
            while(getContextStack().remove(context)) ;
            scopedObjects.remove(context).clear();

            // Keep track of the last 20 contexts that we destroyed so we can throw
            // an error in enter() if a user attempts to open a scope for one of these
            // contexts after it's been destroyed
            while( deadToMe.size()>20 )
                deadToMe.remove(deadToMe.iterator().next());
            deadToMe.add(context.toString());
        }
    }


    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
        return new Provider<T>() {
            public T get() {
                synchronized (ContextScope.class) {
                    final Stack<Context> stack = getContextStack();
                    final Context context = stack.peek();
                    final Map<Key<?>, Object> objectsForScope = scopedObjects.get(context);
                    if( objectsForScope==null )
                        return null;  // May want to consider throwing an exception here (if provider is used after onDestroy())

                    @SuppressWarnings({"unchecked"}) T current = (T) objectsForScope.get(key);
                    if (current==null && !objectsForScope.containsKey(key)) {
                        current = unscoped.get();
                        objectsForScope.put(key, current);
                    }

                    return current;
                }
            }
        };

    }

    protected Map<Key<?>, Object> getOrCreateScopedObjectMap(Context context) {

        Map<Key<?>, Object> scopedObjects = this.scopedObjects.get(context);
        if (scopedObjects == null) {
            scopedObjects = new HashMap<Key<?>, Object>();
            this.scopedObjects.put(context, scopedObjects);
        }
        return scopedObjects;
    }

    public Stack<Context> getContextStack() {
        Stack<Context> stack = contextThreadLocal.get();
        if( stack==null ) {
            stack = new Stack<Context>();
            contextThreadLocal.set(stack);
        }
        return stack;
    }
}
