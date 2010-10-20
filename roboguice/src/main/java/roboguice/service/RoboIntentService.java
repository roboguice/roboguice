package roboguice.service;

import roboguice.application.RoboApplication;
import roboguice.inject.ContextScope;
import roboguice.inject.InjectorProvider;

import com.google.inject.Injector;

import android.app.IntentService;
import android.content.Intent;

/**
 * A {@link RoboIntentService} extends from {@link IntentService} to provide dynamic
 * injection of collaborators, using Google Guice.<br /> <br />
 * 
 * Your own services that usually extend from {@link IntentService} should now extend from
 * {@link RoboIntentService}.<br /> <br />
 *
 * If we didn't provide what you need, you have two options : either post an issue on <a
 * href="http://code.google.com/p/roboguice/issues/list">the bug tracker</a>, or
 * implement it yourself. Have a look at the source code of this class (
 * {@link RoboIntentService}), you won't have to write that much changes. And of
 * course, you are welcome to contribute and send your implementations to the
 * RoboGuice project.<br /> <br />
 *
 * You can have access to the Guice
 * {@link Injector} at any time, by calling {@link #getInjector()}.<br />
 *
 * However, you will not have access to Context scoped beans until
 * {@link #onCreate()} is called. <br /> <br />
 *
 * @author Donn Felker
 */
public abstract class RoboIntentService extends IntentService implements InjectorProvider {
	
   public RoboIntentService(String name) {
		super(name);
	}

   protected ContextScope scope;

   @Override
    public void onCreate() {
        final Injector injector = getInjector();
        scope = injector.getInstance(ContextScope.class);
        scope.enter(this);
        injector.injectMembers(this);
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        scope.enter(this);
        super.onStart(intent, startId);
    }


    @Override
    public void onDestroy() {
        scope.exit(this);
        super.onDestroy();
    }
    
    /**
     * @see roboguice.application.RoboApplication#getInjector() 
     */
    public Injector getInjector() {
        return ((RoboApplication) getApplication()).getInjector();
    }

	
}
