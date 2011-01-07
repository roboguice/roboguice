package roboguice.event;

import roboguice.util.Ln;

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
@SuppressWarnings({"unchecked"})
@Singleton
public class EventManager {
    protected Map<Context, Map<Class<?>, Set<ObserverReference<?>>>> registrations = new WeakHashMap<Context, Map<Class<?>, Set<ObserverReference<?>>>>();

    public boolean isEnabled() {
        return true;
    }

    /**
     * Registers given method with provided context and event.
     */
    public void registerObserver(Context context, Object instance, Method method, Class event) {
        if (!isEnabled()) return;

        final Returns returns = (Returns) event.getAnnotation(Returns.class);
        if( returns!=null && !returns.value().isAssignableFrom(method.getReturnType()) )
            throw new RuntimeException( String.format("Method %s.%s does not return a value that is assignable to %s",method.getDeclaringClass().getName(),method.getName(),returns.value().getName()) );

        Map<Class<?>, Set<ObserverReference<?>>> methods = registrations.get(context);
        if (methods == null) {
            methods = new HashMap<Class<?>, Set<ObserverReference<?>>>();
            registrations.put(context, methods);
        }

        Set<ObserverReference<?>> observers = methods.get(event);
        if (observers == null) {
            observers = new HashSet<ObserverReference<?>>();
            methods.put(event, observers);
        }
        observers.add(new ObserverReference(instance, method));
    }

    /**
     * Unregisters all methods observing the given event from the provided context.
     */
    public void unregisterObserver(Context context, Object instance, Class event) {
        if (!isEnabled()) return;

        final Map<Class<?>, Set<ObserverReference<?>>> methods = registrations.get(context);
        if (methods == null) return;

        final Set<ObserverReference<?>> observers = methods.get(event);
        if (observers == null) return;

        for (Iterator<ObserverReference<?>> iterator = observers.iterator(); iterator.hasNext();) {
            ObserverReference observer = iterator.next();
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
     */
    public void clear( Context context ) {
        final Map<Class<?>, Set<ObserverReference<?>>> methods = registrations.get(context);
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

        final Map<Class<?>, Set<ObserverReference<?>>> methods = registrations.get(context);
        if (methods == null) return;

        for(Class<?> aClass = event.getClass(); aClass != null; aClass = aClass.getSuperclass()){

            final Set<ObserverReference<?>> observers = methods.get(aClass);
            if (observers == null) return;

            for (ObserverReference observer : observers) {
                try {
                    observer.invoke(event,null);
                } catch (InvocationTargetException e) {
                    Ln.e(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
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
    public <ResultType> ResultType notifyWithResult(Context context, Object event, ResultType defaultValue ) {
        if (!isEnabled()) return defaultValue;

        final Map<Class<?>, Set<ObserverReference<?>>> methods = registrations.get(context);
        if (methods == null) return defaultValue;

        for(Class<?> aClass = event.getClass(); aClass != null; aClass = aClass.getSuperclass()){

            final Set<ObserverReference<?>> observers = methods.get(aClass);
            if (observers == null) return defaultValue;

            for (ObserverReference<?> o : observers) {
                final ObserverReference<ResultType> observer = (ObserverReference<ResultType>) o;
                try {
                    return observer.invoke( event, defaultValue);
                } catch (InvocationTargetException e) {
                    Ln.e(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return defaultValue;
    }

    public static class NullEventManager extends EventManager {
        @Override
        public boolean isEnabled() {
            return false;
        }
    }
    
    protected static class ObserverReference<ResultType> {
        protected Method method;
        protected WeakReference<Object> instanceReference;

        public ObserverReference(Object instance, Method method) {
            this.instanceReference = new WeakReference<Object>(instance);
            this.method = method;
            method.setAccessible(true);
        }

        public ResultType invoke(Object event, ResultType defaultValue ) throws InvocationTargetException, IllegalAccessException {
            final Object instance = instanceReference.get();
            return instance == null ? defaultValue : (ResultType) method.invoke(instance, event);

        }

    }
}
