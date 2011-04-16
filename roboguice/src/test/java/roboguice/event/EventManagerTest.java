package roboguice.event;

import android.app.Application;
import android.content.Context;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import roboguice.event.eventListener.ObserverMethodListener;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Test class verifying eventManager functionality
 *
 * @author John Ericksen
 */
public class EventManagerTest {

    private EventManager eventManager;
    private Context context;
    private ContextObserverTesterImpl tester;
    private List<Method> eventOneMethods;
    private List<Method> eventTwoMethods;
    private EventOne event;

    @Before
    public void setup() throws NoSuchMethodException {
        eventManager = new EventManager();
        context = EasyMock.createMock(Context.class);
        tester = new ContextObserverTesterImpl();
        eventOneMethods = ContextObserverTesterImpl.getMethods(EventOne.class);
        eventTwoMethods = ContextObserverTesterImpl.getMethods(EventTwo.class);

        event = new EventOne();
    }

    @Test
    public void testRegistrationLifeCycle(){
        for(Method method : eventOneMethods){
            eventManager.registerObserver(context, EventOne.class, new ObserverMethodListener(tester, method));
        }
        for(Method method : eventTwoMethods){
            eventManager.registerObserver(context, EventTwo.class, new ObserverMethodListener(tester, method));
        }

        eventManager.fire(context, event);

        tester.verifyCallCount(eventOneMethods, EventOne.class, 1);
        tester.verifyCallCount(eventTwoMethods, EventTwo.class, 0);

        //reset
        tester.reset();

        eventManager.unregisterObserver(context, tester, EventOne.class);
        eventManager.unregisterObserver(context, tester, EventTwo.class);

        eventManager.fire(context, event);

        tester.verifyCallCount(eventOneMethods, EventOne.class, 0);
        tester.verifyCallCount(eventTwoMethods, EventTwo.class, 0);
    }

    @Test
    public void testRegistrationClear(){
        Context contextTwo = EasyMock.createMock(Context.class);

        for(Method method : eventOneMethods){
            eventManager.registerObserver(context, EventOne.class, new ObserverMethodListener(tester, method));
        }
        for(Method method : eventOneMethods){
            eventManager.registerObserver(contextTwo, EventOne.class, new ObserverMethodListener(tester, method));
        }

        eventManager.clear(context);

        eventManager.fire(context, event);
        tester.verifyCallCount(eventOneMethods, EventOne.class, 0);

        eventManager.fire(contextTwo, event);
        tester.verifyCallCount(eventOneMethods, EventOne.class, 1);
    }

    @Test(expected = RuntimeException.class)
    public void testApplicationContextEvent(){
        Context applicationContext = EasyMock.createMock(Application.class);

        for(Method method : eventOneMethods){
            eventManager.registerObserver(applicationContext, EventOne.class, new ObserverMethodListener(tester, method));
        }
    }
}
