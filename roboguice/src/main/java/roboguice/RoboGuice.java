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
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.internal.util.Stopwatch;
import com.google.inject.util.Modules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import roboguice.config.DefaultRoboModule;
import roboguice.config.RoboGuiceHierarchyTraversalFilter;
import roboguice.inject.ContextScope;
import roboguice.inject.ContextScopedRoboInjector;

/**
 * Manages injectors for RoboGuice applications.
 * <p/>
 * There are two types of injectors:
 * <p/>
 * 1. The base application injector, which is not typically used directly by the user.
 * 2. The ContextScopedInjector, which is obtained by calling {@link #getInjector(android.content.Context)}, and knows about
 * your current context, whether it's an activity, service, or something else.
 * <p/>
 * BUG hashmap should also key off of stage and modules list
 * TODO add documentation about annotation processing system
 */
public final class RoboGuice {
    @edu.umd.cs.findbugs.annotations.SuppressWarnings("MS_SHOULD_BE_FINAL")
    @SuppressWarnings({"checkstyle:visibilitymodifier", "checkstyle:staticvariablename"})
    public static Stage DEFAULT_STAGE = Stage.PRODUCTION;

    private static Injector injector;

    /**
     * Enables or disables using annotation databases to optimize roboguice. Used for testing. Enabled by default.
     */
    private static boolean useAnnotationDatabases = true;

    //both maps are used together, we only synchronize on the first one.
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
     * Setups the base injector for the application.
     * Here we are indeed configuring the Guice injector. This injector
     * will never be accessible directly via RG. It would not work in RG style APIs either :
     * it would not handle properly the application scope and creates a deadlock when used concurrently.
     * This is known Guice bug : https://github.com/google/guice/issues/785
     * RG doesn't have this bug as we strongly synchronize on scopes.
     * @param application the Android application to configure the injector for.
     * @param modules a list of modules to use for setting up the injector.
     * If the provided list is null, we try to get the list of modules from the manifest.
     * Otherwise, this list takes precedence and the manifest is not used.
     * To define modules in the manifest use the AndroidManifest tag :
     * <pre>
     *   <meta-data android:name="roboguice.modules" android:value="comma separated list of module classes"/>
     * </pre>
     */
    public static void setupBaseApplicationInjector(Application application, Stage stage, Module... modules) {
        if (injector != null) {
            throw new IllegalStateException("Injector already setup.");
        }
        synchronized (RoboGuice.class) {
            if (injector != null) {
                throw new IllegalStateException("Injector already setup.");
            }

            createBaseApplicationInjector(application, stage, modules);
        }
    }

    /**
     * @see #setupBaseApplicationInjector(Application, Stage, Module...)
     * @see #DEFAULT_STAGE
     */
    public static void setupBaseApplicationInjector(Application application, Module... modules) {
        setupBaseApplicationInjector(application, DEFAULT_STAGE, modules);
    }


    /**
     * Return the cached Injector instance for this application, or create a new one if necessary.
     * <b>One of RoboGuice's entry points</b>.
     */
    private static Injector createBaseApplicationInjector(Application application, Stage stage, Module... modules) {
        final Stopwatch stopwatch = null;

        synchronized (RoboGuice.class) {
            initializeAnnotationDatabaseFinderAndHierarchyTraversalFilterFactory(application);

            if (modules == null || modules.length == 0) {
                System.out.println("Using modules from Manifest");
                final List<Module> moduleList = extractModulesFromManifest(application);
                modules = moduleList.toArray(new Module[moduleList.size()]);
            } else {
                System.out.println("Creating base injector using modules " + modules.length);
            }
            return createGuiceInjector(stage, stopwatch, modules);
        }
    }

