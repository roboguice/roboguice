package roboguice.event;

import android.content.Context;
import com.google.inject.Singleton;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Manager class handling the following:
 *
 *   Registration of event observing methods:
 *      registerObserver()
 *      unregisterObserver()
 *      clear()
 *   Raising Events:
 *      notify()
 *      notifyWithResult()
 *
 * @author Adam Tybor
 * @author John Ericksen
 */
@Singleton
public class EventManager {

    protected final Map<Context, Map<Class, Set<ContextObserverReference>>> registrations;

    public EventManager() {
        registrations = new WeakHashMap<Context, Map<Class, Set<ContextObserverReference>>>();
    }

    public boolean isEnabled() {
        return true;
    }

    /**
     * Registers given method with provided context and event.
     *
     * @param context
     * @param instance
     * @param method
     * @param event
     */
    public void registerObserver(Context context, Object instance, Method method, Class event) {
        if (!isEnabled()) return;

        Map<Class, Set<ContextObserverReference>> methods = registrations.get(context);
        if (methods == null) {
            methods = new HashMap<Class, Set<ContextObserverReference>>();
            registrations.put(context, methods);
        }

        Set<ContextObserverReference> observers = methods.get(event);
        if (observers == null) {
            observers = new HashSet<ContextObserverReference>();
            methods.put(event, observers);
        }
        observers.add(new ContextObserverReference(instance, method));
    }

    /**
     * UnRegisters all methods observing the given event from the provided context.
     *
     * @param context
     * @param instance
     * @param event
     */
    public void unregisterObserver(Context context, Object instance, Class event) {
        if (!isEnabled()) return;

        final Map<Class, Set<ContextObserverReference>> methods = registrations.get(context);
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

    /**
     * Clears all observers of the given context.
     * @param context
     */
    public void clear(Context context) {
        if (!isEnabled()) return;

        final Map<Class, Set<ContextObserverReference>> methods = registrations.get(context);
        if (methods == null) return;

        registrations.remove(context);
        methods.clear();
    }

    /**
     * Raises the event's class' event on the given context.  This event object is passed (if configured) to the
     * registered observer's method.
     *
     * @param context
     * @param event
     */
    public void notify(Context context, Object event) {
        if (!isEnabled()) return;

        final Map<Class, Set<ContextObserverReference>> methods = registrations.get(context);
        if (methods == null) return;

        for(Class subClassLoop = event.getClass(); subClassLoop != null; subClassLoop = subClassLoop.getSuperclass()){
            //register class and all super classes, for inheritance based event handling.
            final Set<ContextObserverReference> observers = methods.get(subClassLoop);
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
    }

    /**
     * Raises the event's class' event on the given context.  This event object is passed (if configured) to the
     * registered observer's method.
     *
     * A result handler can be provided to deal with the return values from the invoked observer methods.
     *
     * @param context
     * @param event
     */
    public void notifyWithResult(Context context, Object event, EventResultHandler resultHandler) {
        if (!isEnabled()) return;

        final Map<Class, Set<ContextObserverReference>> methods = registrations.get(context);
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

    public static class NullEventManager extends EventManager {
        @Override
        public boolean isEnabled() {
            return false;
        }
    }
    
    private static class ContextObserverReference {
        protected final Method method;
        protected final WeakReference<Object> instanceReference;

        public ContextObserverReference(Object instance, Method method) {
            this.instanceReference = new WeakReference<Object>(instance);
            this.method = method;
        }

        public void invoke(EventResultHandler resultHandler, Object event) throws InvocationTargetException, IllegalAccessException {
            final Object instance = instanceReference.get();
            final EventResultHandler innerResultHandler = resultHandler == null? new NoOpResultHandler() : resultHandler;
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
