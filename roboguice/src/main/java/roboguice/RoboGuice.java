package roboguice;

import android.app.Application;
import android.content.Context;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import roboguice.config.DefaultRoboModule;
import roboguice.event.EventManager;
import roboguice.inject.*;

import java.util.ArrayList;
import java.util.WeakHashMap;

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
    protected static int modulesResourceId = 0;
    
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
     * If using this method with test cases, be sure to call {@link roboguice.RoboGuice.util#reset()} in your test teardown methods
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
     * Allows the user to override the "roboguice_modules" resource name with some other identifier.
     * This is a static value.
     */
    public static void setModulesResourceId(int modulesResourceId) {
        RoboGuice.modulesResourceId = modulesResourceId;
    }

    /**
     * Return the cached Injector instance for this application, or create a new one if necessary.
     */
    public static Injector setBaseApplicationInjector(Application application, Stage stage) {

        synchronized (RoboGuice.class) {
            int id = modulesResourceId;
            try {
                if (id == 0)
                    id = application.getResources().getIdentifier("roboguice_modules", "array", application.getPackageName());
            } catch( NullPointerException ignored ) {
                // ignored for robolectric 2.1.1, not sure why we're getting an NPE from getIdentifier
            }

            final String[] moduleNames = id>0 ? application.getResources().getStringArray(id) : new String[]{};
            final ArrayList<Module> modules = new ArrayList<Module>();
            final DefaultRoboModule defaultRoboModule = newDefaultRoboModule(application);

            modules.add(defaultRoboModule);

            try {
                for (String name : moduleNames) {
                    final Class<? extends Module> clazz = Class.forName(name).asSubclass(Module.class);

                    try {
                        modules.add(clazz.getDeclaredConstructor(Context.class).newInstance(application));
                    } catch( NoSuchMethodException ignored ) {
                        modules.add( clazz.newInstance() );
                    }

                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            final Injector rtrn = setBaseApplicationInjector(application, stage, modules.toArray(new Module[modules.size()]));
            injectors.put(application,rtrn);
            return rtrn;
        }

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

    public static void destroyInjector(Context context) {
        final RoboInjector injector = getInjector(context);
        injector.getInstance(EventManager.class).destroy();
        //noinspection SuspiciousMethodCalls
        injectors.remove(context); // it's okay, Context is an Application
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
