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
    
    protected Map<Context, Map<Class<?>, Set<EventListener<?>>>> registrations = new WeakHashMap<Context, Map<Class<?>, Set<EventListener<?>>>>();

    public boolean isEnabled() {
        return true;
    }

    public <T> void registerObserver( Context context, Class<T> event, EventListener listener ) {
        if (!isEnabled()) return;

        if( context instanceof Application )
            throw new RuntimeException("You may not register event handlers on the Application context");

        Map<Class<?>, Set<EventListener<?>>> methods = registrations.get(context);
        if (methods == null) {
            methods = new HashMap<Class<?>, Set<EventListener<?>>>();
            registrations.put(context, methods);
        }

        Set<EventListener<?>> observers = methods.get(event);
        if (observers == null) {
            observers = new HashSet<EventListener<?>>();
            methods.put(event, observers);
        }

        observers.add(listener);

    }

    /**
     * Registers given method with provided context and event.
     */
    public <T> void registerObserver(Context context, Object instance, Method method, Class<T> event) {
        registerObserver(context, event, new ObserverMethodListener<T>( new ObserverReference<T>(instance, method)));
    }

    public <T> void unregisterObserver(Context context, Class<T> event, EventListener<T> listener ) {
        if (!isEnabled()) return;

        final Map<Class<?>, Set<EventListener<?>>> methods = registrations.get(context);
        if (methods == null) return;

        final Set<EventListener<?>> observers = methods.get(event);
        if (observers == null) return;

        for (Iterator<EventListener<?>> iterator = observers.iterator(); iterator.hasNext();) {
            final EventListener registeredListener = iterator.next();
            if (registeredListener == listener) {
                iterator.remove();
                break;
            }
        }
    }

    /**
     * Unregisters all methods observing the given event from the provided context.
     */
    public <T> void unregisterObserver(Context context, Object instance, Class<T> event) {
        if (!isEnabled()) return;

        final Map<Class<?>, Set<EventListener<?>>> methods = registrations.get(context);
        if (methods == null) return;

        final Set<EventListener<?>> observers = methods.get(event);
        if (observers == null) return;

        for (Iterator<EventListener<?>> iterator = observers.iterator(); iterator.hasNext();) {
            final EventListener listener = iterator.next();
            if( listener instanceof ObserverMethodListener ) {
                final ObserverReference observer = ((ObserverMethodListener)listener).observer;
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
        final Map<Class<?>, Set<EventListener<?>>> methods = registrations.get(context);
        if (methods == null) return;

        registrations.remove(context);
        methods.clear();
    }

    /**
     * Raises the event's class' event on the current context.  This event object is passed (if configured) to the
     * registered observer's method.
     */
    public void fire( Object event ) {
        fire(contextProvider.get(), event);
    }

    /**
     * Raises the event's class' event on the given context.  This event object is passed (if configured) to the
     * registered observer's method.
     *
     * @param context
     * @param event
     */
    public void fire(Context context, Object event) {
        if (!isEnabled()) return;

        final Map<Class<?>, Set<EventListener<?>>> methods = registrations.get(context);
        if (methods == null) return;


        final Set<EventListener<?>> observers = methods.get(event.getClass());
        if (observers == null) return;

        for (EventListener observer : observers)
            observer.onEvent(event);

    }

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

    public static class ObserverMethodListener<T> implements EventListener<T> {
        protected ObserverReference<T> observer;

        public ObserverMethodListener(ObserverReference<T> observer) {
            this.observer = observer;
        }

        @Override
        public void onEvent(T event) {
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
