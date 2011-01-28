package roboguice.config;

import android.app.Application;

import com.google.inject.AbstractModule;

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
