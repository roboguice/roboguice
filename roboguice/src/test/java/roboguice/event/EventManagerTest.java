package roboguice.event;

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
    private ContextObserverTesterImpl tester;
    private List<Method> eventOneMethods;
    private List<Method> eventTwoMethods;
    private EventOne event;

    @Before
    public void setup() throws NoSuchMethodException {
        eventManager = new EventManager();
        tester = new ContextObserverTesterImpl();
        eventOneMethods = ContextObserverTesterImpl.getMethods(EventOne.class);
        eventTwoMethods = ContextObserverTesterImpl.getMethods(EventTwo.class);

        event = new EventOne();
    }

    @Test
    public void testRegistrationLifeCycle(){
        for(Method method : eventOneMethods){
            eventManager.registerObserver(EventOne.class, new ObserverMethodListener(tester, method));
        }
        for(Method method : eventTwoMethods){
            eventManager.registerObserver(EventTwo.class, new ObserverMethodListener(tester, method));
        }

        eventManager.fire(event);

        tester.verifyCallCount(eventOneMethods, EventOne.class, 1);
        tester.verifyCallCount(eventTwoMethods, EventTwo.class, 0);

        //reset
        tester.reset();

        eventManager.unregisterObserver(tester, EventOne.class);
        eventManager.unregisterObserver( tester, EventTwo.class);

        eventManager.fire(event);

        tester.verifyCallCount(eventOneMethods, EventOne.class, 0);
        tester.verifyCallCount(eventTwoMethods, EventTwo.class, 0);
    }
}
