package roboguice.config;

import roboguice.event.EventManager;
import roboguice.event.ObservesTypeListener;

import android.content.Context;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.matcher.Matchers;

/**
 * @author John Ericksen
 */
public class EventManagerModule extends AbstractModule {

    protected EventManager eventManager;
    protected Provider<Context> contextProvider;

    public EventManagerModule(EventManager eventManager, Provider<Context> contextProvider) {
        this.eventManager = eventManager;
        this.contextProvider = contextProvider;
    }

    @Override
    protected void configure() {

        // Context observers
        bind(EventManager.class).toInstance(eventManager);
        bindListener(Matchers.any(), new ObservesTypeListener(contextProvider, eventManager));
        requestInjection(eventManager);
    }
}
