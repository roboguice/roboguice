package roboguice.event.eventListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import roboguice.event.EventListener;

import android.os.Handler;

import static org.mockito.Mockito.*;

/**
 * Tests for the AsynchronousEventListenerDecorator class
 *
 * @author John Ericksen
 */
@SuppressWarnings("unchecked")
public class AsynchronousEventListenerDecoratorTest {

    protected EventListener<Object> eventListener;
    protected AsynchronousEventListenerDecorator<Object> decorator;

    @SuppressWarnings("unchecked")
    @Before
    public void setup(){
        //noinspection unchecked
        eventListener = mock(EventListener.class);
        decorator = new AsynchronousEventListenerDecorator<Object>(mock(Handler.class),eventListener);
    }

    // Mike doesn't really understand what this test is doing
    @SuppressWarnings("deprecation")
    @Test
    public void onEventTest(){
        reset(eventListener);

        decorator.onEvent( new Object() );

        verify(eventListener,Mockito.never()).onEvent( Mockito.anyObject());
    }
}
