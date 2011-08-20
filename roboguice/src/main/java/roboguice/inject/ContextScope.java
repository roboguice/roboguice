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

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

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

    protected WeakHashMap<Context, Map<Key<?>, WeakReference<Object>>> scopedObjects = new WeakHashMap<Context, Map<Key<?>, WeakReference<Object>>>();
    protected WeakReference<Context> contextRef = new WeakReference<Context>(null);


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

        if( contextRef!=null )
            throw new IllegalArgumentException(String.format("Scope for %s must be closed before scope for %s may be opened",contextRef.get(),context));
        
        // Mark this thread as for this context
        contextRef = new WeakReference<Context>(context);

        // Add the context to the scope
        getScopedObjectMap(context).put(Key.get(Context.class), new WeakReference<Object>(context));

    }

    public void exit(Context context) {
        final Context prev = contextRef.get();
        if( prev!=context )
            throw new IllegalArgumentException(String.format("Scope for %s must be opened before it can be closed",context));

        contextRef = null;
    }


    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
        return new Provider<T>() {
            public T get() {
                if( contextRef!=null ) {
                    final Context context = contextRef.get();
                    if (context != null) {
                        final Map<Key<?>, WeakReference<Object>> scopedObjects = getScopedObjectMap(context);

                        final WeakReference<Object> ref = scopedObjects.get(key);
                        @SuppressWarnings({"unchecked"}) T current = (T) (ref!=null ? ref.get() : null);
                        if (current == null && !scopedObjects.containsKey(key)) {
                            current = unscoped.get();
                            scopedObjects.put(key, new WeakReference<Object>(current));
                        }
                        return current;
                    }
                }

                throw new UnsupportedOperationException(String.format("%s is context-scoped and can't be injected outside of a context scope. Did you intend to make the referencing class @ContextScoped or use ContextScopedProvider instead of Provider?",key.getTypeLiteral().getType()));
            }
        };

    }

    protected Map<Key<?>, WeakReference<Object>> getScopedObjectMap(Context context) {

        Map<Key<?>, WeakReference<Object>> scopedObjects = this.scopedObjects.get(context);
        if (scopedObjects == null) {
            scopedObjects = new HashMap<Key<?>, WeakReference<Object>>();
            this.scopedObjects.put(context, scopedObjects);
        }
        return scopedObjects;
    }

}
