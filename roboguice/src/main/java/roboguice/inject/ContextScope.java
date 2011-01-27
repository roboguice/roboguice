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

/**
 * Scopes a single execution of a block of code. Apply this scope with a
 * try/finally block: <pre>   {@code
 *
 *   scope.enter();
 *   try {
 *     // explicitly seed some seed objects...
 *     scope.seed(Key.get(SomeObject.class), someObject);
 *     // create and access scoped objects
 *   } finally {
 *     scope.exit();
 *   }
 * }</pre>
 *
 * The scope can be initialized with one or more seed values by calling
 * <code>seed(key, value)</code> before the injector will be called upon to
 * provide for this key. A typical use is for a servlet filter to enter/exit the
 * scope, representing a Request Scope, and seed HttpServletRequest and
 * HttpServletResponse.  For each key inserted with seed(), it's good practice
 * (since you have to provide <i>some</i> binding anyhow) to include a
 * corresponding binding that will throw an exception if Guice is asked to
 * provide for that key if it was not yet seeded: <pre>   {@code
 *
 *   bind(key)
 *       .toProvider(ContextScope.<KeyClass>seededKeyProvider())
 *       .in(ScopeAnnotation.class);
 * }</pre>
 *
 * @author Jesse Wilson
 * @author Fedor Karpelevitch
 *
 *
 * From http://code.google.com/p/google-guice/wiki/CustomScopes
 */


import android.app.Application;
import android.content.Context;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Mike Burton
 */
public class ContextScope implements Scope {

    protected final ThreadLocal<Map<Key<Context>, Object>> values = new ThreadLocal<Map<Key<Context>, Object>>();
    protected ArrayList<ViewMembersInjector<?>> viewsForInjection = new ArrayList<ViewMembersInjector<?>>();
    protected Application app;

    public ContextScope( Application app ) {
        this.app = app;
    }

    /**
     * Scopes can be entered multiple times with no problems (eg. from
     * onCreate(), onStart(), etc). However, once they're closed, all their
     * previous values are gone forever until the scope is reinitialized again
     * via enter().
     */
    public void enter(Context context) {
        Map<Key<Context>,Object> map = values.get();
        if( map==null ) {
            map = new HashMap<Key<Context>,Object>();
            values.set(map);
        }

        map.put(Key.get(Context.class), context);
    }

    public void exit(Context ignored) {
        values.remove();
    }

    public void registerViewForInjection(ViewMembersInjector<?> injector) {
        viewsForInjection.add(injector);
    }

    public void injectViews() {
        for (int i = viewsForInjection.size() - 1; i >= 0; --i) {
            viewsForInjection.remove(i).reallyInjectMembers();
        }
    }

    public Provider<Context> scope() {
        return scope(Key.get(Context.class), new Provider<Context>() {
            public Context get() {
                return app;
            }
        });
    }

    /**
     * @param <T> is only allowed to be Context
     */
    @SuppressWarnings({"SuspiciousMethodCalls","unchecked"})
    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
        return new Provider<T>() {
            public T get() {
                final Map<Key<Context>,Object> map = values.get();
                final Map<Key<Context>, Object> scopedObjects = map != null ? map : initialScopedObjectMap();

                T current = (T) scopedObjects.get(key);
                if (current == null && !scopedObjects.containsKey(key)) {
                    current = unscoped.get();
                    scopedObjects.put((Key<Context>) key, current);
                }
                return current;
            }
        };
    }

    protected Map<Key<Context>,Object> initialScopedObjectMap() {
        final HashMap<Key<Context>,Object> map = new HashMap<Key<Context>,Object>();
        map.put(Key.get(Context.class),app);
        return map;
    }

}
