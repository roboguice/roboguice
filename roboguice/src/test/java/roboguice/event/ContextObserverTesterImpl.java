package roboguice.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * ContextSingleton Observer testing class exercising the various implementation combinations.
 *
 * @author John Ericksen
 */
public class ContextObserverTesterImpl extends ContextObserverBase implements ContextObserverTester{

    public static final String[] methods = {
        "observesEvent",
        "observesProtectedEvent",
        "observesPackagePrivateEvent",
        "observesPrivateEvent",
        "observesOverloadedEvent",
        "observesOverloadedProtectedEvent",
        "observesOverloadedPackagePrivateEvent",
        "observesImplementedEvent"
    };

    public void observesEvent(@Observes EventOne event){
        triggerCalled(methods[0], EventOne.class);
    }

    public void observesEvent(@Observes EventTwo event){
        triggerCalled(methods[0], EventOne.class);
    }

    protected void observesProtectedEvent(@Observes EventOne event){
        triggerCalled(methods[1], EventOne.class);
    }

    protected void observesProtectedEvent(@Observes EventTwo event){
        triggerCalled(methods[1], EventOne.class);
    }

    void observesPackagePrivateEvent(@Observes EventOne event){
        triggerCalled(methods[2], EventOne.class);
    }

    void observesPackagePrivateEvent(@Observes EventTwo event){
        triggerCalled(methods[2], EventOne.class);
    }

    private void observesPrivateEvent(@Observes EventOne event){
        triggerCalled(methods[3], EventOne.class);
    }

    private void observesPrivateEvent(@Observes EventTwo event){
        triggerCalled(methods[3], EventOne.class);
    }

    public void observesOverloadedEvent(EventOne event){
        triggerCalled(methods[4], EventOne.class);
    }

    public void observesOverloadedEvent(EventTwo event){
        triggerCalled(methods[4], EventTwo.class);
    }

    protected void observesOverloadedProtectedEvent(EventOne event){
        triggerCalled(methods[5], EventOne.class);
    }

    protected void observesOverloadedProtectedEvent(EventTwo event){
        triggerCalled(methods[5], EventTwo.class);
    }

    void observesOverloadedPackagePrivateEvent(EventOne event){
        triggerCalled(methods[6], EventOne.class);
    }

    void observesOverloadedPackagePrivateEvent(EventTwo event){
        triggerCalled(methods[6], EventTwo.class);
    }

    public void observesImplementedEvent(EventOne event){
        triggerCalled(methods[7], EventOne.class);
    }

    public void observesImplementedEvent(EventTwo event){
        triggerCalled(methods[7], EventTwo.class);
    }

    public static List<Method> getMethods(Class<?> eventClass) throws NoSuchMethodException {
        List<Method> methodList = new ArrayList<Method>();

        methodList.addAll(ContextObserverBase.getMethods(eventClass));

        for(String method : ContextObserverTesterImpl.methods){
            methodList.add(ContextObserverTesterImpl.class.getDeclaredMethod(method, eventClass));
        }

        return methodList;
    }
}
