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
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Scopes the injector based on the current context.
 *
 * Any usage of this class must call #enter(Context) before performing any operations with the
 * injector, and do so within a synchronized block on the ContextScope.class, eg:
 *
 * synchronized(ContextScope.class) {
 * scope.enter(context);
 *
 * // do something, eg.
 * // injector.injectMembers(this);
 * }
 *
 * If you're using ContextScopedRoboInjector (which is the RoboGuice default), this is done for you automatically.
 *
 * If you're trying to use a Provider, you must either use ContextScopedProvider instead, or do your own synchronization
 * and scope.enter() call.
 *
 * @author Mike Burton
 * @see ContextScopedRoboInjector
 */
public class ContextScope implements Scope {
    protected ThreadLocal<Stack<ScopedObjects>> contextThreadLocal = new ThreadLocal<Stack<ScopedObjects>>();
    protected Map<Key<?>, Object> applicationScopedObjects = new HashMap<Key<?>, Object>();
    protected Application application;

    public ContextScope(Application application) {
        this.application = application;
        enter(application, applicationScopedObjects);
    }

    /**
     * You MUST perform any injector operations inside a synchronized(ContextScope.class) block that starts with
     * scope.enter(context) if working in a multithreaded environment
     *
     * @param context the context to enter
     * @see ContextScope
     * @see ContextScopedRoboInjector
     * @see ContextScopedProvider
     */
    public void enter(Context context, Map<Key<?>, Object> scopedObjects) {

        // BUG synchronizing on ContextScope.class may be overly conservative
        synchronized (ContextScope.class) {

            final Stack<ScopedObjects> stack = getContextStack();

            // Mark this thread as for this context
            stack.push(new ScopedObjects(new WeakReference<Context>(context), scopedObjects));

            // Add the context to the scope for key Context, Activity, etc.
            Class<?> c = context.getClass();
            do {
                scopedObjects.put(Key.get(c), context);
                c = c.getSuperclass();
            } while (c != Object.class);
        }
    }

    public void exit(Context context) {
        synchronized (ContextScope.class) {
            final Stack<ScopedObjects> stack = getContextStack();
            final Context c = stack.pop().getContextWeakReference().get();
            if (c != null && c != context) {
                throw new IllegalArgumentException(String.format("Scope for %s must be opened before it can be closed", context));
            }
        }
    }

    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
        return new Provider<T>() {
            public T get() {
                synchronized (ContextScope.class) {
                    final Stack<ScopedObjects> stack = getContextStack();
                    final ScopedObjects scopedObjects = stack.peek(); // The context should never be finalized as long as the provider is still in memory
                    if (scopedObjects == null) {
                        return null;  // May want to consider throwing an exception here (if provider is used after onDestroy())
                    }

                    @SuppressWarnings({"unchecked"}) T current = (T) scopedObjects.getScopedObjects().get(key);
                    if (current == null && !scopedObjects.getScopedObjects().containsKey(key)) {
                        current = unscoped.get();
                        scopedObjects.getScopedObjects().put(key, current);
                    }

                    return current;
                }
            }
        };
    }

    private Stack<ScopedObjects> getContextStack() {
        Stack<ScopedObjects> stack = contextThreadLocal.get();
        if (stack == null) {
            stack = new Stack<ScopedObjects>();
            contextThreadLocal.set(stack);
        }
        return stack;
    }
}
