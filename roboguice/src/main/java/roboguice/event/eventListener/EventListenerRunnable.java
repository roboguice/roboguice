package roboguice.event.eventListener;

import roboguice.event.EventListener;

/**
 * Runnable implementation of the event listener on event call.
 *
* @author John Ericksen
*/
public class EventListenerRunnable<T> implements Runnable {

    protected T event;
    protected EventListener<T> eventListener;

    public EventListenerRunnable(T event, EventListener<T> eventListener) {
        this.event = event;
        this.eventListener = eventListener;
    }

    public void run(){
        eventListener.onEvent(event);
    }
}
