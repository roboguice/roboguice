package roboguice.inject;

import android.content.Context;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

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
                iTypeEncounter.register(new ContextObserverMethodInjector<I>(mContextProvider, mObservationManager, method, annotation.value()));
            }

            if (method.isAnnotationPresent(ContextObservers.class)) {
                final ContextObservers annotation = method.getAnnotation(ContextObservers.class);
                for(ContextObserver observerAnnotation : annotation.value()){
                    iTypeEncounter.register(new ContextObserverMethodInjector<I>(mContextProvider, mObservationManager, method, observerAnnotation.value()));
                }
            }
        }
    }

    static class ContextObserverMethodInjector<I> implements InjectionListener<I> {
        private final Provider<Context> mContextProvider;
        private final ContextObservationManager mObservationManager;
        private final Method mMethod;
        private final String event;

        public ContextObserverMethodInjector(Provider<Context> contextProvider, ContextObservationManager observationManager, Method method, String event) {
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
