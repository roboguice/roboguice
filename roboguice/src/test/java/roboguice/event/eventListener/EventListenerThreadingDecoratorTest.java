package roboguice.event.eventListener;

import org.junit.Before;
import org.junit.Test;
import roboguice.event.EventListener;
import roboguice.event.EventThread;
import roboguice.event.eventListener.factory.EventListenerThreadingDecorator;

import android.os.Handler;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import static junit.framework.Assert.assertEquals;
import static org.easymock.EasyMock.createMock;

/**
 * Tests for the EventListenerThreadingDecorator class
 *
 * @author John Ericksen
 */
public class EventListenerThreadingDecoratorTest {

    protected EventListenerThreadingDecorator eventListenerDecorator;
    protected EventListener<Void> eventListener;

    @Before
    public void setup(){

        //noinspection unchecked
        eventListener = createMock(EventListener.class);


        final Module testFactoryModule = new AbstractModule() {
            public void configure() {
                bind(Handler.class).toInstance(createMock(Handler.class));
            }
        };

        final Injector injector = Guice.createInjector(testFactoryModule);

        eventListenerDecorator = injector.getInstance(EventListenerThreadingDecorator.class);
    }

    @Test
    public void buildCurrentThreadObserverTest(){
        final EventListener outputListener = eventListenerDecorator.decorate(EventThread.CURRENT, eventListener);
        assertEquals(eventListener, outputListener);
    }

    @Test
    public void buildUIThreadObserverTest(){
        final EventListener outputListener = eventListenerDecorator.decorate(EventThread.UI, eventListener);
        assertEquals( eventListener, ((UIThreadEventListenerDecorator)outputListener).eventListener);
    }

    @Test
    public void buildAsyncThreadObserverTest(){
        final EventListener outputListener = eventListenerDecorator.decorate(EventThread.BACKGROUND, eventListener);
        assertEquals( eventListener, ((AsynchronousEventListenerDecorator)outputListener).eventListener);
    }
}
