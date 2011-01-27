package roboguice.event;

/**
 * @author John Ericksen
 */
public interface ContextObserverTester {

    void observesImplementedEvent(@Observes EventOne event);

    void observesImplementedEvent(@Observes EventTwo event);
}
