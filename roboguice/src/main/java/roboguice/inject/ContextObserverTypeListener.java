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
                iTypeEncounter.register(new ContextObserverMethodInjector<I>(mContextProvider, mObservationManager, method));
            }
        }
    }

    static class ContextObserverMethodInjector<I> implements InjectionListener<I> {
        private final Provider<Context> mContextProvider;
        private final ContextObservationManager mObservationManager;
        private final Method mMethod;

        public ContextObserverMethodInjector(Provider<Context> contextProvider, ContextObservationManager observationManager, Method method) {
            mContextProvider = contextProvider;
            mObservationManager = observationManager;
            mMethod = method;
        }

        public void afterInjection(I i) {
            mObservationManager.registerObserver(mContextProvider.get(), i, mMethod);
        }
    }
}
