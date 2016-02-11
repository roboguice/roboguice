package roboguice;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import com.google.inject.Guice;
import com.google.inject.HierarchyTraversalFilter;
import com.google.inject.HierarchyTraversalFilterFactory;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.internal.util.Stopwatch;
import com.google.inject.util.Modules;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import roboguice.config.DefaultRoboModule;
import roboguice.config.RoboGuiceHierarchyTraversalFilter;
import roboguice.inject.ContextScope;
import roboguice.inject.ContextScopedRoboInjector;
import roboguice.util.Strings;

/**
 * Manages injectors for RoboGuice applications.
 *
 * There are two types of injectors:
 *
 * 1. The base application injector, which is not typically used directly by the user.
 * 2. The ContextScopedInjector, which is obtained by calling {@link #getInjector(android.content.Context)}, and knows about
 * your current context, whether it's an activity, service, or something else.
 *
 * BUG hashmap should also key off of stage and modules list
 * TODO add documentation about annotation processing system
 */
public final class RoboGuice {
    @edu.umd.cs.findbugs.annotations.SuppressWarnings("MS_SHOULD_BE_FINAL")
    @SuppressWarnings({"checkstyle:visibilitymodifier", "checkstyle:staticvariablename"})
    public static Stage DEFAULT_STAGE = Stage.PRODUCTION;

    private static Injector injector;

    /** Enables or disables using annotation databases to optimize roboguice. Used for testing. Enabled by default. */
    private static boolean useAnnotationDatabases = true;

    //both maps are used together, we only synchronize on the first one.
    //TODO create data structure to hold injector + scoped objects --> single map.
    private static Map<Context, ContextScopedRoboInjector> mapContextToInjector = new IdentityHashMap<Context, ContextScopedRoboInjector>();
    private static ContextScope contextScope;

    //used for testing
    static {
        String useAnnotationsEnvVar = System.getenv("roboguice.useAnnotationDatabases");
        if (useAnnotationsEnvVar != null) {
            useAnnotationDatabases = Boolean.parseBoolean(useAnnotationsEnvVar);
        }
    }

    private RoboGuice() {
    }

    /**
     * Return the cached Injector instance for this application, or create a new one if necessary.
     */
    public static Injector getOrCreateBaseApplicationInjector(Application application) {
        if (injector != null) {
            return injector;
        }

        synchronized (RoboGuice.class) {
            if (injector != null) {
                return injector;
            }

            return getOrCreateBaseApplicationInjector(application, DEFAULT_STAGE);
        }
    }

    /**
     * Return the cached Injector instance for this application, or create a new one if necessary.
     * If specifying your own modules, you must include a DefaultRoboModule for most things to work properly.
     * Do something like the following:
     *
     * RoboGuice.setApplicationInjector( app, RoboGuice.DEFAULT_STAGE, Modules.override(RoboGuice.newDefaultRoboModule(app)).with(new MyModule() );
     *
     * @see com.google.inject.util.Modules#override(com.google.inject.Module...)
     * @see roboguice.RoboGuice#createBaseApplicationInjector(android.app.Application, com.google.inject.Stage, String[], com.google.inject.Module...)
     * @see roboguice.RoboGuice#newDefaultRoboModule(android.app.Application)
     * @see roboguice.RoboGuice#DEFAULT_STAGE
     *
     * If using this method with test cases, be sure to call {@link roboguice.RoboGuice.Util#reset()} in your test teardown methods
     * to avoid polluting our other tests with your custom injector.  Don't do this in your real application though.
     * <b>One of RoboGuice's entry points</b>.
     */
    public static Injector getOrCreateBaseApplicationInjector(final Application application, Stage stage, Module... modules) {
        final Stopwatch stopwatch = new Stopwatch();
        synchronized (RoboGuice.class) {
            initializeAnnotationDatabaseFinderAndHierarchyTraversalFilterFactory(application);
            return createGuiceInjector(stage, stopwatch, modules);
        }
    }

    /**
     * Shortcut to obtain an injector during tests. It will load all modules declared in manifest, add a {@code DefaultRoboModule},
     * and override all bindings defined in test modules. We use default stage by default.
     *
     * RoboGuice.overrideApplicationInjector( app, new TestModule() );
     *
     * @see com.google.inject.util.Modules#override(com.google.inject.Module...)
     * @see roboguice.RoboGuice#getOrCreateBaseApplicationInjector(android.app.Application, com.google.inject.Stage, com.google.inject.Module...)
     * @see roboguice.RoboGuice#newDefaultRoboModule(android.app.Application)
     * @see roboguice.RoboGuice#DEFAULT_STAGE
     *
     * If using this method with test cases, be sure to call {@link roboguice.RoboGuice.Util#reset()} in your test teardown methods
     * to avoid polluting our other tests with your custom injector.  Don't do this in your real application though.
     */
    public static Injector overrideApplicationInjector(final Application application, Module... overrideModules) {
        synchronized (RoboGuice.class) {
            initializeAnnotationDatabaseFinderAndHierarchyTraversalFilterFactory(application);
            final List<Module> baseModules = extractModulesFromManifest(application);
            return createGuiceInjector(DEFAULT_STAGE, null, Modules.override(baseModules).with(overrideModules));
        }
    }

    /**
     * Return the cached Injector instance for this application, or create a new one if necessary.
     * <b>One of RoboGuice's entry points</b>.
     */
    public static Injector getOrCreateBaseApplicationInjector(Application application, Stage stage) {
        final Stopwatch stopwatch = new Stopwatch();

        synchronized (RoboGuice.class) {
            initializeAnnotationDatabaseFinderAndHierarchyTraversalFilterFactory(application);
            final List<Module> modules = extractModulesFromManifest(application);
            return createGuiceInjector(stage, stopwatch, modules.toArray(new Module[modules.size()]));
        }
    }

