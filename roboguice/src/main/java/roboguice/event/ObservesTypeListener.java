package roboguice.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import roboguice.event.eventListener.ObserverMethodListener;
import roboguice.event.eventListener.factory.EventListenerThreadingDecorator;

import com.google.inject.Guice;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.HierarchyTraversalFilter;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * Guice driven type listener which scans for the @Observes annotations.
 * Registers these methods with the EventManager.
 *
 * @author Adam Tybor
 * @author John Ericksen
 */
public class ObservesTypeListener implements TypeListener {
    protected Provider<EventManager> eventManagerProvider;
    protected EventListenerThreadingDecorator observerThreadingDecorator;
    private HierarchyTraversalFilter filter;

    public ObservesTypeListener(Provider<EventManager> eventManagerProvider, EventListenerThreadingDecorator observerThreadingDecorator) {
        this.eventManagerProvider = eventManagerProvider;
        this.observerThreadingDecorator = observerThreadingDecorator;
    }

    public <I> void hear(TypeLiteral<I> iTypeLiteral, TypeEncounter<I> iTypeEncounter) {
        if( filter == null ) {
            filter = Guice.createHierarchyTraversalFilter();
        } else {
            filter.reset();
        }
        Class<?> c = iTypeLiteral.getRawType();
        while( isWorthScanning(c)) {
            for (Method method : filter.getAllMethods(Observes.class.getName(), c))
                findContextObserver(method, iTypeEncounter);

            for( Class<?> interfaceClass : c.getInterfaces())
                for (Method method : filter.getAllMethods(Observes.class.getName(), interfaceClass))
                    findContextObserver(method, iTypeEncounter);

            c = c.getSuperclass();
        }
    }

    private boolean isWorthScanning(Class<?> c) {
        return filter.isWorthScanningForMethods(Observes.class.getName(), c);
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
        iTypeEncounter.register(new ContextObserverMethodInjector<I, T>(eventManagerProvider, observerThreadingDecorator, method, parameterType,threadType));
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
        protected Provider<EventManager> eventManagerProvider;
        protected Method method;
        protected Class<T> event;
        protected EventThread threadType;

        public ContextObserverMethodInjector(Provider<EventManager> eventManagerProvider,
                EventListenerThreadingDecorator observerThreadingDecorator,  Method method,
                Class<T> event, EventThread threadType) {
            this.observerThreadingDecorator = observerThreadingDecorator;
            this.eventManagerProvider = eventManagerProvider;
            this.method = method;
            this.event = event;
            this.threadType = threadType;
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        public void afterInjection(I i) {
            eventManagerProvider.get().registerObserver( event, observerThreadingDecorator.decorate(threadType, new ObserverMethodListener(i, method)));
        }
    }
}
