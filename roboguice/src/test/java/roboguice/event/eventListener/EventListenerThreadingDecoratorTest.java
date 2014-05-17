package roboguice.event.eventListener;

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import roboguice.event.EventListener;
import roboguice.event.EventThread;
import roboguice.event.eventListener.factory.EventListenerThreadingDecorator;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import android.os.Handler;

/**
 * Tests for the EventListenerThreadingDecorator class
 *
 * @author John Ericksen
 */
public class EventListenerThreadingDecoratorTest {

    protected EventListenerThreadingDecorator eventListenerDecorator;
    protected EventListener<Void> eventListener;

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("rawtypes")
    @Test
    public void buildCurrentThreadObserverTest(){
        final EventListener outputListener = eventListenerDecorator.decorate(EventThread.CURRENT, eventListener);
        assertEquals(eventListener, outputListener);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void buildUIThreadObserverTest(){
        final EventListener outputListener = eventListenerDecorator.decorate(EventThread.UI, eventListener);
        assertEquals( eventListener, ((UIThreadEventListenerDecorator)outputListener).eventListener);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void buildAsyncThreadObserverTest(){
        final EventListener outputListener = eventListenerDecorator.decorate(EventThread.BACKGROUND, eventListener);
        assertEquals( eventListener, ((AsynchronousEventListenerDecorator)outputListener).eventListener);
    }
}
