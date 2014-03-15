package roboguice.event.eventListener;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import roboguice.event.EventListener;
import roboguice.event.eventListener.javaassist.RuntimeSupport;
import roboguice.util.Ln;

/**
 * Observer Method Event Listener.  This class calls to the method on the given instance during onEvent().
 *
 * @author Adam Tybor
 * @author John Ericksen
 */
public class ObserverMethodListener<T> implements EventListener<T> {
    protected String descriptor;
    protected Method method;
    protected WeakReference<Object> instanceReference;

    public ObserverMethodListener(Object instance, Method method) {
        this.instanceReference = new WeakReference<Object>(instance);
        this.method = method;
        //This descriptor is used in the equals and hashcode method to compare
        //methods between super-classes, subclasses and interface declarations.
        this.descriptor = method.getName() + ':' + RuntimeSupport.makeDescriptor(method);
        method.setAccessible(true);
    }

    /**
     * Invokes observable method on instance for which it was registered
     *
     * @param event fired
     */
    public void onEvent(Object event) {
        final Object instance = getInstance();

        if (instance == null) {
            return;
        }

        try {
            method.invoke(instance, event);
        } catch (InvocationTargetException e) {
            Ln.e(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return instance for which method was registered
     */
    public Object getInstance() {
        return instanceReference.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObserverMethodListener<?> that = (ObserverMethodListener<?>) o;

        final Object instance = getInstance();
        final Object thatInstance = that.getInstance();

        if (descriptor != null ? !descriptor.equals(that.descriptor) : that.descriptor != null)
            return false;
        return !(instance != null ? !instance.equals(thatInstance) : thatInstance != null);

    }

    @Override
    public int hashCode() {
        final Object instance = getInstance();

        int result = descriptor != null ? descriptor.hashCode() : 0;
        final int prime = 31;
        result = prime * result + (instance != null ? instance.hashCode() : 0);
        return result;
    }

}
