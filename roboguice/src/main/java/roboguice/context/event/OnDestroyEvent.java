package roboguice.context.event;

import android.content.Context;

/**
 * Class representing the event raised by Context.onDestroy()
 *
 * @author Adam Tybor
 * @author John Ericksen
 */
public class OnDestroyEvent<T extends Context> {
    protected T context;

    public OnDestroyEvent(T context) {
        this.context = context;
    }

    public T getContext() {
        return context;
    }
}
