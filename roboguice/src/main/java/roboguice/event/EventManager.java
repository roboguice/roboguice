package roboguice.event;

import roboguice.event.javaassist.RuntimeSupport;
import roboguice.inject.ContextSingleton;
import roboguice.util.Ln;

import com.google.inject.Inject;

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
@ContextSingleton
public class EventManager {
    @Inject protected android.content.Context context;

    protected Map<Class<?>, Set<EventListener<?>>> registrations = new HashMap<Class<?>, Set<EventListener<?>>>();

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
            observers = new HashSet<EventListener<?>>();
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
     * @param instance to be unregistered
     * @param event observed
     */
    public <T> void unregisterObserver(Object instance, Class<T> event) {

        final Set<EventListener<?>> observers = registrations.get(event);
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
     * Raises the event's class' event on the given context.  This event object is passed (if configured) to the
     * registered observer's method.
     *
     * @param event observed
     */
    public void fire(Object event) {

        final Set<EventListener<?>> observers = registrations.get(event.getClass());
        if (observers == null) return;

        for (EventListener observer : observers)
            observer.onEvent(event);

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
                if (instance != null) {
                    method.invoke(instance, event);
                } else {
                    Ln.w("trying to observe event %1$s on disposed context, consider explicitly calling EventManager.unregisterObserver", method.getName());
                }
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
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
