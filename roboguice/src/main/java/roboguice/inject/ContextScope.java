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

import roboguice.application.RoboApplication;
import roboguice.util.Ln;
import roboguice.util.Strings;

import android.content.Context;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.internal.Maps;

import java.util.ArrayList;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author Mike Burton
 */
public class ContextScope implements Scope {

    protected ThreadLocal<WeakHashMap<Context,Map<Key<?>, Object>>> values = new ThreadLocal<WeakHashMap<Context,Map<Key<?>, Object>>>();
    protected ThreadLocal<Context> currentContext = new ThreadLocal<Context>();
    protected ArrayList<ViewMembersInjector<?>> viewsForInjection = new ArrayList<ViewMembersInjector<?>>();
    protected ArrayList<PreferenceMembersInjector<?>> preferencesForInjection = new ArrayList<PreferenceMembersInjector<?>>();

    public ContextScope(RoboApplication app) {
        enter(app);
    }

    public void registerViewForInjection(ViewMembersInjector<?> injector) {
        viewsForInjection.add(injector);
    }

    public void registerPreferenceForInjection(PreferenceMembersInjector<?> injector) {
        preferencesForInjection.add(injector);
    }

    public void injectViews() {
        for (int i = viewsForInjection.size() - 1; i >= 0; --i)
            viewsForInjection.remove(i).reallyInjectMembers();
    }

    public void injectPreferenceViews() {
        for (int i = preferencesForInjection.size() - 1; i >= 0; --i)
            preferencesForInjection.remove(i).reallyInjectMembers();
    }


    public void enter(Context context) {
        currentContext.set(context);

        final Key<Context> key = Key.get(Context.class);
        getScopedObjectMap(key).put(key, context);

        final WeakHashMap<Context,Map<Key<?>,Object>> map = values.get();
        if( map!=null )
            Ln.d("Contexts in the %s scope map after inserting %s: %s", Thread.currentThread().getName(), context, Strings.join( ", ", map.keySet()));
    }

    public void exit(Context context) {
        final WeakHashMap<Context,Map<Key<?>,Object>> map = values.get();
        if( map!=null ) {
            final Map<Key<?>,Object> scopedObjects = map.remove(context);
            if( scopedObjects!=null )
                scopedObjects.clear();

            Ln.d("Contexts in the %s scope map after removing %s: %s", Thread.currentThread().getName(), context, Strings.join( ", ", map.keySet()));
        }
    }

    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
        return new Provider<T>() {
            public T get() {
                Map<Key<?>, Object> scopedObjects = getScopedObjectMap(key);

                @SuppressWarnings("unchecked")
                T current = (T) scopedObjects.get(key);
                if (current == null && !scopedObjects.containsKey(key)) {
                    current = unscoped.get();
                    scopedObjects.put(key, current);
                }
                return current;
            }
        };
    }

    protected <T> Map<Key<?>, Object> getScopedObjectMap(Key<T> key) {
        final Context context = currentContext.get();

        WeakHashMap<Context,Map<Key<?>, Object>> contextMap = values.get();
        if( contextMap==null ) {
            contextMap = new WeakHashMap<Context,Map<Key<?>,Object>>();
            values.set( contextMap );
        }


        Map<Key<?>,Object> scopedObjects = contextMap.get(context);
        if (scopedObjects == null) {
            scopedObjects = Maps.newHashMap();
            contextMap.put(context, scopedObjects);
        }

        return scopedObjects;
    }

}
