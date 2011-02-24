package roboguice.event;


/**
 * Interface for registering functionality with the EventManager.
 *
 * @author Mike Burton
 *
 * @param <T>
 */
public interface EventListener<T> {

    /**
     * Method called when event T is triggered.
     * 
     * @param event fired
     */
    public void onEvent(T event);
}
