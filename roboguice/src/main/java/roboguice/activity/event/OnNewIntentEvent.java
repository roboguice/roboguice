package roboguice.activity.event;

import android.app.Activity;

/**
 * Class representing the event raised by RoboActivity.onNewIntent()
 *
 * @author Adam Tybor
 * @author John Ericksen
 */
public class OnNewIntentEvent {
    protected Activity activity;

    public OnNewIntentEvent(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }
}
