package roboguice.event;

import roboguice.event.eventListener.ObserverMethodListener;
import roboguice.inject.ContextSingleton;

import android.content.Context;

import com.google.inject.Inject;

import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

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
@ContextSingleton
public class EventManager {
    @Inject protected Context context;

    protected Map<Class<?>, Set<EventListener<?>>> registrations = new HashMap<Class<?>, Set<EventListener<?>>>(); // synchronized set

    /**
     * Register the given EventListener to the contest and event class.
     *
     * @param event observed
     * @param listener to be triggered
     * @param <T> event type
     */
    public <T> void registerObserver( Class<T> event, EventListener listener ) {
        Set<EventListener<?>> observers = registrations.get(event);
        if (observers == null) {
            observers = Collections.synchronizedSet(new LinkedHashSet<EventListener<?>>());
            registrations.put(event, observers);
        }

        observers.add(listener);

    }

    /**
     * Registers given method with provided context and event.
     *
     * @param instance to be called
     * @param method to be called
     * @param event observed
     */
    public <T> void registerObserver(Object instance, Method method, Class<T> event) {
        registerObserver(event, new ObserverMethodListener<T>(instance, method));
    }

    /**
     * Unregisters the provided event listener from the given event
     *
     * @param event observed
     * @param listener to be unregistered
     * @param <T> event type
     */
    public <T> void unregisterObserver(Class<T> event, EventListener<T> listener ) {

        final Set<EventListener<?>> observers = registrations.get(event);
        if (observers == null) return;

        // As documented in http://docs.oracle.com/javase/1.4.2/docs/api/java/util/Collections.html#synchronizedSet(java.util.Set)
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (observers) {
            for (Iterator<EventListener<?>> iterator = observers.iterator(); iterator.hasNext();) {
                final EventListener registeredListener = iterator.next();
                if (registeredListener == listener) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

    /**
     * Unregister all methods observing the given event from the provided context.
     *
     * @param instance to be unregistered
     * @param event observed
     */
    public <T> void unregisterObserver(Object instance, Class<T> event) {

        final Set<EventListener<?>> observers = registrations.get(event);
        if (observers == null) return;

        // As documented in http://docs.oracle.com/javase/1.4.2/docs/api/java/util/Collections.html#synchronizedSet(java.util.Set)
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (observers) {
            for (Iterator<EventListener<?>> iterator = observers.iterator(); iterator.hasNext();) {
                final EventListener listener = iterator.next();
                if( listener instanceof ObserverMethodListener ) {
                    final ObserverMethodListener observer = ((ObserverMethodListener)listener);
                    if (observer.getInstance() == instance) {
                        iterator.remove();
                        break;
                    }
                }
            }
        }
    }

    /**
     * Raises the event's class' event on the given context.  This event object is passed (if configured) to the
     * registered observer's method.
     *
     * @param event observed
     */
    public void fire(Object event) {

        final Set<EventListener<?>> observers = registrations.get(event.getClass());
        if (observers == null) return;

        // As documented in http://docs.oracle.com/javase/1.4.2/docs/api/java/util/Collections.html#synchronizedSet(java.util.Set)
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (observers) {
            for (EventListener observer : observers)
                observer.onEvent(event);
        }

    }
    
    
    public void destroy() {
        for( Entry<Class<?>, Set<EventListener<?>>> e : registrations.entrySet() )
            e.getValue().clear();
        registrations.clear();
    }

}
