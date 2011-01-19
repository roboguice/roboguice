package roboguice.event;

import roboguice.util.Ln;

import android.app.Application;
import android.content.Context;

import com.google.inject.Inject;
import com.google.inject.Provider;
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
 *      fire()
 *      notifyWithResult()
 *
 * @author Adam Tybor
 * @author John Ericksen
 */
@SuppressWarnings({"unchecked"})
@Singleton
public class EventManager {
    @Inject protected Provider<Context> contextProvider;
    
    protected Map<Context, Map<Class<?>, Set<ObserverReference<?>>>> registrations = new WeakHashMap<Context, Map<Class<?>, Set<ObserverReference<?>>>>();

    public boolean isEnabled() {
        return true;
    }

    /**
     * Registers given method with provided context and event.
     */
    public void registerObserver(Context context, Object instance, Method method, Class event) {
        if (!isEnabled()) return;

        if( context instanceof Application )
            throw new RuntimeException("You may not register event handlers on the Application context");

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

        /*
        final Returns returns = (Returns) event.getAnnotation(Returns.class);
        if( returns!=null ) {
            if( !returns.value().isAssignableFrom(method.getReturnType()) )
                throw new RuntimeException( String.format("Method %s.%s does not return a value that is assignable to %s",method.getDeclaringClass().getName(),method.getName(),returns.value().getName()) );

            if( !observers.isEmpty() ) {
                final ObserverReference observer = observers.iterator().next();
                throw new RuntimeException( String.format("Only one observer allowed for event types that return a value annotation.  Previously registered observer is %s.%s", observer.method.getDeclaringClass().getName(), observer.method.getName()));
            }
        }
        */

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
     * Raises the event's class' event on the current context.  This event object is passed (if configured) to the
     * registered observer's method.
     */
    public void fire( Object event ) {
        fire(contextProvider.get(),event);
    }

    /**
     * Raises the event's class' event on the given context.  This event object is passed (if configured) to the
     * registered observer's method.
     *
     * @param context
     * @param event
     */
    protected void fire(Context context, Object event) {
        if (!isEnabled()) return;

        /*
        if( event.getClass().getAnnotation(Returns.class)!=null )
            throw new RuntimeException("You must use notifyWithResult for events that expect return values");
        */

        final Map<Class<?>, Set<ObserverReference<?>>> methods = registrations.get(context);
        if (methods == null) return;


        final Set<ObserverReference<?>> observers = methods.get(event.getClass());
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

    /**
     * Raises the event's class' event on the given context.  This event object is passed (if configured) to the
     * registered observer's method.
     *
     * A result handler can be provided to deal with the return values from the invoked observer methods.
     *
     * @param context
     * @param event
     */
    /*
    // Disabled for now until we can figure out best way to proceed
    public <ResultType> ResultType notifyWithResult(Context context, Object event, ResultType defaultValue ) {
        if (!isEnabled()) return defaultValue;

        if( event.getClass().getAnnotation(Returns.class)==null )
            throw new RuntimeException("You must use fire with events that do not expect return values");

        final Map<Class<?>, Set<ObserverReference<?>>> methods = registrations.get(context);
        if (methods == null) return defaultValue;

        final Set<ObserverReference<?>> observers = methods.get(event.getClass());
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

        return defaultValue;
    }
    */

    public static class NullEventManager extends EventManager {
        @Override
        public boolean isEnabled() {
            return false;
        }
    }
    
    public static class ObserverReference<ResultType> {
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
