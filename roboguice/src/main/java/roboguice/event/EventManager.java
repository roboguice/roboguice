package roboguice.inject;

import android.content.Context;

import com.google.inject.Singleton;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Singleton
public class ContextObservationManager {

    protected final Map<Context, Map<Class<?>, Set<ContextObserverMethod>>> mRegistrations;

    public ContextObservationManager() {
        mRegistrations  = new WeakHashMap<Context, Map<Class<?>, Set<ContextObserverMethod>>>();
    }

    public boolean isEnabled() {
        return true;
    }

    public void registerObserver(Context context, Object instance, Method method, Class<?> type ) {
        if (!isEnabled()) return;

        Map<Class<?>, Set<ContextObserverMethod>> methods = mRegistrations.get(context);
        if (methods == null) {
            methods = new HashMap<Class<?>, Set<ContextObserverMethod>>();
            mRegistrations.put(context, methods);
        }

        Set<ContextObserverMethod> observers = methods.get(type);
        if (observers == null) {
            observers = new HashSet<ContextObserverMethod>();
            methods.put(type, observers);
        }

        observers.add(new ContextObserverMethod(instance, method, type));
    }

    public void unregisterObserver(Context context, Object instance, String event) {
        if (!isEnabled()) return;

        final Map<Class<?>, Set<ContextObserverMethod>> methods = mRegistrations.get(context);
        if (methods == null) return;

        final Set<ContextObserverMethod> observers = methods.get(event);
        if (observers == null) return;

        for (Iterator<ContextObserverMethod> iterator = observers.iterator(); iterator.hasNext();) {
            ContextObserverMethod observer = iterator.next();
            if (observer != null) {
                final Object registeredInstance = observer.instanceReference.get();
                if (registeredInstance == instance) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

    public void clear(Context context) {
        if (!isEnabled()) return;

        final Map<Class<?>, Set<ContextObserverMethod>> methods = mRegistrations.get(context);
        if (methods == null) return;

        mRegistrations.remove(context);
        methods.clear();
    }

    public void notify(Context context, String event, Object... args) {
        if (!isEnabled()) return;

        final Map<Class<?>, Set<ContextObserverMethod>> methods = mRegistrations.get(context);
        if (methods == null) return;

        final Set<ContextObserverMethod> observers = methods.get(event);
        if (observers == null) return;

        for (ContextObserverMethod observerMethod : observers) {
            try {
                observerMethod.invoke(null, args);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static class NullContextObservationManager extends ContextObservationManager {
        @Override
        public boolean isEnabled() {
            return false;
        }
    }

    protected static class ContextObserverMethod {
        protected Class<?> eventType;
        protected Method method;
        protected WeakReference<Object> instanceReference;

        public ContextObserverMethod(Object instance, Method method, Class<?> eventType ) {
            this.instanceReference = new WeakReference<Object>(instance);
            this.method = method;
            this.eventType = eventType;
        }

        public Object invoke(Object defaultReturn, Object... args) throws InvocationTargetException, IllegalAccessException {
            final Object instance = instanceReference.get();
            return instance!=null ? method.invoke(instance,args) : defaultReturn;
        }
    }
}
