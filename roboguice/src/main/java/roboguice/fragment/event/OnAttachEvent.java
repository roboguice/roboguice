package roboguice.fragment.event;

import android.app.Activity;

/**
 * Class representing the event raised by RoboFragment.onAttach()
 *
 * @author Cherry Development
 */
public class OnAttachEvent<T> extends AbstractFragmentEvent<T> {

    protected final Activity activity;

    public OnAttachEvent(T fragment, Activity activity) {
        super(fragment);
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }
}

