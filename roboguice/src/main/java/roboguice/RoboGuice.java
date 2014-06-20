package roboguice;

import java.util.ArrayList;
import java.util.WeakHashMap;

import roboguice.config.DefaultRoboModule;
import roboguice.event.EventManager;
import roboguice.inject.ContextScope;
import roboguice.inject.ContextScopedRoboInjector;
import roboguice.inject.ResourceListener;
import roboguice.inject.RoboInjector;
import roboguice.inject.ViewListener;
import roboguice.util.Strings;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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
public final class RoboGuice {
    @edu.umd.cs.findbugs.annotations.SuppressWarnings("MS_SHOULD_BE_FINAL")
    @SuppressWarnings({"checkstyle:visibilitymodifier","checkstyle:staticvariablename"})
    public static Stage DEFAULT_STAGE = Stage.PRODUCTION;

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="MS_SHOULD_BE_FINAL")
    protected static WeakHashMap<Application,Injector> injectors = new WeakHashMap<Application,Injector>();
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="MS_SHOULD_BE_FINAL")
    protected static WeakHashMap<Application,ResourceListener> resourceListeners = new WeakHashMap<Application, ResourceListener>();
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="MS_SHOULD_BE_FINAL")
    protected static WeakHashMap<Application,ViewListener> viewListeners = new WeakHashMap<Application, ViewListener>();

    private RoboGuice() {
    }

    /**
     * Return the cached Injector instance for this application, or create a new one if necessary.
     */
    public static Injector getBaseApplicationInjector(Application application) {
        Injector rtrn = injectors.get(application);
        if( rtrn!=null )
            return rtrn;

        synchronized (RoboGuice.class) {
            rtrn = injectors.get(application);
            if( rtrn!=null )
                return rtrn;
            
            return setBaseApplicationInjector(application, DEFAULT_STAGE);
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
     * @see roboguice.RoboGuice#setBaseApplicationInjector(android.app.Application, com.google.inject.Stage, com.google.inject.Module...)
     * @see roboguice.RoboGuice#newDefaultRoboModule(android.app.Application)
     * @see roboguice.RoboGuice#DEFAULT_STAGE
     *
     * If using this method with test cases, be sure to call {@link roboguice.RoboGuice.Util#reset()} in your test teardown methods
     * to avoid polluting our other tests with your custom injector.  Don't do this in your real application though.
     *
     */
    public static Injector setBaseApplicationInjector(final Application application, Stage stage, Module... modules) {
        synchronized (RoboGuice.class) {
            final Injector rtrn = Guice.createInjector(stage, modules);
            injectors.put(application,rtrn);
            return rtrn;
        }
    }

    /**
     * Return the cached Injector instance for this application, or create a new one if necessary.
     */
    public static Injector setBaseApplicationInjector(Application application, Stage stage) {

        synchronized (RoboGuice.class) {

            final ArrayList<Module> modules = new ArrayList<Module>();

            try {
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
                            modules.add(clazz.getDeclaredConstructor(Application.class).newInstance(application));
                        } catch( NoSuchMethodException ignored ) {
                            modules.add( clazz.newInstance() );
                        }
                    }
                }

            } catch (Exception e) {
                throw new RuntimeException("Unable to instantiate your Module.  Check your roboguice.modules metadata in your AndroidManifest.xml",e);
            }

            final Injector rtrn = setBaseApplicationInjector(application, stage, modules.toArray(new Module[modules.size()]));
            injectors.put(application,rtrn);
            return rtrn;
        }

    }


    public static RoboInjector getInjector(Context context) {
        final Application application = (Application)context.getApplicationContext();
        return new ContextScopedRoboInjector(context, getBaseApplicationInjector(application));
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
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="NP_LOAD_OF_KNOWN_NULL_VALUE", justification="Double check lock")
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
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="NP_LOAD_OF_KNOWN_NULL_VALUE", justification="Double check lock")
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

    public static void destroyInjector(Context context) {
        final RoboInjector injector = getInjector(context);
        injector.getInstance(EventManager.class).destroy();
        //noinspection SuspiciousMethodCalls
        injectors.remove(context); // it's okay, Context is an Application
    }

    public static final class Util {
        private Util() {}

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
