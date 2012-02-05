package roboguice.event.eventListener;

import roboguice.event.EventListener;
import roboguice.event.eventListener.javaassist.RuntimeSupport;
import roboguice.util.Ln;

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
    protected Object instance;

    public ObserverMethodListener(Object instance, Method method) {
        this.instance = instance;
        this.method = method;
        //This descriptor is used in the equals and hashcode method to compare
        //methods between super-classes, subclasses and interface declarations.
        this.descriptor = method.getName() + ':' + RuntimeSupport.makeDescriptor(method);
        method.setAccessible(true);
    }

    public void onEvent(Object event) {
        try {
            method.invoke(instance, event);
        } catch (InvocationTargetException e) {
            Ln.e(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getInstance() {
        return instance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObserverMethodListener that = (ObserverMethodListener) o;

        if (descriptor != null ? !descriptor.equals(that.descriptor) : that.descriptor != null) return false;
        return !(instance != null ? !instance.equals(that.instance) : that.instance != null);

    }

    @Override
    public int hashCode() {
        int result = descriptor != null ? descriptor.hashCode() : 0;
        result = 31 * result + (instance != null ? instance.hashCode() : 0);
        return result;
    }

}
