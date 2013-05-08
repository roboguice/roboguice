package roboguice.event;

/**
 * Interface for registering functionality with the EventManager.
 * 
 * @author Mike Burton
 * 
 * @param <T>
 */
public interface EventProducer<T> {

    /**
     * Method called when event T is requested.
     * 
     * @return event produced.
     */
    public T onEventRequested();
}
