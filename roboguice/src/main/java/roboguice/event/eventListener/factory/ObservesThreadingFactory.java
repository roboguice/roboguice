package roboguice.event.eventListener.factory;

import roboguice.event.EventListener;
import roboguice.event.EventThread;

import com.google.inject.Inject;

/**
 * @author John Ericksen
 */
public class ObservesThreadingFactory {

    @Inject protected UIThreadEventListenerDecoratorFactory uiThreadFactory;
    @Inject protected AsynchronousEventListenerDecoratorFactory asyncFactory;

    public <T> EventListener<T> decorate(EventThread threadType, EventListener<T> eventListener){
        switch (threadType){
            case CURRENT:
                return eventListener;
            case UI:
                return uiThreadFactory.decorate(eventListener);
            case BACKGROUND:
                return asyncFactory.decorate(eventListener);
            default:
                return eventListener;
        }
    }
}
