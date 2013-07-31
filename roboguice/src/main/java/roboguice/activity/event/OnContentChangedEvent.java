package roboguice.activity.event;

import android.app.Activity;

/**
 * Class representing the event raised by RoboActivity.onContentChanged()
 *
 * @author Adam Tybor
 * @author John Ericksen
 */
public class OnContentChangedEvent {
    protected Activity activity;

    public OnContentChangedEvent(Activity activity) {
    }

    public Activity getActivity() {
        return activity;
    }
}