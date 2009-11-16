package roboguice.application;

import roboguice.inject.ContextScope;

import com.google.inject.Injector;

/**
 * Like GuiceApplication, but allows you to inject resource into the application
 * itself.
 *
 * Introduces additional cost to the app's startup time, so may not be desirable
 * in all cases.
 *
 * @author mike
 *
 */
public class GuiceInjectableApplication extends GuiceApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        final Injector i = getInjector();
        final ContextScope scope = i.getInstance(ContextScope.class);
        scope.enter(this);
        i.injectMembers(this);
    }

}
