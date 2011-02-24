package roboguice.event;

/**
 * Context Observer testing interface
 *
 * @author John Ericksen
 */
public interface ContextObserverTester {

    void observesImplementedEvent(@Observes EventOne event);

    void observesImplementedEvent(@Observes EventTwo event);
}
