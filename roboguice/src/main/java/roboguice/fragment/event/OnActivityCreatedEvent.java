package roboguice.fragment.event;

import android.os.Bundle;

/**
 * Class representing the event raised by RoboFragment.onActivityCreated()
 *
 * @author Cherry Development
 */
public class OnActivityCreatedEvent<T> extends AbstractFragmentEvent<T> {

    protected final Bundle savedInstanceState;

    public OnActivityCreatedEvent(T fragment, Bundle savedInstanceState) {
        super(fragment);
        this.savedInstanceState = savedInstanceState;
    }

    public Bundle getSavedInstanceState() {
        return savedInstanceState;
    }
}
