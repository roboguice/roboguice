package roboguice;

import roboguice.config.AbstractRoboModule;
import roboguice.config.RoboModule;

import android.app.Application;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.WeakHashMap;

/**
 * BUG hashmap should also key off of stage
 */
public class RoboGuice {
    protected static WeakHashMap<Application,Injector> injectors = new WeakHashMap<Application,Injector>();
    protected static Stage DEFAULT_STAGE = Stage.PRODUCTION;

    private RoboGuice() {
    }

    public static Injector getInjector( Application context) {
        return getInjector(DEFAULT_STAGE, context);
    }

    public static Injector getInjector( Application application, Module... modules ) {
        return getInjector( DEFAULT_STAGE, application, modules );
    }

    public static Injector getInjector( Stage stage, Application application, Module... modules ) {

        Injector rtrn = injectors.get(application);
        if( rtrn!=null )
            return rtrn;

        synchronized (RoboGuice.class) {
            rtrn = injectors.get(application);
            if( rtrn!=null )
                return rtrn;

            rtrn = Guice.createInjector(stage, modules);
            injectors.put(application,rtrn);

        }

        return rtrn;
    }



    public static Injector getInjector(Stage stage, Application application) {

        Injector rtrn = injectors.get(application);
        if( rtrn!=null )
            return rtrn;

        synchronized (RoboGuice.class) {
            rtrn = injectors.get(application);
            if( rtrn!=null )
                return rtrn;

            final int id = application.getResources().getIdentifier("roboguice_modules", "array", application.getPackageName());
            final String[] moduleNames = application.getResources().getStringArray(id);
            final ArrayList<Module> modules = new ArrayList<Module>();
            final RoboModule roboModule = new RoboModule(application);

            modules.add(roboModule);

            if (moduleNames != null) {
                try {
                    for (String name : moduleNames) {
                        final Class<? extends Module> clazz = Class.forName(name).asSubclass(Module.class);
                        modules.add( AbstractRoboModule.class.isAssignableFrom(clazz) ? clazz.getConstructor(RoboModule.class).newInstance(roboModule) : clazz.newInstance() );
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            rtrn = getInjector(stage,application,modules.toArray(new Module[modules.size()]));
            injectors.put(application,rtrn);

        }

        return rtrn;
    }
}
