package roboguice.event.eventListener;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import org.junit.Before;
import org.junit.Test;

import roboguice.event.EventListener;

import android.os.Handler;

/**
 * Tests for the AsynchronousEventListenerDecorator class
 *
 * @author John Ericksen
 */
public class AsynchronousEventListenerDecoratorTest {

    protected EventListener<Object> eventListener;
    protected RunnableAsyncTaskAdaptor asyncTaskAdaptor;
    protected AsynchronousEventListenerDecorator<Object> decorator;

    @SuppressWarnings("unchecked")
    @Before
    public void setup(){
        //noinspection unchecked
        eventListener = createMock(EventListener.class);
        asyncTaskAdaptor = createMock(RunnableAsyncTaskAdaptor.class);
        decorator = new AsynchronousEventListenerDecorator<Object>(createMock(Handler.class),eventListener);
    }

    // Mike doesn't really understand what this test is doing
    @SuppressWarnings("deprecation")
    @Test
    public void onEventTest(){
        reset(eventListener);

        asyncTaskAdaptor.execute();

        replay(eventListener);

        decorator.onEvent( new Object() );

        verify(eventListener);
    }
}
