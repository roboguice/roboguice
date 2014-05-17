package roboguice.event.eventListener;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

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
        eventListener = createMock(EventListener.class);
        event = new EventOne();

        eventListenerRunnable = new EventListenerRunnable<EventOne>(event, eventListener);
    }

    @Test
    public void runTest(){
        reset(eventListener);

        eventListener.onEvent(event);

        replay(eventListener);

        eventListenerRunnable.run();

        verify(eventListener);
    }
}
