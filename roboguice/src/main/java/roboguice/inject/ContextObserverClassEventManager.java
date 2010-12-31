package roboguice.inject;

import android.content.Context;
import com.google.inject.Singleton;
import roboguice.util.Ln;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Context Observer manager dealing with the events handled by the @ContextObserves parameter annotation
 *
 * @author John Ericksen
 */
@Singleton
public class ContextObserverClassEventManager {

    private final Map<Context, Map<Class, Set<ContextObserverMethod>>> mRegistrations;

    public ContextObserverClassEventManager() {
        mRegistrations  = new WeakHashMap<Context, Map<Class, Set<ContextObserverMethod>>>();
    }

    public boolean isEnabled() {
        return true;
    }

    public void registerObserver(Context context, Object instance, Method method, Class event) {
        if (!isEnabled()) return;

        Map<Class, Set<ContextObserverMethod>> methods = mRegistrations.get(context);
        if (methods == null) {
            methods = new HashMap<Class, Set<ContextObserverMethod>>();
            mRegistrations.put(context, methods);
        }

        Set<ContextObserverMethod> observers = methods.get(event);
        if (observers == null) {
            observers = new HashSet<ContextObserverMethod>();
            methods.put(event, observers);
        }

        if(method.getReturnType() != void.class){
            Ln.i("ContextObserver method: " + method.getDeclaringClass() + "." + method.getName() +
                    " has non-void return type. Return value will be ignored during observer call.");
        }

        observers.add(new ContextObserverMethod(instance, method));
    }

    public void unregisterObserver(Context context, Object instance, String event) {
        if (!isEnabled()) return;

        final Map<Class, Set<ContextObserverMethod>> methods = mRegistrations.get(context);
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

        final Map<Class, Set<ContextObserverMethod>> methods = mRegistrations.get(context);
        if (methods == null) return;

        mRegistrations.remove(context);
        methods.clear();
    }

    public void notify(Context context, Object event) {
        if (!isEnabled()) return;

        final Map<Class, Set<ContextObserverMethod>> methods = mRegistrations.get(context);
        if (methods == null) return;

        final Set<ContextObserverMethod> observers = methods.get(event.getClass());
        if (observers == null) return;

        for (ContextObserverMethod observerMethod : observers) {
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

        final Map<Class, Set<ContextObserverMethod>> methods = mRegistrations.get(context);
        if (methods == null) return;
        
        final Set<ContextObserverMethod> observers = methods.get(event.getClass());
        if (observers == null) return;

        for (ContextObserverMethod observerMethod : observers) {
            try {
                observerMethod.invoke(resultHandler, event);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static class NullContextObservationManager extends ContextObserverClassEventManager {
        @Override
        public boolean isEnabled() {
            return false;
        }
    }

    private static class ContextObserverMethod {
        private Method method;
        private WeakReference<Object> instanceReference;

        public ContextObserverMethod(Object instance, Method method) {
            this.instanceReference = new WeakReference<Object>(instance);
            this.method = method;
        }

        public void invoke(EventResultHandler resultHandler, Object event) throws InvocationTargetException, IllegalAccessException {
            final Object instance = instanceReference.get();
            EventResultHandler innerResultHandler = resultHandler == null? new NoOpResultHandler() : resultHandler;
            if (instance != null) {
                innerResultHandler.handleReturn(method.invoke(instance, event));
            }
        }
    }
}