    private static List<Module> extractModulesFromManifest(Application application) {
        try {
            final ArrayList<Module> modules = new ArrayList<Module>();

            final ApplicationInfo ai = application.getPackageManager().getApplicationInfo(application.getPackageName(), PackageManager.GET_META_DATA);
            final Bundle bundle = ai.metaData;
            final String roboguiceModules = bundle != null ? bundle.getString("roboguice.modules") : null;
            final DefaultRoboModule defaultRoboModule = newDefaultRoboModule(application);
            final String[] moduleNames = roboguiceModules != null ? roboguiceModules.split("[\\s,]") : new String[] {};

            modules.add(defaultRoboModule);

            for (String name : moduleNames) {
                if (Strings.notEmpty(name)) {
                    final Class<? extends Module> clazz = Class.forName(name).asSubclass(Module.class);
                    try {
                        modules.add(clazz.getDeclaredConstructor(Application.class).newInstance(application));
                    } catch (NoSuchMethodException ignored) {
                        modules.add(clazz.newInstance());
                    }
                }
            }
            return modules;
        } catch (Exception e) {
            throw new RuntimeException("Unable to instantiate your Module.  Check your roboguice.modules metadata in your AndroidManifest.xml", e);
        }
    }

    private static Injector createGuiceInjector(Stage stage, final Stopwatch stopwatch, Module... modules) {
        try {
            synchronized (RoboGuice.class) {
                injector = Guice.createInjector(stage, modules);
                contextScope = injector.getInstance(ContextScope.class);
                return injector;
            }
        } finally {
            if (stopwatch != null) {
                stopwatch.resetAndLog("BaseApplicationInjector creation");
            }
        }
    }

    public static ContextScopedRoboInjector getInjector(Context context) {
        ContextScopedRoboInjector contextScopedRoboInjector = mapContextToInjector.get(context);
        if (contextScopedRoboInjector != null) {
            return contextScopedRoboInjector;
        }
        synchronized (mapContextToInjector) {
            contextScopedRoboInjector = mapContextToInjector.get(context);
            if (contextScopedRoboInjector != null) {
                return contextScopedRoboInjector;
            }


            final Application application = (Application) context.getApplicationContext();
            final HashMap<Key<?>, Object> scopedObjects = new HashMap<Key<?>, Object>();
            final ContextScopedRoboInjector newContextScopedRoboInjector = new ContextScopedRoboInjector(context, getOrCreateBaseApplicationInjector(application), contextScope, scopedObjects);
            mapContextToInjector.put(context, newContextScopedRoboInjector);
            return newContextScopedRoboInjector;
        }
    }

    public static void destroyInjector(Context context) {
        synchronized (mapContextToInjector) {
            mapContextToInjector.remove(context);
        }
    }

    public static DefaultRoboModule newDefaultRoboModule(final Application application) {
        return new DefaultRoboModule(application, new ContextScope(application));
    }

    public static void setUseAnnotationDatabases(boolean useAnnotationDatabases) {
        RoboGuice.useAnnotationDatabases = useAnnotationDatabases;
    }

    private static void initializeAnnotationDatabaseFinderAndHierarchyTraversalFilterFactory(Application application) {
        if (useAnnotationDatabases) {
            Log.d(RoboGuice.class.getName(), "Using annotation database(s).");
            long start = SystemClock.currentThreadTimeMillis();

            try {
                Set<String> packageNameList = new HashSet<String>();

                try {
                    ApplicationInfo ai = application.getPackageManager().getApplicationInfo(application.getPackageName(), PackageManager.GET_META_DATA);
                    final Bundle bundle = ai.metaData;
                    final String roboguicePackages = bundle != null ? bundle.getString("roboguice.annotations.packages") : null;
                    if (roboguicePackages != null) {
                        for (String packageName : roboguicePackages.split("[\\s,]")) {
                            packageNameList.add(packageName);
                        }
                    }
                } catch (NameNotFoundException e) {
                    //if no packages are found in manifest, just log
                    Log.d(RoboGuice.class.getName(), "Failed to read manifest properly.");
                    e.printStackTrace();
                }

                if (packageNameList.isEmpty()) {
                    //add default package if none is specified
                    packageNameList.add("");
                }
                packageNameList.add("roboguice");
                Log.d(RoboGuice.class.getName(), "Using annotation database(s) : " + packageNameList.toString());

                final String[] packageNames = new String[packageNameList.size()];
                packageNameList.toArray(packageNames);

                Guice.setAnnotationDatabasePackageNames(packageNames);
                long end = SystemClock.currentThreadTimeMillis();
                Log.d(RoboGuice.class.getName(), "Time spent loading annotation databases : " + (end - start));
            } catch (Exception ex) {
                throw new IllegalStateException("Unable to use annotation database(s)", ex);
            }
        } else {
            Log.d(RoboGuice.class.getName(), "Using full reflection. Try using RoboGuice annotation processor for better performance.");
            Guice.setHierarchyTraversalFilterFactory(new HierarchyTraversalFilterFactory() {
                @Override
                public HierarchyTraversalFilter createHierarchyTraversalFilter() {
                    return new RoboGuiceHierarchyTraversalFilter();
                }
            });
        }
    }

    public static final class Util {
        private Util() {}

        /**
         * This method is provided to reset RoboGuice in testcases.
         * It should not be called in a real application.
         */
        public static void reset() {
            injector = null;
            //clear annotation database finder
            //restore hierarchy filter
            Guice.setAnnotationDatabasePackageNames(null);
            Guice.setHierarchyTraversalFilterFactory(new HierarchyTraversalFilterFactory());
        }
    }
}
