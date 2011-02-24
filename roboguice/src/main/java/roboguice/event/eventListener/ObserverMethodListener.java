package roboguice.event.eventListener;

import roboguice.event.EventListener;
import roboguice.event.eventListener.javaassist.RuntimeSupport;
import roboguice.util.Ln;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

    public void onEvent(Object event) {
        try {
            final Object instance = instanceReference.get();
            method.invoke(instance, event);
        } catch (InvocationTargetException e) {
            Ln.e(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public WeakReference<Object> getInstanceReference() {
        return instanceReference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObserverMethodListener that = (ObserverMethodListener) o;

        if (descriptor != null ? !descriptor.equals(that.descriptor) : that.descriptor != null) return false;
        Object thisInstance = instanceReference.get();
        Object thatInstance = that.instanceReference.get();
        return !(thisInstance != null ? !thisInstance.equals(thatInstance) : thatInstance != null);

    }

    @Override
    public int hashCode() {
        int result = descriptor != null ? descriptor.hashCode() : 0;
        Object thisInstance = instanceReference.get();
        result = 31 * result + (thisInstance != null ? thisInstance.hashCode() : 0);
        return result;
    }

}
