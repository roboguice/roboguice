package roboguice;

import roboguice.config.AbstractRoboModule;
import roboguice.config.RoboModule;
import roboguice.inject.ContextScopedInjector;

import android.app.Application;
import android.content.Context;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

import java.util.ArrayList;
import java.util.WeakHashMap;

/**
 * BUG hashmap should also key off of stage and modules list
 */
public class RoboGuice {
    public static Stage DEFAULT_STAGE = Stage.PRODUCTION;

    protected static WeakHashMap<Application,Injector> injectors = new WeakHashMap<Application,Injector>();

    private RoboGuice() {
    }

    /**
     * Return the cached Injector instance for this application, or create a new one if necessary.
     */
    public static Injector getApplicationInjector(Application application) {
        Injector rtrn = injectors.get(application);
        if( rtrn!=null )
            return rtrn;

        synchronized (RoboGuice.class) {
            rtrn = injectors.get(application);
            if( rtrn!=null )
                return rtrn;
            
            return setApplicationInjector(application, DEFAULT_STAGE);
        }
    }

    /**
     * Return the cached Injector instance for this application, or create a new one if necessary.
     * If specifying your own modules, you must include a RoboModule for most things to work properly.
     */
    public static Injector setApplicationInjector(Application application, Stage stage, Module... modules) {

        synchronized (RoboGuice.class) {
            final Injector rtrn = Guice.createInjector(stage, modules);
            injectors.put(application,rtrn);
            return rtrn;
        }
    }

    /**
     * Return the cached Injector instance for this application, or create a new one if necessary.
     */
    public static Injector setApplicationInjector(Application application, Stage stage) {

        synchronized (RoboGuice.class) {
            final int id = application.getResources().getIdentifier("roboguice_modules", "array", application.getPackageName());
            final String[] moduleNames = id>0 ? application.getResources().getStringArray(id) : new String[]{};
            final ArrayList<Module> modules = new ArrayList<Module>();
            final RoboModule roboModule = new RoboModule(application);

            modules.add(roboModule);

            try {
                for (String name : moduleNames) {
                    final Class<? extends Module> clazz = Class.forName(name).asSubclass(Module.class);
                    modules.add( AbstractRoboModule.class.isAssignableFrom(clazz) ? clazz.getConstructor(RoboModule.class).newInstance(roboModule) : clazz.newInstance() );
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            final Injector rtrn = setApplicationInjector(application, stage, modules.toArray(new Module[modules.size()]));
            injectors.put(application,rtrn);
            return rtrn;
        }

    }


    public static ContextScopedInjector getInjector(Context context) {
        return new ContextScopedInjector(context, getApplicationInjector((Application)context.getApplicationContext()));
    }
}
