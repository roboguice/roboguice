package roboguice.fragment.event;

import android.os.Bundle;

/**
 * Class representing the event raised by RoboFragment.onCreate()
 *
 * You may also be interested in roboguice.fragment.provided.event.OnViewCreatedEvent
 *
 * @author Adam Tybor
 * @author John Ericksen
 * @author Cherry Development
 */
public class OnCreateEvent<T> extends AbstractFragmentEvent<T> {

    protected Bundle savedInstanceState;

    public OnCreateEvent(T fragment, Bundle savedInstanceState) {
        super(fragment);
        this.savedInstanceState = savedInstanceState;
    }

    public Bundle getSavedInstanceState() {
        return savedInstanceState;
    }
}
