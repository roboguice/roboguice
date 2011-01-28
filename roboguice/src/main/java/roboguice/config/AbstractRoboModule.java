package roboguice.config;

import android.app.Application;

import com.google.inject.AbstractModule;

/**
 * An extension to guice's AbstractModule that gives the module access to
 * the Application instance.
 *
 * In addition, it overrides {@link #requestStaticInjection(Class[])} to add support
 * for RoboGuice's resource and view injection when injecting static methods.
 */
public abstract class AbstractRoboModule extends AbstractModule {
    protected RoboModule roboModule;
    protected Application application;

    protected AbstractRoboModule(Application application) {
        this.application = application;
    }

    public Application getApplication() {
        return application;
    }

    public RoboModule getRoboModule() {
        return roboModule;
    }

    public void setRoboModule( RoboModule roboModule ) {
        this.roboModule = roboModule;
    }

    @Override
    protected void requestStaticInjection(Class<?>... types) {
        super.requestStaticInjection(types);
        roboModule.resourceListener.requestStaticInjection(types);
        roboModule.viewListener.requestStaticInjection(types); // BUG does it make sense to statically inject views?
    }
    

}
