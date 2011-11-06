package roboguice.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Base ContextSingleton Observer testing class exercising the various implementation combinations.
 *
 * @author John Ericksen
 */
@SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
public class ContextObserverBase {

    private Map<String, Map<Class<?>, Integer>> callCount = new HashMap<String, Map<Class<?>, Integer>>();

    public void verifyCallCount(List<Method> methods, Class<?> event, int expectedCount){
        for(Method method : methods){

            assertTrue("Method: " + method.getName() + " was not called.",
                    callCount.containsKey(method.getName()) || expectedCount == 0);

            if(callCount.containsKey(method.getName())){

            Map<Class<?>, Integer> callCountClass = callCount.get(method.getName());

                if(expectedCount > 0){

                    assertTrue("Event: " + event.getName() + " was not observed.",
                            callCountClass.containsKey(event) || expectedCount == 0);

                    if(callCountClass.containsKey(event)){
                        assertEquals(
                                "Call count was not expected",
                                callCountClass.get(event).intValue(), expectedCount);
                    }
                }
            }
        }
    }

    public void reset() {
        for(Map.Entry<String, Map<Class<?>, Integer>> callCountEntry : callCount.entrySet()){
            for(Map.Entry<Class<?>, Integer> callCountClassEntry : callCountEntry.getValue().entrySet()){
                callCountClassEntry.setValue(0);
            }
        }
    }

    public static final String[] methods = {
        "baseObservesEvent",
        "baseObservesProtectedEvent",
        "baseObservesPackagePrivateEvent",
        "baseObservesPrivateEvent",
    };

    public static final String OVERLOADED_METHOD = "baseObservesOverloadedEvent";

    public void baseObservesEvent(@Observes EventOne event){
        triggerCalled(methods[0], EventOne.class);
    }

    public void baseObservesEvent(@Observes EventTwo event){
        triggerCalled(methods[0], EventTwo.class);
    }

    protected void baseObservesProtectedEvent(@Observes EventOne event){
        triggerCalled(methods[1], EventOne.class);
    }

    protected void baseObservesProtectedEvent(@Observes EventTwo event){
        triggerCalled(methods[1], EventTwo.class);
    }

    void baseObservesPackagePrivateEvent(@Observes EventOne event){
        triggerCalled(methods[2], EventOne.class);
    }

    void baseObservesPackagePrivateEvent(@Observes EventTwo event){
        triggerCalled(methods[2], EventTwo.class);
    }

    private void baseObservesPrivateEvent(@Observes EventOne event){
        triggerCalled(methods[3], EventOne.class);
    }

    private void baseObservesPrivateEvent(@Observes EventTwo event){
        triggerCalled(methods[3], EventTwo.class);
    }

    public void observesOverloadedEvent(@Observes EventOne event){
        triggerCalled(OVERLOADED_METHOD, EventOne.class);
    }

    public void observesOverloadedEvent(@Observes EventTwo event){
        triggerCalled(OVERLOADED_METHOD, EventTwo.class);
    }

    protected void observesOverloadedProtectedEvent(@Observes EventOne event){
        triggerCalled(OVERLOADED_METHOD, EventOne.class);
    }

    protected void observesOverloadedProtectedEvent(@Observes EventTwo event){
        triggerCalled(OVERLOADED_METHOD, EventTwo.class);
    }

    void observesOverloadedPackagePrivateEvent(@Observes EventOne event){
        triggerCalled(OVERLOADED_METHOD, EventOne.class);
    }

    void observesOverloadedPackagePrivateEvent(@Observes EventTwo event){
        triggerCalled(OVERLOADED_METHOD, EventTwo.class);
    }

    public void triggerCalled(String method, Class eventClass) {
        if(!callCount.containsKey(method)){
            callCount.put(method, new HashMap<Class<?>, Integer>());
        }
        Map<Class<?>, Integer> callCountClass = callCount.get(method);

        if(!callCountClass.containsKey(eventClass)){
            callCountClass.put(eventClass, 0);
        }
        callCountClass.put(eventClass, callCountClass.get(eventClass) + 1);
    }

    public static List<Method> getMethods(Class<?> eventClass) throws NoSuchMethodException {
        List<Method> methodList = new ArrayList<Method>();

        for(String method : ContextObserverBase.methods){
            methodList.add(ContextObserverBase.class.getDeclaredMethod(method, eventClass));
        }

        return methodList;
    }
}
