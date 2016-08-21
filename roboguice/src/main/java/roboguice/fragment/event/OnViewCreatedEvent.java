package roboguice.fragment.event;

import android.os.Bundle;
import android.view.View;

/**
 * Class representing the event raised by RoboFragment.onViewCreated()
 *
 * @author Cherry Development
 */
public class OnViewCreatedEvent<T> extends AbstractFragmentEvent<T> {

    protected final View view;
    protected final Bundle savedInstanceState;

    public OnViewCreatedEvent(T fragment, View view, Bundle savedInstanceState) {
        super(fragment);
        this.view = view;
        this.savedInstanceState = savedInstanceState;
    }

    public View getView() {
        return view;
    }

    public Bundle getSavedInstanceState() {
        return savedInstanceState;
    }
}
