package roboguice.event;

import roboguice.event.eventListener.ObserverMethodListener;
import roboguice.event.eventListener.factory.EventListenerThreadingDecorator;

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
public class ObservesTypeListener implements TypeListener {
    protected EventManager eventManager;
    protected EventListenerThreadingDecorator observerThreadingDecorator;

    public ObservesTypeListener(EventManager eventManager, EventListenerThreadingDecorator observerThreadingDecorator) {
        this.eventManager = eventManager;
        this.observerThreadingDecorator = observerThreadingDecorator;
    }

    public <I> void hear(TypeLiteral<I> iTypeLiteral, TypeEncounter<I> iTypeEncounter) {
        for( Class<?> c = iTypeLiteral.getRawType(); c!=Object.class ; c = c.getSuperclass() ) {
            for (Method method : c.getDeclaredMethods()) {
                findContextObserver(method, iTypeEncounter);
            }
            for( Class<?> interfaceClass : c.getInterfaces()){
                for (Method method : interfaceClass.getDeclaredMethods()){
                    findContextObserver(method, iTypeEncounter);
                }
            }
        }
    }

    protected <I> void findContextObserver(Method method, TypeEncounter<I> iTypeEncounter) {
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for(int i = 0; i < parameterAnnotations.length; i++){
            final Annotation[] annotationArray = parameterAnnotations[i];
            final Class<?>[] parameterTypes = method.getParameterTypes();
            final Class<?> parameterType = parameterTypes[i];

            for(Annotation annotation : annotationArray)
                if(annotation.annotationType().equals(Observes.class))
                    registerContextObserver(iTypeEncounter, method, parameterType, ((Observes)annotation).value());
        }
    }

    /**
     * Error checks the observed method and registers method with typeEncounter
     *
     * @param iTypeEncounter
     * @param method
     * @param parameterType
     * @param threadType
     * @param <I, T>
     */
    protected <I, T> void registerContextObserver(TypeEncounter<I> iTypeEncounter, Method method, Class<T> parameterType, EventThread threadType) {
        checkMethodParameters(method);
        iTypeEncounter.register(new ContextObserverMethodInjector<I, T>(eventManager, observerThreadingDecorator,
                method, parameterType,threadType));
    }

    /**
     * Error checking method, verifies that the method has the correct number of parameters.
     *
     * @param method
     */
    protected void checkMethodParameters(Method method) {
        if(method.getParameterTypes().length > 1)
            throw new RuntimeException("Annotation @Observes must only annotate one parameter," +
                    " which must be the only parameter in the listener method.");
    }

    /**
     * Injection listener to handle the observation manager registration.
     *
     * @param <I>
     */
    public static class ContextObserverMethodInjector<I, T> implements InjectionListener<I> {
        protected EventListenerThreadingDecorator observerThreadingDecorator;
        protected EventManager eventManager;
        protected Method method;
        protected Class<T> event;
        protected EventThread threadType;

        public ContextObserverMethodInjector(EventManager eventManager,
                                             EventListenerThreadingDecorator observerThreadingDecorator,  Method method,
                                             Class<T> event, EventThread threadType) {
            this.observerThreadingDecorator = observerThreadingDecorator;
            this.eventManager = eventManager;
            this.method = method;
            this.event = event;
            this.threadType = threadType;
        }

        public void afterInjection(I i) {
            eventManager.registerObserver( event, observerThreadingDecorator.decorate(threadType, new ObserverMethodListener<T>(i, method)));
        }
    }
}
