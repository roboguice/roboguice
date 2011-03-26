package roboguice.event.eventListener.factory;

import roboguice.event.EventListener;
import roboguice.event.eventListener.AsynchronousEventListenerDecorator;

import com.google.inject.Inject;

/**
 * Factory for the AsynchronousEventListenerDecorator
 * 
 * @author John Ericksen
 */
public class AsynchronousEventListenerDecoratorFactory {

    @Inject protected RunnableAsyncTaskAdaptorFactory taskFactory;
    
    public <T> EventListener<T> decorate(EventListener<T> eventListener) {
        return new AsynchronousEventListenerDecorator<T>(eventListener, taskFactory);
    }
}
