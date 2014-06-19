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

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import roboguice.util.RoboContext;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

import android.app.Application;
import android.content.ContextWrapper;

/**
 * Scopes the injector based on the current fragment. As requested by issue #XXX
 *
 * Any usage of this class must call #enter(Context) before performing any operations with the
 * injector, and do so within a synchronized block on the FragmentScope.class, eg:
 * <pre>
 * synchronized(FragmentScope.class) {
 *     scope.enter(fragment);
 *
 *     // do something, eg.
 *     // injector.injectMembers(this);
 * }
 * </pre>
 * If you're using FragmentScopedRoboInjector (which is the RoboGuice default), this is done for you automatically.
 *
 * If you're trying to use a Provider, you must either use FragmentScopedProvider instead, or do your own synchronization
 * and scope.enter() call.
 *
 * @see FragmentScopedRoboInjector
 * @author SNI
 */
public class FragmentScope implements Scope {
    //Object is used here to accomodate both native and support Fragment 
    protected ThreadLocal<Stack<WeakReference<Object>>> contextThreadLocal = new ThreadLocal<Stack<WeakReference<Object>>>();
    protected Map<Key<?>,Object> applicationScopedObjects = new HashMap<Key<?>, Object>();
    protected Application application;

    public FragmentScope(Application application) {
        this.application = application;
    }

    /**
     * You MUST perform any injector operations inside a synchronized(FragmentScope.class) block that starts with
     * scope.enter(context) if working in a multithreaded environment
     *
     * @see FragmentScope
     * @see FragmentScopedRoboInjector
     * @see FragmentScopedProvider
     * @param context the context to enter
     */
    public void enter(Object context) {

        // BUG synchronizing on FragmentScope.class may be overly conservative
        synchronized (FragmentScope.class) {

            final Stack<WeakReference<Object>> stack = getContextStack();
            final Map<Key<?>,Object> map = getScopedObjectMap(context);

            // Mark this thread as for this context
            stack.push(new WeakReference<Object>(context));

            // Add the context to the scope for key Context, Activity, etc.
            Class<?> c = context.getClass();
            do {
                map.put(Key.get(c), context);
                c = c.getSuperclass();
            } while( c!=Object.class );
        }
    }

    public void exit(Object context) {
        synchronized (FragmentScope.class) {
            final Stack<WeakReference<Object>> stack = getContextStack();
            final Object c = stack.pop().get();
            if( c!=null && c!=context )
                throw new IllegalArgumentException(String.format("Scope for %s must be opened before it can be closed",context));
        }
    }

    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
        return new Provider<T>() {
            public T get() {
                synchronized (FragmentScope.class) {
                    final Stack<WeakReference<Object>> stack = getContextStack();
                    final Object context = stack.peek().get(); // The context should never be finalized as long as the provider is still in memory
                    final Map<Key<?>, Object> objectsForScope = getScopedObjectMap(context);
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


    public Stack<WeakReference<Object>> getContextStack() {
        Stack<WeakReference<Object>> stack = contextThreadLocal.get();
        if( stack==null ) {
            stack = new Stack<WeakReference<Object>>();
            contextThreadLocal.set(stack);
        }
        return stack;
    }

    protected Map<Key<?>,Object> getScopedObjectMap(final Object origContext) {
        Object context = origContext;
        while( !(context instanceof RoboContext) && !(context instanceof Application) && context instanceof ContextWrapper )
            context = ((ContextWrapper)context).getBaseContext();

        // Special case for application so that users don't have to manually set up application subclasses
        if( context instanceof Application )
            return applicationScopedObjects;


        if( !(context instanceof RoboContext) )
            throw new IllegalArgumentException(String.format("%s does not appear to be a RoboGuice context (instanceof RoboContext)",origContext));

        return ((RoboContext)context).getScopedObjectMap();
    }
}
