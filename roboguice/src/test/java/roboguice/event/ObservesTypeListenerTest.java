package roboguice.event;

import org.easymock.EasyMock;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import android.content.Context;

import com.google.inject.*;
import com.google.inject.matcher.Matchers;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author John Ericksen
 */
public class ObservesTypeListenerTest {

    private EventManager contextAwareEventManager;
    private Provider<Context> contextProvider;
    private Context context;
    private Injector injector;
    private List<Method> eventOneMethods;
    private List<Method> eventTwoMethods;

    @BeforeClass(groups = "roboguice")
    public void setup() throws NoSuchMethodException {
        context = EasyMock.createMock(Context.class);

        contextAwareEventManager = new EventManager();

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
        injector.injectMembers(contextAwareEventManager);

        eventOneMethods = ContextObserverTesterImpl.getMethods(EventOne.class);
        eventTwoMethods = ContextObserverTesterImpl.getMethods(EventTwo.class);
    }

    @Test(groups = "roboguice")
    public void simulateInjection() {
        InjectedTestClass testClass = new InjectedTestClass();
        injector.injectMembers(testClass);

        contextAwareEventManager.fire(new EventOne());

        testClass.getTester().verifyCallCount(eventOneMethods, EventOne.class, 1);
        testClass.getTester().verifyCallCount(eventTwoMethods, EventTwo.class, 0);
    }

    @Test(groups = "roboguice", expectedExceptions = RuntimeException.class)
    public void invalidObservesMethodSignature(){
        MalformedObserves testClass = new MalformedObserves();

        injector.injectMembers(testClass);
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


class EventManagerModule extends AbstractModule {

    protected EventManager eventManager;
    protected Provider<Context> contextProvider;

    public EventManagerModule(EventManager eventManager, Provider<Context> contextProvider) {
        this.eventManager = eventManager;
        this.contextProvider = contextProvider;
    }

    @Override
    protected void configure() {

        // Context observers
        bind(EventManager.class).toInstance(eventManager);
        bindListener(Matchers.any(), new ObservesTypeListener(contextProvider, eventManager));

        requestInjection(eventManager);
    }
}
