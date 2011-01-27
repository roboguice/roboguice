package roboguice;

import roboguice.config.RoboModule;

import android.app.Application;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

import java.util.ArrayList;
import java.util.WeakHashMap;

public class RoboGuice {
    protected static WeakHashMap<Application,Injector> injectors = new WeakHashMap<Application,Injector>();

    private RoboGuice() {
    }

    public static Injector getInjector( Application context) {
        return getInjector(Stage.PRODUCTION, context);
    }
    
    public static Injector getInjector(Stage stage, Application context) {

        Injector rtrn = injectors.get(context);
        if( rtrn!=null )
            return rtrn;

        synchronized (RoboGuice.class) {
            rtrn = injectors.get(context);
            if( rtrn!=null )
                return rtrn;

            final int id = context.getResources().getIdentifier("roboguice_modules", "array", context.getPackageName());
            final String[] moduleNames = context.getResources().getStringArray(id);
            final ArrayList<Module> modules = new ArrayList<Module>();

            modules.add(new RoboModule(context));

            if (moduleNames != null) {
                try {
                    for (String name : moduleNames)
                        modules.add((Module) Class.forName(name).newInstance());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            rtrn = Guice.createInjector(stage, modules);
            injectors.put(context,rtrn);

        }

        return rtrn;
    }
}
