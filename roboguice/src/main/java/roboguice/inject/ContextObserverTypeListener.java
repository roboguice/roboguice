package roboguice.inject;

import roboguice.event.ContextObserver;

import android.content.Context;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import java.lang.reflect.Method;
import java.security.InvalidParameterException;

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

                final Class<?>[] parameterTypes = method.getParameterTypes();                
                if( parameterTypes.length != 1 )
                    throw new InvalidParameterException("Methods annotated with @ContextObserver must take a single parameter subtype of roboguice.event.Event");

                iTypeEncounter.register(new ContextObserverMethodInjector<I>(mContextProvider, mObservationManager, method, method.getParameterTypes()[0] ));
            }
        }
    }

    static class ContextObserverMethodInjector<I> implements InjectionListener<I> {
        protected final Provider<Context> mContextProvider;
        protected final ContextObservationManager mObservationManager;
        protected final Method mMethod;
        protected final Class<?> eventType;

        public ContextObserverMethodInjector(Provider<Context> contextProvider, ContextObservationManager observationManager, Method method, Class<?> eventType) {
            this.mContextProvider = contextProvider;
            this.mObservationManager = observationManager;
            this.mMethod = method;
            this.eventType = eventType;
        }

        @Override
        public void afterInjection(I i) {
            mObservationManager.registerObserver(mContextProvider.get(), i, mMethod, eventType);
        }
    }
}
