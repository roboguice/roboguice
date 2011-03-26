package roboguice.event.eventListener.factory;

import roboguice.event.EventListener;
import roboguice.event.eventListener.UIThreadEventListenerDecorator;

import android.os.Handler;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Factory for the UIThreadEventListenerDecorator.
 *
 * @author John Ericksen
 */
public class UIThreadEventListenerDecoratorFactory {

    @Inject protected Provider<Handler> handlerProvider;
    @Inject protected EventFireRunnableFactory eventFireFactory;

    public <T> EventListener<T> decorate(EventListener<T> eventListener) {
        return new UIThreadEventListenerDecorator<T>(eventListener, handlerProvider.get(), eventFireFactory);
    }
}
