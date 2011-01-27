package roboguice.event;

import android.app.Application;
import android.content.Context;
import org.easymock.EasyMock;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author John Ericksen
 */
public class EventManagerTest {

    private EventManager eventManager;
    private Context context;
    private ContextObserverTesterImpl tester;
    private List<Method> eventOneMethods;
    private List<Method> eventTwoMethods;
    private List<Method> methods;
    private EventOne event;

    @BeforeClass(groups = "roboguice")
    public void setup() throws NoSuchMethodException {
        eventManager = new EventManager();
        context = EasyMock.createMock(Context.class);
        tester = new ContextObserverTesterImpl();
        eventOneMethods = ContextObserverTesterImpl.getMethods(EventOne.class);
        eventTwoMethods = ContextObserverTesterImpl.getMethods(EventTwo.class);
        methods = new ArrayList<Method>();
        methods.addAll(eventOneMethods);
        methods.addAll(eventTwoMethods);

        event = new EventOne();
    }

    @BeforeMethod(groups = "roboguice")
    public void reset(){
        tester.reset();
    }

    @Test(groups = "roboguice")
    public void testRegistrationLifeCycle(){
        for(Method method : eventOneMethods){
            eventManager.registerObserver(context, tester, method, EventOne.class);
        }
        for(Method method : eventTwoMethods){
            eventManager.registerObserver(context, tester, method, EventTwo.class);
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

    @Test(groups = "roboguice")
    public void testRegistrationClear(){
        Context contextTwo = EasyMock.createMock(Context.class);

        for(Method method : eventOneMethods){
            eventManager.registerObserver(context, tester, method, EventOne.class);
        }
        for(Method method : eventOneMethods){
            eventManager.registerObserver(contextTwo, tester, method, EventOne.class);
        }

        eventManager.clear(context);

        eventManager.fire(context, event);
        tester.verifyCallCount(eventOneMethods, EventOne.class, 0);

        eventManager.fire(contextTwo, event);
        tester.verifyCallCount(eventOneMethods, EventOne.class, 1);
    }

    @Test(groups = "roboguice", expectedExceptions = RuntimeException.class)
    public void testApplicationContextEvent(){
        Context applicationContext = EasyMock.createMock(Application.class);

        for(Method method : eventOneMethods){
            eventManager.registerObserver(applicationContext, tester, method, EventOne.class);
        }
    }

    @Test(groups = "roboguice")
    public void testInheritanceMethodCalling() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        ClassOne one = new ClassOne();

        Method baseMethod = ClassTwo.class.getDeclaredMethod("bar", null);

        baseMethod.invoke(one, null);
    }

    public class ClassOne extends ClassTwo{
        public void bar(){
            System.out.println("I get called");
        }
    }

    public class ClassTwo{
        public void bar(){
            System.out.println("I don't");
        }
    }
}
