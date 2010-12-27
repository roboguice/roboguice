package roboguice.inject;

import android.content.Context;
import com.google.inject.Singleton;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Singleton
public class ContextObservationManager {

    private final Map<Context, Map<String, Set<ContextObserverMethod>>> mRegistrations;

    public ContextObservationManager() {
        mRegistrations  = new WeakHashMap<Context, Map<String, Set<ContextObserverMethod>>>();
    }

    public boolean isEnabled() {
        return true;
    }

    public void registerObserver(Context context, Object instance, Method method) {
        registerObserver(context, instance, method, method.getName());
    }

    public void registerObserver(Context context, Object instance, Method method, String methodName) {
        if (!isEnabled()) return;

        Map<String, Set<ContextObserverMethod>> methods = mRegistrations.get(context);
        if (methods == null) {
            methods = new HashMap<String, Set<ContextObserverMethod>>();
            mRegistrations.put(context, methods);
        }

        Set<ContextObserverMethod> observers = methods.get(methodName);
        if (observers == null) {
            observers = new HashSet<ContextObserverMethod>();
            methods.put(methodName, observers);
        }

        observers.add(new ContextObserverMethod(instance, method, methodName));
    }

    public void unregisterObserver(Context context, Object instance, String method) {
        if (!isEnabled()) return;

        final Map<String, Set<ContextObserverMethod>> methods = mRegistrations.get(context);
        if (methods == null) return;

        final Set<ContextObserverMethod> observers = methods.get(method);
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

        final Map<String, Set<ContextObserverMethod>> methods = mRegistrations.get(context);
        if (methods == null) return;

        mRegistrations.remove(context);
        methods.clear();
    }

    public void notify(Context context, String methodName, Object... args) {
        if (!isEnabled()) return;

        final Map<String, Set<ContextObserverMethod>> methods = mRegistrations.get(context);
        if (methods == null) return;

        final Set<ContextObserverMethod> observers = methods.get(methodName);
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

    public Object notifyWithResult(Context context, String methodName, Object defaultReturn, Object... args) {
        if (!isEnabled()) return defaultReturn;

        final Map<String, Set<ContextObserverMethod>> methods = mRegistrations.get(context);
        if (methods == null) return defaultReturn;

        final Set<ContextObserverMethod> observers = methods.get(methodName);
        if (observers == null) return defaultReturn;

        for (ContextObserverMethod observerMethod : observers) {
            Object result = null;
            try {
                result = observerMethod.invoke(defaultReturn, args);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (result != null && !result.equals(defaultReturn)) return result;
        }

        return defaultReturn;
    }

    public static class NullContextObservationManager extends ContextObservationManager {
        @Override
        public boolean isEnabled() {
            return false;
        }
    }

    static class ContextObserverMethod {
        String contextMethodName;
        Method method;
        WeakReference<Object> instanceReference;

        public ContextObserverMethod(Object instance, Method method, String contextMethodName) {
            this.instanceReference = new WeakReference<Object>(instance);
            this.method = method;
            this.contextMethodName = contextMethodName;
        }

        public Object invoke(Object defaultReturn, Object... args) throws InvocationTargetException, IllegalAccessException {
            final Object instance = instanceReference.get();
            if (instance != null) {
                return method.invoke(instance, args);
            }
            return defaultReturn;
        }
    }
}
