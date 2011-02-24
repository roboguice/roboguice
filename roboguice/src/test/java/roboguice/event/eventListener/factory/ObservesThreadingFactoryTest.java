package roboguice.event.eventListener.factory;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.junit.Before;
import org.junit.Test;
import roboguice.event.EventListener;
import roboguice.event.EventThread;

import static junit.framework.Assert.assertEquals;
import static org.easymock.EasyMock.*;

/**
 * Tests for the ObservesThreadingFactory class
 *
 * @author John Ericksen
 */
public class ObservesThreadingFactoryTest {

    protected UIThreadEventListenerDecoratorFactory uiThreadFactory;
    protected AsynchronousEventListenerDecoratorFactory asyncFactory;
    protected ObservesThreadingFactory observesFactory;
    protected EventListener eventListener;

    @Before
    public void setup(){

        uiThreadFactory = createMock(UIThreadEventListenerDecoratorFactory.class);
        asyncFactory = createMock(AsynchronousEventListenerDecoratorFactory.class);
        eventListener = createMock(EventListener.class);


        Module testFactoryModule = new AbstractModule() {
            public void configure() {
                bind(UIThreadEventListenerDecoratorFactory.class).toInstance(uiThreadFactory);
                bind(AsynchronousEventListenerDecoratorFactory.class).toInstance(asyncFactory);
            }
        };

        Injector injector = Guice.createInjector(testFactoryModule);

        observesFactory = injector.getInstance(ObservesThreadingFactory.class);
    }

    @Test
    public void buildCurrentThreadObserverTest(){
        reset(uiThreadFactory, asyncFactory);

        //no calls

        replay(uiThreadFactory, asyncFactory);

        EventListener outputListener = observesFactory.buildMethodObserver(EventThread.CURRENT, eventListener);

        assertEquals(eventListener, outputListener);

        verify(uiThreadFactory, asyncFactory);
    }

    @Test
    public void buildUIThreadObserverTest(){
        EventListener outputListener = createMock(EventListener.class);

        reset(uiThreadFactory, asyncFactory);

        expect(uiThreadFactory.buildDecorator(eventListener)).andReturn(outputListener);

        replay(uiThreadFactory, asyncFactory);

        assertEquals(observesFactory.buildMethodObserver(EventThread.UI, eventListener),
                outputListener);

        verify(uiThreadFactory, asyncFactory);
    }

    @Test
    public void buildAsyncThreadObserverTest(){
        EventListener outputListener = createMock(EventListener.class);

        reset(uiThreadFactory, asyncFactory);

        expect(asyncFactory.buildDecorator(eventListener)).andReturn(outputListener);

        replay(uiThreadFactory, asyncFactory);

        assertEquals(observesFactory.buildMethodObserver(EventThread.NEW, eventListener),
                outputListener);

        verify(uiThreadFactory, asyncFactory);
    }
}
