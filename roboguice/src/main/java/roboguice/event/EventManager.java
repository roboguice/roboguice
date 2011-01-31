package roboguice.event;

import roboguice.event.javaassist.RuntimeSupport;
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

    /**
     * Register an observer EventListener with the current context (provided).
     *
     * @param event to observe
     * @param listener to be triggered
     * @param <T> event type
     */
    public <T> void registerObserver( Class<T> event, EventListener listener ) {
        registerObserver(contextProvider.get(),event,listener);
    }

    /**
     * Register a method observer with the current context (provided).
     *
     * @param instance to be called
     * @param method to be called
     * @param event observed
     * @param <T> event type
     */
    public <T> void registerObserver(Object instance, Method method, Class<T> event) {
        registerObserver(contextProvider.get(),instance,method,event);
    }

     /**
     * Unregister the given EventListener with the current context (provided).
     *
     * @param event observed
     * @param listener to be unregistered
     * @param <T> event type
     */
    public <T> void unregisterObserver(Class<T> event, EventListener<T> listener ) {
        unregisterObserver(contextProvider.get(),event,listener);
    }

    /**
     * Unregister the given event from the current context (provided).
     *
     * @param instance to be unregistered
     * @param event observed
     * @param <T> event type
     */
    public <T> void unregisterObserver(Object instance, Class<T> event) {
        unregisterObserver(contextProvider.get(),instance,event);
    }

    /**
     * Raises the event's class' event on the current context.  This event object is passed (if configured) to the
     * registered observer's method.
     *
     * @param event observed
     */
    public void fire( Object event ) {
        fire(contextProvider.get(), event);
    }

    /**
     * Register the given EventListener to the contest and event class.
     *
     * @param context associated with event
     * @param event observed
     * @param listener to be triggered
     * @param <T> event type
     */
    public <T> void registerObserver( Context context, Class<T> event, EventListener listener ) {
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
     *
     * @param context associated with event
     * @param instance to be called
     * @param method to be called
     * @param event observed
     */
    public <T> void registerObserver(Context context, Object instance, Method method, Class<T> event) {
        registerObserver(context, event, new ObserverMethodListener<T>(instance, method));
    }

    /**
     * Unregisters the provided event listener from the given event
     *
     * @param context associated with event
     * @param event observed
     * @param listener to be unregistered
     * @param <T> event type
     */
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
     * Unregister all methods observing the given event from the provided context.
     *
     * @param context associated with event
     * @param instance to be unregistered
     * @param event observed
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
                final ObserverMethodListener observer = ((ObserverMethodListener)listener);
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
     *
     * @param context associated with event
     */
    public void clear( Context context ) {
        final Map<Class<?>, Set<EventListener<?>>> methods = registrations.get(context);
        if (methods == null) return;

        registrations.remove(context);
        methods.clear();
    }

    /**
     * Raises the event's class' event on the given context.  This event object is passed (if configured) to the
     * registered observer's method.
     *
     * @param context associated with event
     * @param event observed
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

    public static class ObserverMethodListener<T> implements EventListener<T> {
        protected String descriptor;
        protected Method method;
        protected WeakReference<Object> instanceReference;

        public ObserverMethodListener(Object instance, Method method) {
            this.instanceReference = new WeakReference<Object>(instance);
            this.method = method;
            this.descriptor = method.getName() + ':' + RuntimeSupport.makeDescriptor(method);
            method.setAccessible(true);
        }

        public void onEvent(T event) {
            try {
                final Object instance = instanceReference.get();
                method.invoke(instance, event);
            } catch (InvocationTargetException e) {
                Ln.e(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final ObserverMethodListener that = (ObserverMethodListener) o;

            if (descriptor != null ? !descriptor.equals(that.descriptor) : that.descriptor != null) return false;
            final Object thisInstance = instanceReference.get();
            final Object thatInstance = that.instanceReference.get();
            return !(thisInstance != null ? !thisInstance.equals(thatInstance) : thatInstance != null);

        }

        @Override
        public int hashCode() {
            int result = descriptor != null ? descriptor.hashCode() : 0;
            final Object thisInstance = instanceReference.get();
            result = 31 * result + (thisInstance != null ? thisInstance.hashCode() : 0);
            return result;
        }

    }
}
