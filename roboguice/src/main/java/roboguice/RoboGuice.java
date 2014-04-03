package roboguice;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import roboguice.config.AnnotatedRoboGuiceHierarchyTraversalFilter;
import roboguice.config.AnnotationDatabaseFinder;
import roboguice.config.DefaultRoboModule;
import roboguice.config.RoboGuiceHierarchyTraversalFilter;
import roboguice.event.EventManager;
import roboguice.inject.ContextScope;
import roboguice.inject.ContextScopedRoboInjector;
import roboguice.inject.ResourceListener;
import roboguice.inject.RoboInjector;
import roboguice.inject.ViewListener;
import roboguice.util.Strings;

import com.google.inject.Guice;
import com.google.inject.HierarchyTraversalFilter;
import com.google.inject.HierarchyTraversalFilterFactory;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.internal.util.Stopwatch;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

/**
 *
 * Manages injectors for RoboGuice applications.
 *
 * There are two types of injectors:
 *
 * 1. The base application injector, which is not typically used directly by the user.
 * 2. The ContextScopedInjector, which is obtained by calling {@link #getInjector(android.content.Context)}, and knows about
 *    your current context, whether it's an activity, service, or something else.
 * 
 * BUG hashmap should also key off of stage and modules list
 */
public class RoboGuice {
    public static Stage DEFAULT_STAGE = Stage.PRODUCTION;

    protected static WeakHashMap<Application,Injector> injectors = new WeakHashMap<Application,Injector>();
    protected static WeakHashMap<Application,ResourceListener> resourceListeners = new WeakHashMap<Application, ResourceListener>();
    protected static WeakHashMap<Application,ViewListener> viewListeners = new WeakHashMap<Application, ViewListener>();


    /** Enables or disables using annotation databases to optimize roboguice. Used for testing. Enabled by default.*/
    public static boolean useAnnotationDatabases = true;

    private static AnnotationDatabaseFinder annotationDatabaseFinder; 

    private RoboGuice() {
    }

    /**
     * Return the cached Injector instance for this application, or create a new one if necessary.
     */
    public static Injector getBaseApplicationInjector(Application application) {
        Injector rtrn = injectors.get(application);
        if( rtrn!=null )
            return rtrn;

        rtrn = injectors.get(application);
        if( rtrn!=null )
            return rtrn;

        return createBaseApplicationInjector(application, DEFAULT_STAGE);
    }

    /**
     * Return the cached Injector instance for this application, or create a new one if necessary.
     * If specifying your own modules, you must include a DefaultRoboModule for most things to work properly.
     * Do something like the following:
     *
     * RoboGuice.setApplicationInjector( app, RoboGuice.DEFAULT_STAGE, Modules.override(RoboGuice.newDefaultRoboModule(app)).with(new MyModule() );
     *
     * @see com.google.inject.util.Modules#override(com.google.inject.Module...)
     * @see roboguice.RoboGuice#setBaseApplicationInjector(android.app.Application, com.google.inject.Stage, String[], com.google.inject.Module...)
     * @see roboguice.RoboGuice#newDefaultRoboModule(android.app.Application)
     * @see roboguice.RoboGuice#DEFAULT_STAGE
     *
     * If using this method with test cases, be sure to call {@link roboguice.RoboGuice.util#reset()} in your test teardown methods
     * to avoid polluting our other tests with your custom injector.  Don't do this in your real application though.
     * <b>One of RoboGuice's entry points</b>.
     */
    public static Injector createBaseApplicationInjector(final Application application, Stage stage, Module... modules ) {
        final Stopwatch stopwatch = new Stopwatch();
        try {
            synchronized (RoboGuice.class) {
                final Injector rtrn = Guice.createInjector(stage, modules);
                injectors.put(application,rtrn);
                return rtrn;
            }
        } finally {
            stopwatch.resetAndLog("BaseApplicationInjector creation");
        }
    }

    /**
     * Return the cached Injector instance for this application, or create a new one if necessary.
     * <b>One of RoboGuice's entry points</b>.
     */
    public static Injector createBaseApplicationInjector(Application application, Stage stage) {
        final ArrayList<Module> modules = new ArrayList<Module>();

        try {
            initializeAnnotationDatabaseFinderAndHierarchyTraversalFilterFactory(application);
            initializeModules(application, modules);
        } catch (Exception e) {
            throw new RuntimeException("Unable to instantiate your Module.  Check your roboguice.modules metadata in your AndroidManifest.xml",e);
        }

        final Injector rtrn = createBaseApplicationInjector(application, stage, modules.toArray(new Module[modules.size()]));
        injectors.put(application,rtrn);
        return rtrn;
    }

    public static RoboInjector getInjector(Context context) {
        final Application application = (Application)context.getApplicationContext();
        return new ContextScopedRoboInjector(context, getBaseApplicationInjector(application), getViewListener(application));
    }

