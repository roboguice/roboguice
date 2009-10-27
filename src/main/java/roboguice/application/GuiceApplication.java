package roboguice.application;

import roboguice.config.AndroidModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

import android.app.Application;

public class GuiceApplication extends Application {
    protected Injector guice;

    public Injector getInjector() {
        return guice!=null ? guice : ( guice = Guice.createInjector( Stage.PRODUCTION, getModule() ) );
    }

    /**
     * Subclass AndroidModule and override this method to do your own custom bindings
     * @return
     */
    public Module getModule() {
        return new AndroidModule(this);
    }
}
