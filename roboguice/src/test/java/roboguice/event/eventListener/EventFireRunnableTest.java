package roboguice.event.eventListener;

import org.junit.Before;
import org.junit.Test;
import roboguice.event.EventListener;
import roboguice.event.EventOne;

import static org.easymock.EasyMock.*;

/**
 * Tests for the EventFireRunnable class
 *
 * @author John Ericksen
 */
public class EventFireRunnableTest {

    protected EventOne event;
    protected EventListener<EventOne> eventListener;

    protected EventFireRunnable eventFireRunnable;

    @Before
    public void setup(){
        eventListener = createMock(EventListener.class);
        event = new EventOne();

        eventFireRunnable = new EventFireRunnable<EventOne>(event, eventListener);
    }

    @Test
    public void runTest(){
        reset(eventListener);

        eventListener.onEvent(event);

        replay(eventListener);

        eventFireRunnable.run();

        verify(eventListener);
    }
}
