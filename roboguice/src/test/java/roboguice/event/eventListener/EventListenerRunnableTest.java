package roboguice.event.eventListener;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import roboguice.event.EventListener;
import roboguice.event.EventOne;

/**
 * Tests for the EventListenerRunnable class
 *
 * @author John Ericksen
 */
public class EventListenerRunnableTest {

    protected EventOne event;
    protected EventListener<EventOne> eventListener;

    @SuppressWarnings("rawtypes")
    protected EventListenerRunnable eventListenerRunnable;

    @SuppressWarnings("unchecked")
    @Before
    public void setup(){
        //noinspection unchecked
        eventListener = mock(EventListener.class);
        event = new EventOne();

        eventListenerRunnable = new EventListenerRunnable<EventOne>(event, eventListener);
    }

    @Test
    public void runTest(){
        reset(eventListener);

        eventListenerRunnable.run();

        verify(eventListener).onEvent(event);
    }
}
