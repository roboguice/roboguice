package roboguice.inject;

import android.content.Context;
import com.google.inject.Singleton;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Singleton
public class ContextObservationManager {

    private final Map<Context, Map<Class, Set<ContextObserverReference>>> mRegistrations;

    public ContextObservationManager() {
        mRegistrations  = new WeakHashMap<Context, Map<Class, Set<ContextObserverReference>>>();
    }

    public boolean isEnabled() {
        return true;
    }

    public void registerObserver(Context context, Object instance, Method method, Class event) {
        if (!isEnabled()) return;

        Map<Class, Set<ContextObserverReference>> methods = mRegistrations.get(context);
        if (methods == null) {
            methods = new HashMap<Class, Set<ContextObserverReference>>();
            mRegistrations.put(context, methods);
        }

        Set<ContextObserverReference> observers = methods.get(event);
        if (observers == null) {
            observers = new HashSet<ContextObserverReference>();
            methods.put(event, observers);
        }

        observers.add(new ContextObserverReference(instance, method));
    }

    public void unregisterObserver(Context context, Object instance, String event) {
        if (!isEnabled()) return;

        final Map<Class, Set<ContextObserverReference>> methods = mRegistrations.get(context);
        if (methods == null) return;

        final Set<ContextObserverReference> observers = methods.get(event);
        if (observers == null) return;

        for (Iterator<ContextObserverReference> iterator = observers.iterator(); iterator.hasNext();) {
            ContextObserverReference observer = iterator.next();
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

        final Map<Class, Set<ContextObserverReference>> methods = mRegistrations.get(context);
        if (methods == null) return;

        mRegistrations.remove(context);
        methods.clear();
    }

    public void notify(Context context, Object event) {
        if (!isEnabled()) return;

        final Map<Class, Set<ContextObserverReference>> methods = mRegistrations.get(context);
        if (methods == null) return;

        final Set<ContextObserverReference> observers = methods.get(event.getClass());
        if (observers == null) return;

        for (ContextObserverReference observerMethod : observers) {
            try {
                observerMethod.invoke(null, event);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void notifyWithResult(Context context, Object event, EventResultHandler resultHandler) {
        if (!isEnabled()) return;

        final Map<Class, Set<ContextObserverReference>> methods = mRegistrations.get(context);
        if (methods == null) return;

        final Set<ContextObserverReference> observers = methods.get(event.getClass());
        if (observers == null) return;

        for (ContextObserverReference observerMethod : observers) {
            try {
                observerMethod.invoke(resultHandler, event);
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
    
    private static class ContextObserverReference {
        private Method method;
        private WeakReference<Object> instanceReference;

        public ContextObserverReference(Object instance, Method method) {
            this.instanceReference = new WeakReference<Object>(instance);
            this.method = method;
        }

        public void invoke(EventResultHandler resultHandler, Object event) throws InvocationTargetException, IllegalAccessException {
            final Object instance = instanceReference.get();
            EventResultHandler innerResultHandler = resultHandler == null? new NoOpResultHandler() : resultHandler;
            if (instance != null) {
                Class[] paramTypes = method.getParameterTypes();
                if(paramTypes.length == 0){
                    //empty parameters
                    innerResultHandler.handleReturn(method.invoke(instance));
                }
                else{
                    innerResultHandler.handleReturn(method.invoke(instance, event));
                }
            }
        }
    }
}
