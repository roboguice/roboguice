package roboguice.activity.event;

import android.app.Activity;

/**
 * Class representing the event raised by RoboActivity.onStop()
 *
 * @author Adam Tybor
 * @author John Ericksen
 */
public class OnStopEvent {
    protected Activity activity;

    public OnStopEvent(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }
}
