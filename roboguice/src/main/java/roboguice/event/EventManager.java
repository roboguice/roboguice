package roboguice.event;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import roboguice.event.eventListener.ObserverMethodListener;
import roboguice.inject.ContextSingleton;

import com.google.inject.Inject;

import android.content.Context;
import android.os.Handler;

/**
 * Manager class handling the following:
 * 
 * Registration of event observing methods: registerObserver()
 * unregisterObserver() clear() Raising Events: fire() notifyWithResult()
 * 
 * @author Adam Tybor
 * @author John Ericksen
 */
@ContextSingleton
public class EventManager {
    @Inject
    protected Context context;

    protected Map<Class<?>, Set<EventListener<?>>> registrations = new HashMap<Class<?>, Set<EventListener<?>>>(); // synchronized
    // set
    protected Map<Class<?>, EventProducer<?>> productions = new HashMap<Class<?>, EventProducer<?>>(); // synchronized
    protected Map<Class<?>, Object> stickyEvents = new HashMap<Class<?>, Object>(); // synchronized

    // set

    /**
     * Register the given EventListener to the contest and event class.
     * 
     * @param event
     *            observed
     * @param listener
     *            to be triggered
     * @param <T>
     *            event type
     */
    public <T> void registerObserver(final Class<T> event, final EventListener listener) {
        Set<EventListener<?>> observers = registrations.get(event);
        if (observers == null) {
            observers = Collections.synchronizedSet(new LinkedHashSet<EventListener<?>>());
            registrations.put(event, observers);
        }

        if (stickyEvents.get(event) != null) {
            // we post a message that will be executed asap bu the main thread
            // of this event manager's context
            new Handler(context.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    listener.onEvent(stickyEvents.get(event));
                }
            });
        } else if (productions.get(event) != null) {
            // we post a message that will be executed asap bu the main thread
            // of this event manager's context
            new Handler(context.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    listener.onEvent(productions.get(event).onEventRequested());
                }
            });
        }

        observers.add(listener);

    }

    /**
     * Registers given method with provided context and event.
     * 
     * @param instance
     *            to be called
     * @param method
     *            to be called
     * @param event
     *            observed
     */
    public <T> void registerObserver(Object instance, Method method, Class<T> event) {
        registerObserver(event, new ObserverMethodListener<T>(instance, method));
    }

    /**
     * Register the given EventListener to the contest and event class.
     * 
     * @param event
     *            observed
     * @param listener
     *            to be triggered
     * @param <T>
     *            event type
     */
    public <T> void registerProducer(Class<T> event, EventProducer producer) {
        EventProducer previousProducer = productions.get(event);
        if (previousProducer != null) {
            throw new RuntimeException("A producer is already registered for event type " + event.getSimpleName() + " :" + previousProducer);
        }
        productions.put(event, producer);
    }

    /**
     * Unregisters the provided event listener from the given event
     * 
     * @param event
     *            observed
     * @param listener
     *            to be unregistered
     * @param <T>
     *            event type
     */
    public <T> void unregisterObserver(Class<T> event, EventListener<T> listener) {

        final Set<EventListener<?>> observers = registrations.get(event);
        if (observers == null) {
            return;
        }

        // As documented in
        // http://docs.oracle.com/javase/1.4.2/docs/api/java/util/Collections.html#synchronizedSet(java.util.Set)
        // noinspection SynchronizationOnLocalVariableOrMethodParameter
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
     * Unregister all methods observing the given event from the provided
     * context.
     * 
     * @param instance
     *            to be unregistered
     * @param event
     *            observed
     */
    public <T> void unregisterObserver(Object instance, Class<T> event) {

        final Set<EventListener<?>> observers = registrations.get(event);
        if (observers == null) {
            return;
        }

        // As documented in
        // http://docs.oracle.com/javase/1.4.2/docs/api/java/util/Collections.html#synchronizedSet(java.util.Set)
        // noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (observers) {
            for (Iterator<EventListener<?>> iterator = observers.iterator(); iterator.hasNext();) {
                final EventListener listener = iterator.next();
                if (listener instanceof ObserverMethodListener) {
                    final ObserverMethodListener observer = (ObserverMethodListener) listener;
                    if (observer.getInstance() == instance) {
                        iterator.remove();
                        break;
                    }
                }
            }
        }
    }

    /**
     * Raises the event's class' event on the given context. This event object
     * is passed (if configured) to the registered observer's method.
     * 
     * @param event
     *            observed
     */
    public void fire(Object event) {

        final Set<EventListener<?>> observers = registrations.get(event.getClass());
        if (observers == null) {
            return;
        }

        // As documented in
        // http://docs.oracle.com/javase/1.4.2/docs/api/java/util/Collections.html#synchronizedSet(java.util.Set)
        // noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (observers) {
            for (EventListener observer : observers) {
                observer.onEvent(event);
            }
        }

        if (event.getClass().isAnnotationPresent(StickyEvent.class)) {
            stickyEvents.put(event.getClass(), event);
        }

    }

    public void destroy() {
        for (Entry<Class<?>, Set<EventListener<?>>> e : registrations.entrySet()) {
            e.getValue().clear();
        }
        registrations.clear();
        productions.clear();
    }

}
