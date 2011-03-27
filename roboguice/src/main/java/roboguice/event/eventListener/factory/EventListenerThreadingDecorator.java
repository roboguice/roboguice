package roboguice.event.eventListener.factory;

import roboguice.event.EventListener;
import roboguice.event.EventThread;
import roboguice.event.eventListener.AsynchronousEventListenerDecorator;
import roboguice.event.eventListener.UIThreadEventListenerDecorator;

import android.os.Handler;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * @author John Ericksen
 */
public class EventListenerThreadingDecorator {

    @Inject protected Provider<Handler> handlerProvider;

    public <T> EventListener<T> decorate(EventThread threadType, EventListener<T> eventListener){
        switch (threadType){
            case UI:
                return new UIThreadEventListenerDecorator<T>(eventListener, handlerProvider.get() );
            case BACKGROUND:
                return new AsynchronousEventListenerDecorator<T>(eventListener);
            default:
                return eventListener;
        }
    }
}
