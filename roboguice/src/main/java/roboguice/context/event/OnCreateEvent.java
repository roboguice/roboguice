package roboguice.context.event;

import android.content.Context;
import android.os.Bundle;

/**
 * Class representing the event raised by Context.onCreate()
 * <p/>
 * You may also be interested in roboguice.activity.event.OnContentViewAvailableEvent
 *
 * @author Adam Tybor
 * @author John Ericksen
 */
public class OnCreateEvent<T extends Context> {

    protected Bundle savedInstanceState;
    protected T context;

    public OnCreateEvent(T context, Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        this.context = context;
    }

    public Bundle getSavedInstanceState() {
        return savedInstanceState;
    }

    public T getContext() {
        return context;
    }
}
