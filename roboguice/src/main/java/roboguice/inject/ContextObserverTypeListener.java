package roboguice.inject;

import android.content.Context;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class ContextObserverTypeListener implements TypeListener {
    final Provider<Context> mContextProvider;
    final ContextObservationManager mObservationManager;

    public ContextObserverTypeListener(Provider<Context> contextProvider, ContextObservationManager observationManager) {
        this.mContextProvider = contextProvider;
        this.mObservationManager = observationManager;
    }

    public <I> void hear(TypeLiteral<I> iTypeLiteral, TypeEncounter<I> iTypeEncounter) {
        for (Method method : iTypeLiteral.getRawType().getMethods()) {
            if (method.isAnnotationPresent(ContextObserver.class)) {
                final ContextObserver annotation = method.getAnnotation(ContextObserver.class);
                registerContextObserver(iTypeEncounter, method, annotation.value());
            }

            if (method.isAnnotationPresent(ContextObservers.class)) {
                final ContextObservers annotation = method.getAnnotation(ContextObservers.class);
                for(ContextObserver observerAnnotation : annotation.value()){
                    registerContextObserver(iTypeEncounter, method, observerAnnotation.value());
                }
            }
            for(int i = 0; i < method.getParameterAnnotations().length; i++){
                Annotation[] annotationArray = method.getParameterAnnotations()[i];
                Class<?>[] parameterTypes = method.getParameterTypes();
                Class parameterType = parameterTypes[i];
                for(Annotation annotation : annotationArray){
                    if(annotation.annotationType().equals(ContextObserves.class)){
                        registerContextObserver(iTypeEncounter, method, parameterType);
                    }
                }
            }
        }
    }

    private <I> void registerContextObserver(TypeEncounter<I> iTypeEncounter, Method method, Class parameterType) {
        checkMethodParameters(method, parameterType);
        iTypeEncounter.register(new ContextObserverMethodInjector<I>(mContextProvider, mObservationManager, method, parameterType));
    }

    private void checkMethodParameters(Method method, Class parameterType) {
        if(method.getParameterTypes().length > 1){
            throw new RuntimeException("Annotation @ContextObserves must only annotate one parameter," +
                    " which must be the only parameter in the listener method.");
        }
        if(method.getParameterTypes().length == 1 && !method.getParameterTypes()[0].equals(parameterType)){
            throw new RuntimeException("Value injected by ContextObserver or ContextObserves in method " +
                    method.getDeclaringClass().getCanonicalName() + "." + method.getName() +
                    " must match annotated type " + parameterType.getName() + " or have no parameters.");
        }
    }

    static class ContextObserverMethodInjector<I> implements InjectionListener<I> {
        private final Provider<Context> mContextProvider;
        private final ContextObservationManager mObservationManager;
        private final Method mMethod;
        private final Class event;

        public ContextObserverMethodInjector(Provider<Context> contextProvider, ContextObservationManager observationManager, Method method, Class event) {
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
