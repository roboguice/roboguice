package roboguice.inject;

import android.content.Context;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Injection type listener to bind the @ContextObserves annotated methods to the
 * ContextObserverClassEventManager events.
 *
 * @author John Ericksen
 */
public class ContextObserverClassEventTypeListener implements TypeListener {
    private final Provider<Context> contextProvider;
    private final ContextObserverClassEventManager observationManager;

    public ContextObserverClassEventTypeListener(Provider<Context> contextProvider, ContextObserverClassEventManager observationManager) {
        this.contextProvider = contextProvider;
        this.observationManager = observationManager;
    }

    public <I> void hear(TypeLiteral<I> iTypeLiteral, TypeEncounter<I> iTypeEncounter) {
        for (Method method : iTypeLiteral.getRawType().getMethods()) {
            for(int i = 0; i < method.getParameterAnnotations().length; i++){
                Annotation[] annotationArray = method.getParameterAnnotations()[i];
                Class<?>[] parameterTypes = method.getParameterTypes();
                Class parameterType = parameterTypes[i];
                for(Annotation annotation : annotationArray){
                    if(annotation.annotationType().equals(ContextObserves.class)){
                        if(parameterTypes.length > 1){
                            throw new RuntimeException("Annotation @ContextObserves must only annotate one parameter," +
                                    " which must be the only parameter in the listener method.");
                        }
                        iTypeEncounter.register(new ContextObserverClassEventMethodInjector<I>(contextProvider, observationManager, method, parameterType));;
                    }
                }
            }
        }
    }

    private static class ContextObserverClassEventMethodInjector<I> implements InjectionListener<I> {
        private final Provider<Context> contextProvider;
        private final ContextObserverClassEventManager observationManager;
        private final Method mMethod;
        private final Class event;

        public ContextObserverClassEventMethodInjector(Provider<Context> contextProvider, ContextObserverClassEventManager observationManager, Method method, Class event) {
            this.contextProvider = contextProvider;
            this.observationManager = observationManager;
            this.mMethod = method;
            this.event = event;
        }

        public void afterInjection(I i) {
            observationManager.registerObserver(contextProvider.get(), i, mMethod, event);
        }
    }
}
