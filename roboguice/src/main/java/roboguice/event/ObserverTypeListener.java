package roboguice.event;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Guice driven type listener which scans for the @Observes annotations.
 * Registers these methods with the EventManager.
 *
 * @author Adam Tybor
 * @author John Ericksen
 */
public class ObserverTypeListener implements TypeListener {
    protected final EventManager observationManager;

    public ObserverTypeListener(EventManager observationManager) {
        this.observationManager = observationManager;
    }

    public <I> void hear(TypeLiteral<I> iTypeLiteral, TypeEncounter<I> iTypeEncounter) {
        for (Method method : iTypeLiteral.getRawType().getMethods()) {
            final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for(int i = 0; i < parameterAnnotations.length; i++){
                final Annotation[] annotationArray = parameterAnnotations[i];
                final Class<?>[] parameterTypes = method.getParameterTypes();
                final Class<?> parameterType = parameterTypes[i];

                for(Annotation annotation : annotationArray)
                    if(annotation.annotationType().equals(Observes.class))
                        registerContextObserver(iTypeEncounter, method, parameterType);                                    
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
        iTypeEncounter.register(new ContextObserverMethodInjector<I>(observationManager, method, parameterType));
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
        if(method.getParameterTypes().length > 1)
            throw new RuntimeException("Annotation @Observes must only annotate one parameter," +
                    " which must be the only parameter in the listener method.");

        if(method.getParameterTypes().length == 1 && !method.getParameterTypes()[0].isAssignableFrom(parameterType))
            throw new RuntimeException("Value injected by Observer or Observes in method " +
                    method.getDeclaringClass().getCanonicalName() + "." + method.getName() +
                    " must match annotated type " + parameterType.getName() + " .");
        
    }

    /**
     * Injection listener to handle the observation manager registration.
     * 
     * @param <I>
     */
    protected static class ContextObserverMethodInjector<I> implements InjectionListener<I> {
        protected final EventManager mObservationManager;
        protected final Method mMethod;
        protected final Class event;

        public ContextObserverMethodInjector(EventManager observationManager, Method method, Class event) {
            this.mObservationManager = observationManager;
            this.mMethod = method;
            this.event = event;
        }

        public void afterInjection(I i) {
            mObservationManager.registerObserver(i, mMethod, event);
        }
    }
}