    /**
     * A shortcut for RoboGuice.getInjector(context).injectMembers(o);
     */
    public static <T> T injectMembers( Context context, T t ) {
        getInjector(context).injectMembers(t);
        return t;
    }

    public static DefaultRoboModule newDefaultRoboModule(final Application application) {
        return new DefaultRoboModule(application, new ContextScope(application), getViewListener(application), getResourceListener(application));
    }

    @SuppressWarnings("ConstantConditions")
    protected static ResourceListener getResourceListener( Application application ) {
        ResourceListener resourceListener = resourceListeners.get(application);
        if( resourceListener==null ) {
            synchronized (RoboGuice.class) {
                if( resourceListener==null ) {
                    resourceListener = new ResourceListener(application);
                    resourceListeners.put(application,resourceListener);
                }
            }
        }
        return resourceListener;
    }

    @SuppressWarnings("ConstantConditions")
    protected static ViewListener getViewListener( final Application application ) {
        ViewListener viewListener = viewListeners.get(application);
        if( viewListener==null ) {
            synchronized (RoboGuice.class) {
                if( viewListener==null ) {
                    viewListener = new ViewListener();
                    viewListeners.put(application,viewListener);
                }
            }
        }
        return viewListener;
    }

    public static AnnotationDatabaseFinder getAnnotationDatabaseFinder() {
        return annotationDatabaseFinder;
    }

    public static void destroyInjector(Context context) {
        final RoboInjector injector = getInjector(context);
        injector.getInstance(EventManager.class).destroy();
        //noinspection SuspiciousMethodCalls
        injectors.remove(context); // it's okay, Context is an Application
    }
    
    private static void initializeAnnotationDatabaseFinderAndHierarchyTraversalFilterFactory(Application application) {
        if( useAnnotationDatabases ) {
            try {
                annotationDatabaseFinder = new AnnotationDatabaseFinder(application);
                final HashMap<String, Map<String, Set<String>>> mapAnnotationToMapClassWithInjectionNameToFieldSet = annotationDatabaseFinder.getMapAnnotationToMapClassWithInjectionNameToFieldSet();
                final HashMap<String, Map<String, Set<String>>> mapAnnotationToMapClassWithInjectionNameToMethodSet = annotationDatabaseFinder.getMapAnnotationToMapClassWithInjectionNameToMethodSet();
                final HashMap<String, Map<String, Set<String>>> mapAnnotationToMapClassWithInjectionNameToConstructorSet = annotationDatabaseFinder.getMapAnnotationToMapClassWithInjectionNameToConstructorSet();
                Guice.setHierarchyTraversalFilterFactory(new HierarchyTraversalFilterFactory() {
                    @Override
                    public HierarchyTraversalFilter createHierarchyTraversalFilter() {
                        return new AnnotatedRoboGuiceHierarchyTraversalFilter(mapAnnotationToMapClassWithInjectionNameToFieldSet, mapAnnotationToMapClassWithInjectionNameToMethodSet, mapAnnotationToMapClassWithInjectionNameToConstructorSet);
                    }
                });
            } catch( Exception ex ) {
                throw new IllegalStateException("Unable use Annotation Database(s)", ex);
            }
        } else {
            Guice.setHierarchyTraversalFilterFactory(new HierarchyTraversalFilterFactory() {
                @Override
                public HierarchyTraversalFilter createHierarchyTraversalFilter() {
                    return new RoboGuiceHierarchyTraversalFilter();
                }
            });
        }
    }

    private static void initializeModules(Application application, final ArrayList<Module> modules) throws NameNotFoundException, ClassNotFoundException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        
        final ApplicationInfo ai = application.getPackageManager().getApplicationInfo(application.getPackageName(), PackageManager.GET_META_DATA);
        final Bundle bundle = ai.metaData;
        final String roboguiceModules = bundle!=null ? bundle.getString("roboguice.modules") : null;
        final DefaultRoboModule defaultRoboModule = newDefaultRoboModule(application);
        final String[] moduleNames = roboguiceModules!=null ? roboguiceModules.split("[\\s,]") : new String[]{};

        modules.add(defaultRoboModule);

        for (String name : moduleNames) {
            if( Strings.notEmpty(name)) {
                final Class<? extends Module> clazz = Class.forName(name).asSubclass(Module.class);
                try {
                    modules.add(clazz.getDeclaredConstructor(Context.class).newInstance(application));
                } catch( NoSuchMethodException ignored ) {
                    modules.add( clazz.newInstance() );
                }
            }
        }
    }

    public static class util {
        private util() {}

        /**
         * This method is provided to reset RoboGuice in testcases.
         * It should not be called in a real application.
         */
        public static void reset() {
            injectors.clear();
            resourceListeners.clear();
            viewListeners.clear();
        }
    }

}
