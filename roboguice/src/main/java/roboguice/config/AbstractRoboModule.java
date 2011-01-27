package roboguice.config;

import com.google.inject.AbstractModule;

public abstract class AbstractRoboModule extends AbstractModule {
    protected RoboModule roboModule;

    @Override
    protected void requestStaticInjection(Class<?>... types) {
        super.requestStaticInjection(types);
        roboModule.resourceListener.requestStaticInjection(types);
        roboModule.viewListener.requestStaticInjection(types); // BUG does it make sense to statically inject views?
    }

    public void setRoboModule( RoboModule roboModule ) {
        this.roboModule = roboModule;
    }

}
