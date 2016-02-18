package roboguice.inject;

import android.content.Context;
import com.google.inject.Key;
import roboguice.RoboGuice;
import roboguice.util.Ln;

/**
 * Lazily injects a dependency.
 * See Dagger for doc ;)
 *
 * It also has the advantage on Android to limit recursion
 * depth and limit stack over flows when the injection graph
 * is too deep for a limited stack. (It happens on some older devices in non activity contexts).
 *
 * This class is abstract so that it must be derived to be used.
 * It should be derived anonymously in a class that extends Context:
 * <code>private Lazy<A> foo = new Lazy<A>(this) {};</code>
 *
 * Warning : the context you pass should <b>not</b> be null. This means
 * that if you are outside a context, you can't do both :
 * <ul>
 *  <li>@Inject Context context</li>
 *  <li>Lazy&lt;A&gt; lazy = Lazy&lt;A&gt;(context) {};</li>
 * </ul>
 * Because the context will be null at the lazy creation time.
 * In that case, you will have to create an @Inject annotated method that will
 * be executed right after all fields get injected, then you can you the context
 * to create a Lazy (actually you can declare the lazy as a field and initialize it
 * in this method), like this :
 * <tt>
   @Inject
   public void init() {
     lazy = Lazy<A>(context) {};
   }
 * </tt>
 *
 * @param <T> the type to lazily inject.
 */
public class Lazy<T> {
    /** Used to debug startup and check that no lazy deps are created eagerly before GRP24. */
    private static boolean verboseLogging = false;

    private Context context;
    private T instance;
    private Class<T> type;

    public Lazy(Class type, Context context) {
        this.type = type;
        this.context = context;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "DC_DOUBLECHECK")
    public T get() {
        if (instance != null) {
            return instance;
        }
        synchronized (ContextScope.class) {
            if (instance != null) {
                return instance;
            }

            ContextScope scope = null;
            try {
                final ContextScopedRoboInjector injector = RoboGuice.getInjector(context);
                scope = injector.getContextScope();
                scope.enter(context, injector.getScopedObjects());
                //this is very hacky,
                //but in this case it has no side-effect
                //http://stackoverflow.com/q/1901164/693752

                if (verboseLogging) {
                    Ln.d("Creating lazy instance of type: %s", type.toString());
                    try {
                        throw new RuntimeException();
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                }
                //here we need to create the instance dynamically, otherwise guice must
                //scan T eagerly, which causes too long recursions, and stack over flows
                instance = (T) injector.getInstance(Key.get(type));
                return instance;
            } finally {
                scope.exit(context);
            }
        }
    }

    public static boolean isVerboseLoggingEnabled() {
        return verboseLogging;
    }

    public static void setVerboseLoggingEnabled(boolean verboseLoggingEnabled) {
        verboseLogging = verboseLoggingEnabled;
    }
}
