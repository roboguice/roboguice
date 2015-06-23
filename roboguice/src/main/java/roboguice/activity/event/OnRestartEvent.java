package roboguice.activity.event;

import android.app.Activity;

/**
 * Class representing the event raised by RoboActivity.onRestart()
 *
 * @author Adam Tybor
 * @author John Ericksen
 */
public class OnRestartEvent {
    protected Activity activity;

    public OnRestartEvent(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }
}
