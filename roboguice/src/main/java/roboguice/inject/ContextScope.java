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

import android.content.Context;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

import java.util.HashMap;
import java.util.Map;

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

    protected HashMap<Context, Map<Key<?>, Object>> scopedObjects = new HashMap<Context, Map<Key<?>, Object>>();
    protected ThreadLocal<Context> contextThreadLocal = new ThreadLocal<Context>();


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

        // BUG synchronizing on ContextScope.class may be overly conservative
        synchronized (ContextScope.class) {
            final Context prev = contextThreadLocal.get();
            final Map<Key<?>,Object> map = getScopedObjectMap(context);

            if( prev!=null )
                throw new IllegalArgumentException(String.format("Scope for %s must be closed before scope for %s may be opened",prev,context));

            // Mark this thread as for this context
            contextThreadLocal.set( context );

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
            final Context prev = contextThreadLocal.get();
            if( prev!=context )
                throw new IllegalArgumentException(String.format("Scope for %s must be opened before it can be closed",context));

            contextThreadLocal.set(null);
        }
    }

    /**
     * MUST be called when a context is destroyed, otherwise will leak memory
     */
    public void destroy(Context context) {
        synchronized (ContextScope.class) {
            contextThreadLocal.set(null);
            final Map<Key<?>,Object> contextMap = scopedObjects.remove(context);
            contextMap.clear();
        }
    }


    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
        return new Provider<T>() {
            public T get() {
                synchronized (ContextScope.class) {
                    final Context context = contextThreadLocal.get();
                    if (context != null) {
                        final Map<Key<?>, Object> scopedObjects = getScopedObjectMap(context);

                        @SuppressWarnings({"unchecked"}) T current = (T) scopedObjects.get(key);
                        if (current==null && !scopedObjects.containsKey(key)) {
                            current = unscoped.get();
                            scopedObjects.put(key, current);
                        }
                        
                        return current;
                    }
                }

                throw new UnsupportedOperationException(String.format("%s is context-scoped and can't be injected outside of a context scope. Did you intend to make the referencing class @ContextSingleton or use ContextScopedProvider instead of Provider?",key.getTypeLiteral().getType()));
            }
        };

    }

    protected Map<Key<?>, Object> getScopedObjectMap(Context context) {

        Map<Key<?>, Object> scopedObjects = this.scopedObjects.get(context);
        if (scopedObjects == null) {
            scopedObjects = new HashMap<Key<?>, Object>();
            this.scopedObjects.put(context, scopedObjects);
        }
        return scopedObjects;
    }

}
