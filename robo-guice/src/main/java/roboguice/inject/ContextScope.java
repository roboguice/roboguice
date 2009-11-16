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


import java.util.HashMap;
import java.util.Map;

import com.google.inject.Key;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.Scope;

import android.content.Context;

public class ContextScope implements Scope {

    protected static final Provider<Object> SEEDED_KEY_PROVIDER = new Provider<Object>() {
        public Object get() {
            throw new IllegalStateException("If you got here then it means that your code asked for scoped object which should have been explicitly seeded in this scope by calling ContextScope.seed(), but was not.");
        }
    };

    protected final ThreadLocal<Map<Key<?>, Object>> values = new ThreadLocal<Map<Key<?>, Object>>();

    /**
     * Scopes can be entered multiple times with no problems (eg. from onCreate(), onStart(), etc).
     * However, once they're closed, all their previous values are gone forever
     * until the scope is reinitialized again via enter().
     */
    public void enter( Context context ) {
        if(values.get()==null )
            values.set( new HashMap<Key<?>,Object>( ) );

        values.get().put(Key.get(Context.class), context);
    }


    public void exit( Context context ) {
        values.remove();
    }




    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
        return new Provider<T>() {
            public T get() {
                final Map<Key<?>, Object> scopedObjects = getScopedObjectMap(key);

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
        final Map<Key<?>, Object> scopedObjects = values.get();
        if (scopedObjects == null)
            throw new OutOfScopeException("Cannot access " + key + " outside of a scoping block");

        return scopedObjects;
    }

    /**
     * Returns a provider that always throws exception complaining that the
     * object in question must be seeded before it can be injected.
     *
     * @return typed provider
     */
    @SuppressWarnings( { "unchecked" })
    public static <T> Provider<T> seededKeyProvider() {
        return (Provider<T>) SEEDED_KEY_PROVIDER;
    }
}