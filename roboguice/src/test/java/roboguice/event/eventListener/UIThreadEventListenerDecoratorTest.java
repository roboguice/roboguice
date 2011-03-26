package roboguice.event.eventListener;

import android.os.Handler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import roboguice.event.EventListener;
import roboguice.event.EventOne;
import roboguice.event.eventListener.factory.EventFireRunnableFactory;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.*;

/**
 * Tests for the UIThreadEventListenerDecorator class
 *
 * @author John Ericksen
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Handler.class)
public class UIThreadEventListenerDecoratorTest {

    protected EventListener<EventOne> eventListener;
    protected Handler handler;
    protected EventFireRunnableFactory eventFireFactory;
    protected EventFireRunnable<EventOne> eventFireRunnable;
    protected EventOne event;
    
    protected UIThreadEventListenerDecorator<EventOne> decorator;

    @Before
    public void setup(){
        eventListener = createMock(EventListener.class);
        handler = createMock(Handler.class);
        eventFireFactory = createMock(EventFireRunnableFactory.class);
        eventFireRunnable = createMock(EventFireRunnable.class);
        event = new EventOne();
        
        decorator = new UIThreadEventListenerDecorator<EventOne>(eventListener, handler, eventFireFactory);
        
        
    }

    @Test
    public void onEventTest(){
        reset(eventListener, handler, eventFireFactory, eventFireRunnable);

        expect(eventFireFactory.build(event, eventListener)).andReturn(eventFireRunnable);
        expect(handler.post(eventFireRunnable)).andReturn(true);

        replay(eventListener, handler, eventFireFactory, eventFireRunnable);

        decorator.onEvent(event);

        verify(eventListener, handler, eventFireFactory, eventFireRunnable);
    }
}
