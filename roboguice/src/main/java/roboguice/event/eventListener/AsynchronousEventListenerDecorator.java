package roboguice.event.eventListener;

import roboguice.event.EventListener;

import android.os.Handler;

/**
 * Event Listener Decorator class.  This decorator executes the event listener through the SafeAsyncTask functionality.
 *
 * @author John Ericksen
 */
public class AsynchronousEventListenerDecorator<T> implements EventListener<T>{

    protected EventListener<T> eventListener;
    protected Handler handler;

    public AsynchronousEventListenerDecorator(EventListener<T> eventListener) {
        this.eventListener = eventListener;
    }

    public AsynchronousEventListenerDecorator(Handler handler, EventListener<T> eventListener) {
        this.handler = handler;
        this.eventListener = eventListener;
    }

    public void onEvent(T event) {
        new RunnableAsyncTaskAdaptor(handler, new EventListenerRunnable<T>(event, eventListener)).execute();
    }
}
