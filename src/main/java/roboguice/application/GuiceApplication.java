package roboguice.application;

import roboguice.inject.ContextScope;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

import android.app.Application;

public class GuiceApplication extends Application implements Module {
    protected Injector guice;

    @Override
    public void onCreate() {
        super.onCreate();
        final Injector i = getInjector();
        final ContextScope scope = i.getInstance(ContextScope.class);
        scope.enter(this);
        i.injectMembers(this);
    }

    public Injector getInjector() {
        return guice!=null ? guice : ( guice = Guice.createInjector( Stage.PRODUCTION, this ) );
    }

    /**
     * Override to configure your own appliation
     */
    public void configure(Binder binder) {
    }
}
