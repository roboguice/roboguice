package roboguice.event.eventListener;

import org.junit.Before;
import org.junit.Test;
import roboguice.event.EventListener;
import roboguice.event.EventOne;
import roboguice.event.eventListener.factory.RunnableAsyncTaskAdaptorFactory;

import static org.easymock.EasyMock.*;

/**
 * Tests for the AsynchronousEventListenerDecorator class
 *
 * @author John Ericksen
 */
public class AsynchronousEventListenerDecoratorTest {

    protected EventListener<EventOne> eventListener;
    protected RunnableAsyncTaskAdaptorFactory asyncTaskFactory;
    protected RunnableAsyncTaskAdaptor asyncTaskAdaptor;
    protected EventOne event;
    
    protected AsynchronousEventListenerDecorator<EventOne> decorator;

    @Before
    public void setup(){
        eventListener = createMock(EventListener.class);
        asyncTaskFactory = createMock(RunnableAsyncTaskAdaptorFactory.class);
        asyncTaskAdaptor = createMock(RunnableAsyncTaskAdaptor.class);
        event = new EventOne();

        decorator = new AsynchronousEventListenerDecorator<EventOne>(eventListener, asyncTaskFactory);
    }

    @Test
    public void onEventTest(){
        reset(eventListener, asyncTaskFactory);

        expect(asyncTaskFactory.build(event, eventListener)).andReturn(asyncTaskAdaptor);
        asyncTaskAdaptor.execute();

        replay(eventListener, asyncTaskFactory);

        decorator.onEvent(event);

        verify(eventListener, asyncTaskFactory);
    }
}