    /**
     * Shortcut to obtain an injector during tests. It will load all modules declared in manifest, add a {@code DefaultRoboModule},
     * and override all bindings defined in test modules. We use default stage by default.
     * <p/>
     * RoboGuice.overrideApplicationInjector( app, new TestModule() );
     *
     * @see com.google.inject.util.Modules#override(com.google.inject.Module...)
     * @see roboguice.RoboGuice#newDefaultRoboModule(android.app.Application)
     * @see roboguice.RoboGuice#DEFAULT_STAGE
     * <p/>
     * If using this method with test cases, be sure to call {@link roboguice.RoboGuice.Util#reset()} in your test teardown methods
     * to avoid polluting our other tests with your custom injector.  Don't do this in your real application though.
     */
    public static void overrideApplicationInjector(final Application application, Module... overrideModules) {
        overrideApplicationInjector(application, DEFAULT_STAGE, overrideModules);
    }

    /**
     * Overrides the modules found in the manifest with the overrideModules.
     * @param application
     * @param stage
     * @param overrideModules
     */
    public static void overrideApplicationInjector(final Application application, Stage stage, Module... overrideModules) {
        synchronized (RoboGuice.class) {
            initializeAnnotationDatabaseFinderAndHierarchyTraversalFilterFactory(application);
            final List<Module> baseModules = extractModulesFromManifest(application);
            createGuiceInjector(stage, null, Modules.override(baseModules).with(overrideModules));
        }
    }

    /**
     * Creates the internal guice injector per say. It should never be exposed.
     * Sets the internal state of RG with the injector ready.
     * @param stage
     * @param stopwatch
     * @param modules
     * @return
     */
    private static Injector createGuiceInjector(Stage stage, final Stopwatch stopwatch, Module... modules) {
        try {
            injector = Guice.createInjector(stage, modules);
            contextScope = injector.getInstance(ContextScope.class);
            return injector;
        } finally {
            if (stopwatch != null) {
                stopwatch.resetAndLog("BaseApplicationInjector creation");
            }
        }
    }


    private static List<Module> extractModulesFromManifest(Application application) {
        try {
            final ArrayList<Module> modules = new ArrayList<Module>();

            final ApplicationInfo ai = application.getPackageManager().getApplicationInfo(application.getPackageName(), PackageManager.GET_META_DATA);
            final Bundle bundle = ai.metaData;
            final String roboguiceModules = bundle != null ? bundle.getString("roboguice.modules") : null;
            final DefaultRoboModule defaultRoboModule = newDefaultRoboModule(application);
            final String[] moduleNames = roboguiceModules != null ? roboguiceModules.split("[\\s,]") : new String[]{};

            modules.add(defaultRoboModule);

            for (String name : moduleNames) {
                if (name != null && !"".equals(name)) {
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

    public static ContextScopedRoboInjector getInjector(Context context) {
        return getInjector(context, null);
    }

    public static ContextScopedRoboInjector getInjector(Context context, Iterable<? extends Module> modules) {
        ContextScopedRoboInjector contextScopedRoboInjector = mapContextToInjector.get(context);
        if (injector == null) {
            throw new IllegalStateException("No base injector in RG. Please use RoboGuice.setupBaseInjector");
        }
        if (contextScopedRoboInjector != null) {
            return contextScopedRoboInjector;
        }
        synchronized (mapContextToInjector) {
            contextScopedRoboInjector = mapContextToInjector.get(context);
            if (contextScopedRoboInjector != null) {
                return contextScopedRoboInjector;
            }

            final ContextScopedRoboInjector newContextScopedRoboInjector = new ContextScopedRoboInjector(context, injector, contextScope, modules);
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
        private Util() {
        }

        /**
         * This method is provided to reset RoboGuice in testcases.
         * It should not be called in a real application.
         */
        public static void reset() {
            injector = null;
            contextScope = null;
            mapContextToInjector = new IdentityHashMap<Context, ContextScopedRoboInjector>();
            //clear annotation database finder
            //restore hierarchy filter
            Guice.setAnnotationDatabasePackageNames(null);
            Guice.setHierarchyTraversalFilterFactory(new HierarchyTraversalFilterFactory());
        }
    }
}
