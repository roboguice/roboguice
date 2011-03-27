package roboguice.config;

import roboguice.event.EventManager;
import roboguice.event.ObservesTypeListener;
import roboguice.event.eventListener.factory.EventListenerThreadingDecorator;

import android.content.Context;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.matcher.Matchers;

/**
 * Guice module configuring the Observes and EventManager functionality.
 *
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

        final EventListenerThreadingDecorator observerThreadingDecorator = new EventListenerThreadingDecorator();
        
        bind(EventManager.class).toInstance(eventManager);
        bind(EventListenerThreadingDecorator.class).toInstance(observerThreadingDecorator);

        bindListener(Matchers.any(), new ObservesTypeListener(contextProvider, eventManager, observerThreadingDecorator));
        
        requestInjection(observerThreadingDecorator);
        requestInjection(eventManager);
    }
}
