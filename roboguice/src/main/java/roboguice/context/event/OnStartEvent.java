package roboguice.context.event;

import android.content.Context;

/**
 * Class representing the event raised by Context.onStart()
 *
 * @author Adam Tybor
 * @author John Ericksen
 */
public class OnStartEvent<T extends Context> {
    protected T context;

    public OnStartEvent(T context) {
        this.context = context;
    }

    public T getContext() {
        return context;
    }
}
