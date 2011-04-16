package roboguice.event.eventListener;

import org.junit.Before;
import org.junit.Test;
import roboguice.event.EventListener;

import android.os.Handler;

import static org.easymock.EasyMock.*;

/**
 * Tests for the AsynchronousEventListenerDecorator class
 *
 * @author John Ericksen
 */
public class AsynchronousEventListenerDecoratorTest {

    protected EventListener<Object> eventListener;
    protected RunnableAsyncTaskAdaptor asyncTaskAdaptor;
    protected AsynchronousEventListenerDecorator<Object> decorator;

    @Before
    public void setup(){
        //noinspection unchecked
        eventListener = createMock(EventListener.class);
        asyncTaskAdaptor = createMock(RunnableAsyncTaskAdaptor.class);
        decorator = new AsynchronousEventListenerDecorator<Object>(createMock(Handler.class),eventListener);
    }

    // Mike doesn't really understand what this test is doing
    @Test
    public void onEventTest(){
        reset(eventListener);

        asyncTaskAdaptor.execute();

        replay(eventListener);

        decorator.onEvent( new Object() );

        verify(eventListener);
    }
}
