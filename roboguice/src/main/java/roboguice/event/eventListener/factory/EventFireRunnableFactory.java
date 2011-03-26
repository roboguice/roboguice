package roboguice.event.eventListener.factory;

import roboguice.event.EventListener;
import roboguice.event.eventListener.EventFireRunnable;

/**
 * Factory for EventFireRunnable.
 *
 * @author John Ericksen
 */
public class EventFireRunnableFactory {
    public <T> EventFireRunnable<T> build(T event, EventListener<T> eventListener) {
        return new EventFireRunnable<T>(event, eventListener);
    }
}
