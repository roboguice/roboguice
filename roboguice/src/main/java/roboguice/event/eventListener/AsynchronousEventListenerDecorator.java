package roboguice.event.eventListener;

import roboguice.event.EventListener;
import roboguice.event.eventListener.factory.RunnableAsyncTaskAdaptorFactory;

/**
 * Event Listener Decorator class.  This decorator executes the event listener through the SafeAsyncTask functionality.
 *
 * @author John Ericksen
 */
public class AsynchronousEventListenerDecorator<T> implements EventListener<T>{

    protected EventListener<T> eventListener;
    protected RunnableAsyncTaskAdaptorFactory asyncTaskFactory;

    public AsynchronousEventListenerDecorator(EventListener<T> eventListener, RunnableAsyncTaskAdaptorFactory asyncTaskFactory) {
        this.eventListener = eventListener;
        this.asyncTaskFactory = asyncTaskFactory;
    }

    public void onEvent(T event) {
        asyncTaskFactory.build(event, eventListener).execute();
    }
}
