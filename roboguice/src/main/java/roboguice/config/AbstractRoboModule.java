package roboguice.config;

import android.app.Application;

import com.google.inject.AbstractModule;

/**
 * An extension to guice's AbstractModule that gives the module access to
 * the RoboModule.
 *
 * In addition, it overrides {@link #requestStaticInjection(Class[])} to add support
 * for RoboGuice's resource and view injection when injecting static methods.
 */
public abstract class AbstractRoboModule extends AbstractModule {
    protected RoboModule roboModule;

    protected AbstractRoboModule(RoboModule roboModule) {
        this.roboModule = roboModule;
    }

    public Application getApplication() {
        return roboModule.application;
    }

    @Override
    protected void requestStaticInjection(Class<?>... types) {
        super.requestStaticInjection(types);
        roboModule.resourceListener.requestStaticInjection(types);
        roboModule.viewListener.requestStaticInjection(types); // BUG does it make sense to statically inject views?
    }
    

}
