package roboguice.event.eventListener.factory;

import com.google.inject.Inject;
import roboguice.event.EventListener;
import roboguice.event.eventListener.AsynchronousEventListenerDecorator;

/**
 * Factory for the AsynchronousEventListenerDecorator
 * 
 * @author John Ericksen
 */
public class AsynchronousEventListenerDecoratorFactory {

    @Inject
    protected RunnableAsyncTaskAdaptorFactory taskFactory;
    
    public <T> EventListener<T> buildDecorator(EventListener<T> eventListener) {
        return new AsynchronousEventListenerDecorator<T>(eventListener, taskFactory);
    }
}
