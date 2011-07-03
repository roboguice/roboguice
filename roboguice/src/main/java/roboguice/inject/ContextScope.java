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
import java.util.WeakHashMap;

/**
 * @author Mike Burton
 */
public class ContextScope implements Scope {

    protected WeakHashMap<Context,Map<Key<?>, WeakReference<Object>>> values = new WeakHashMap<Context,Map<Key<?>, WeakReference<Object>>>();
    protected ThreadLocal<WeakReference<Context>> threadLocal = new ThreadLocal<WeakReference<Context>>();


    public ContextScope(Application app) {
        enter(app); // BUG I don't think it should set up the context by default
    }


    public void enter(Context context) {

        // Mark this thread as for this context
        threadLocal.set(new WeakReference<Context>(context));

        // Add the context to the scope
        final Key<Context> key = Key.get(Context.class);
        getScopedObjectMap(key).put(key, new WeakReference<Object>(context));

    }


    @SuppressWarnings({"unchecked"})
    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
        return new Provider<T>() {
            public T get() {
                final Map<Key<?>, WeakReference<Object>> scopedObjects = getScopedObjectMap(key);

                final WeakReference<Object> ref = scopedObjects.get(key);
                T current = (T) (ref!=null ? ref.get() : null);
                if (current == null && !scopedObjects.containsKey(key)) {
                    current = unscoped.get();
                    scopedObjects.put(key, new WeakReference<Object>(current));
                }
                return current;
            }
        };
    }

    @SuppressWarnings({"UnusedParameters"})
    protected <T> Map<Key<?>, WeakReference<Object>> getScopedObjectMap(Key<T> key) {

        final WeakReference<Context> contextRef = threadLocal.get();
        if( contextRef!=null ) {
            final Context context = contextRef.get();
            if( context!=null ) {
                Map<Key<?>,WeakReference<Object>> scopedObjects = values.get(context);
                if (scopedObjects == null) {
                    scopedObjects = new HashMap<Key<?>, WeakReference<Object>>();
                    values.put(context, scopedObjects);
                }
                return scopedObjects;
            }
        }

        throw new UnsupportedOperationException("Cannot perform injection outside of a context scope");
    }

}
