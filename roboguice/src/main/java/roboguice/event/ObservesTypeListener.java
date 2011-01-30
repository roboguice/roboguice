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
 * Guice driven type listener which scans for the @Observes annotations.
 * Registers these methods with the EventManager.
 *
 * @author Adam Tybor
 * @author John Ericksen
 */
public class ObservesTypeListener implements TypeListener {
    protected EventManager eventManager;
    protected Provider<Context> contextProvider;

    public ObservesTypeListener(Provider<Context> contextProvider, EventManager eventManager) {
        this.eventManager = eventManager;
        this.contextProvider = contextProvider;
    }

    public <I> void hear(TypeLiteral<I> iTypeLiteral, TypeEncounter<I> iTypeEncounter) {
        for( Class<?> c = iTypeLiteral.getRawType(); c!=Object.class ; c = c.getSuperclass() ) {
            for (Method method : c.getDeclaredMethods())
                findContextObserver(method, iTypeEncounter);

            for( Class<?> interfaceClass : c.getInterfaces())
                for (Method method : interfaceClass.getDeclaredMethods())
                    findContextObserver(method, iTypeEncounter);

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
                    registerContextObserver(iTypeEncounter, method, parameterType);
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
        checkMethodParameters(method);
        iTypeEncounter.register(new ContextObserverMethodInjector<I>(contextProvider, eventManager, method, parameterType));
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
    public static class ContextObserverMethodInjector<I> implements InjectionListener<I> {
        protected Provider<Context> contextProvider;
        protected EventManager eventManager;
        protected Method method;
        protected Class<?> event;

        public ContextObserverMethodInjector(Provider<Context> contextProvider, EventManager eventManager, Method method, Class<?> event) {
            this.contextProvider = contextProvider;
            this.eventManager = eventManager;
            this.method = method;
            this.event = event;
        }

        public void afterInjection(I i) {
            eventManager.registerObserver(contextProvider.get(), i, method, event);
        }
    }
}
