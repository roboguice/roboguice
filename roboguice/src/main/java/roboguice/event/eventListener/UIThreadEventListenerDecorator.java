package roboguice.event.eventListener;

import roboguice.event.EventListener;

import android.os.Handler;

/**
 * EventListener Decorator which executes the given event listener on the ui thread, through the provided Handler.
 *
 * @author John Ericksen
 */
public class UIThreadEventListenerDecorator<T> implements EventListener<T> {

    protected EventListener<T> eventListener;
    protected Handler handler;

    public UIThreadEventListenerDecorator(EventListener<T> eventListener, Handler handler) {
        this.eventListener = eventListener;
        this.handler = handler;
    }

    public void onEvent(T event) {
        handler.post( new EventListenerRunnable<T>(event, eventListener));
    }
}
