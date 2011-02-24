package roboguice.event;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import roboguice.config.EventManagerModule;

import android.content.Context;

import com.google.inject.*;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author John Ericksen
 */
public class ObservesTypeListenerTest {

    private EventManager eventManager;
    private Provider<Context> contextProvider;
    private Context context;
    private Injector injector;
    private List<Method> eventOneMethods;
    private List<Method> eventTwoMethods;

    @Before
    public void setup() throws NoSuchMethodException {
        context = EasyMock.createMock(Context.class);
        
        contextProvider = new Provider<Context>() {
            public Context get() {
                return context;
            }
        };

        Module eventManagerModule = new EventManagerModule(new EventManager(), contextProvider);

        Module contextProviderModule = new AbstractModule() {
            public void configure() {
                bind(Context.class).toProvider(contextProvider);
            }
        };

        injector = Guice.createInjector(eventManagerModule, contextProviderModule);

        eventManager = injector.getInstance(EventManager.class);

        eventOneMethods = ContextObserverTesterImpl.getMethods(EventOne.class);
        eventTwoMethods = ContextObserverTesterImpl.getMethods(EventTwo.class);
    }

    @Test
    public void simulateInjection() {
        InjectedTestClass testClass = new InjectedTestClass();
        injector.injectMembers(testClass);

        eventManager.fire(new EventOne());

        testClass.getTester().verifyCallCount(eventOneMethods, EventOne.class, 1);
        testClass.getTester().verifyCallCount(eventTwoMethods, EventTwo.class, 0);
    }

    @Test(expected = RuntimeException.class)
    public void invalidObservesMethodSignature(){
        injector.getInstance(MalformedObserves.class);
    }

    public class InjectedTestClass{
        @Inject
        public ContextObserverTesterImpl tester;

        public ContextObserverTesterImpl getTester() {
            return tester;
        }
    }

    public class MalformedObserves{
        public void malformedObserves(int val, @Observes EventOne event){}
    }
}
