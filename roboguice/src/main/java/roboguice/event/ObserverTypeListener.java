package roboguice.event;

import android.content.Context;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Guice driven type listener which scans for the @Observer, @Observers, and @Observes annotations.
 * Registers these methods with the EventManager.
 *
 * @author Adam Tabor
 * @author John Ericksen
 */
public class ObserverTypeListener implements TypeListener {
    protected final Provider<Context> contextProvider;
    protected final EventManager observationManager;

    public ObserverTypeListener(Provider<Context> contextProvider, EventManager observationManager) {
        this.contextProvider = contextProvider;
        this.observationManager = observationManager;
    }

    public <I> void hear(TypeLiteral<I> iTypeLiteral, TypeEncounter<I> iTypeEncounter) {
        for (Method method : iTypeLiteral.getRawType().getMethods()) {
            //Observer scan
            if (method.isAnnotationPresent(Observer.class)) {
                final Observer annotation = method.getAnnotation(Observer.class);
                registerContextObserver(iTypeEncounter, method, annotation.value());
            }
            //Observers scan
            if (method.isAnnotationPresent(Observers.class)) {
                final Observers annotation = method.getAnnotation(Observers.class);
                for(Observer observerAnnotation : annotation.value()){
                    registerContextObserver(iTypeEncounter, method, observerAnnotation.value());
                }
            }
            //Observes scan
            for(int i = 0; i < method.getParameterAnnotations().length; i++){
                final Annotation[] annotationArray = method.getParameterAnnotations()[i];
                final Class<?>[] parameterTypes = method.getParameterTypes();
                final Class parameterType = parameterTypes[i];
                for(Annotation annotation : annotationArray){
                    if(annotation.annotationType().equals(Observes.class)){
                        registerContextObserver(iTypeEncounter, method, parameterType);
                    }
                }
            }
        }
    }

    /**
     * Error checks the observed method and registers method with typeEncounter
     *
     * @param iTypeEncounter
     * @param method
     * @param parameterType
     * @param <I>
     */
    protected <I> void registerContextObserver(TypeEncounter<I> iTypeEncounter, Method method, Class parameterType) {
        checkMethodParameters(method, parameterType);
        iTypeEncounter.register(new ContextObserverMethodInjector<I>(contextProvider, observationManager, method, parameterType));
    }

    /**
     * Error checking method, verifies:
     *
     * 1.  The method has the correct number of parameters, 0 or 1
     * 2.  If the method has a parameter, it is of the proper type.
     *
     * @param method
     * @param parameterType
     */
    protected void checkMethodParameters(Method method, Class parameterType) {
        if(method.getParameterTypes().length > 1){
            throw new RuntimeException("Annotation @Observes must only annotate one parameter," +
                    " which must be the only parameter in the listener method.");
        }
        if(method.getParameterTypes().length == 1 && !method.getParameterTypes()[0].isAssignableFrom(parameterType)){
            throw new RuntimeException("Value injected by Observer or Observes in method " +
                    method.getDeclaringClass().getCanonicalName() + "." + method.getName() +
                    " must match annotated type " + parameterType.getName() + " or have no parameters.");
        }
    }

    /**
     * Injection listener to handle the observation manager registration.
     * 
     * @param <I>
     */
    protected static class ContextObserverMethodInjector<I> implements InjectionListener<I> {
        private final Provider<Context> mContextProvider;
        private final EventManager mObservationManager;
        private final Method mMethod;
        private final Class event;

        public ContextObserverMethodInjector(Provider<Context> contextProvider, EventManager observationManager, Method method, Class event) {
            this.mContextProvider = contextProvider;
            this.mObservationManager = observationManager;
            this.mMethod = method;
            this.event = event;
        }

        public void afterInjection(I i) {
            mObservationManager.registerObserver(mContextProvider.get(), i, mMethod, event);
        }
    }
}
