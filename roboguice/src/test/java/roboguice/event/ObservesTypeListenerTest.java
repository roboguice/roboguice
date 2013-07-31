package roboguice.event;

import android.app.Activity;
import android.content.Context;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import roboguice.RoboGuice;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author John Ericksen
 */
@RunWith(RobolectricTestRunner.class)
public class ObservesTypeListenerTest {

    protected EventManager eventManager;
    protected Injector injector;
    protected List<Method> eventOneMethods;
    protected List<Method> eventTwoMethods;
    protected Context context = new Activity();

    @Before
    public void setup() throws NoSuchMethodException {
        injector = RoboGuice.getInjector(Robolectric.application);

        eventManager = injector.getInstance(EventManager.class);

        eventOneMethods = ContextObserverTesterImpl.getMethods(EventOne.class);
        eventTwoMethods = ContextObserverTesterImpl.getMethods(EventTwo.class);
    }

    @Test
    public void simulateInjection() {
        final InjectedTestClass testClass = new InjectedTestClass();
        injector.injectMembers(testClass);

        eventManager.fire(new EventOne());

        testClass.tester.verifyCallCount(eventOneMethods, EventOne.class, 1);
        testClass.tester.verifyCallCount(eventTwoMethods, EventTwo.class, 0);
    }

    @Test(expected = RuntimeException.class)
    public void invalidObservesMethodSignature(){
        injector.getInstance(MalformedObserves.class);
    }

    static public class InjectedTestClass{
        @Inject public ContextObserverTesterImpl tester;
    }

    public class MalformedObserves{
        public void malformedObserves(int val, @Observes EventOne event){}
    }
}
