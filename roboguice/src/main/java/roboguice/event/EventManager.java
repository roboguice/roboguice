package roboguice.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
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
    protected Map<Class<?>, List<Object>> stickyEvents = new HashMap<Class<?>, List<Object>>(); // synchronized

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
    @SuppressWarnings("unchecked")
    public <T> void registerObserver(final Class<T> event, final EventListener<T> listener, int stickyEventsCountRequested) {
        Set<EventListener<?>> observers = registrations.get(event);
        if (observers == null) {
            observers = Collections.synchronizedSet(new LinkedHashSet<EventListener<?>>());
            registrations.put(event, observers);
        }

        if (stickyEvents.get(event) != null) {
            boolean allEvents = stickyEventsCountRequested == Observes.ALL_EVENTS;
            List<T> eventsList = (List<T>) stickyEvents.get(event);
            int stickyEventsToTriggerCount = allEvents ? 0 : Math.max(0, eventsList.size() - stickyEventsCountRequested);
            ListIterator<T> listIterator = eventsList.listIterator(stickyEventsToTriggerCount);
            while (listIterator.hasNext()) {
                fireStickyEvent(listIterator.next(), listener);
                System.out.println(event);
            }
        } else if (productions.get(event) != null) {
            fireStickyEvent(((EventProducer<T>) productions.get(event)).onEventRequested(), listener);
        }

        observers.add(listener);

    }

    private <T> void fireStickyEvent(final T event, final EventListener<T> listener) {
        // we post a message that will be executed asap bu the main thread
        // of this event manager's context
        new Handler(context.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                listener.onEvent(event);
            }
        });
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
    public <T> void registerObserver(Object instance, Method method, Class<T> event, int stickyEventsCount) {
        registerObserver(event, new ObserverMethodListener<T>(instance, method), stickyEventsCount);
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
    public <T> void registerProducer(Class<T> event, EventProducer<?> producer) {
        EventProducer<?> previousProducer = productions.get(event);
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
                final EventListener<?> registeredListener = iterator.next();
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
                final EventListener<?> listener = iterator.next();
                if (listener instanceof ObserverMethodListener) {
                    final ObserverMethodListener<?> observer = (ObserverMethodListener<?>) listener;
                    if (observer.getInstance() == instance) {
                        iterator.remove();
                        break;
                    }
                }
            }
        }
    }

    public <T> void unregisterProducer(Class<T> event) {
        productions.remove(event);
    }

    public <T> void clearStickyEvents(Class<T> event) {
        stickyEvents.remove(event);
    }

    /**
     * Raises the event's class' event on the given context. This event object
     * is passed (if configured) to the registered observer's method.
     * 
     * @param event
     *            observed
     */
    @SuppressWarnings("unchecked")
    public <T> void fire(T event) {

        final Set<EventListener<?>> observers = registrations.get(event.getClass());

        if (event.getClass().isAnnotationPresent(StickyEvent.class)) {
            List<T> stickyEventsList = (List<T>) stickyEvents.get(event.getClass());
            if (stickyEventsList == null) {
                stickyEventsList = new ArrayList<T>();
                stickyEvents.put(event.getClass(), (List<Object>) stickyEventsList);
            }
            stickyEventsList.add(event);
        }

        if (observers == null) {
            return;
        }

        // As documented in
        // http://docs.oracle.com/javase/1.4.2/docs/api/java/util/Collections.html#synchronizedSet(java.util.Set)
        // noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (observers) {
            for (EventListener<?> observer : observers) {
                ((EventListener<T>) observer).onEvent(event);
            }
        }

    }

    public void destroy() {
        for (Entry<Class<?>, Set<EventListener<?>>> e : registrations.entrySet()) {
            e.getValue().clear();
        }
        registrations.clear();
        productions.clear();

        for (Entry<Class<?>, List<Object>> e : stickyEvents.entrySet()) {
            e.getValue().clear();
        }
        stickyEvents.clear();
    }

}
