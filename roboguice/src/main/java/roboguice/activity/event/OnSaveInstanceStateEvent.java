package roboguice.activity.event;

import android.app.Activity;
import android.os.Bundle;

public class OnSaveInstanceStateEvent {

    protected Activity activity;
    protected Bundle savedInstanceState;

    public OnSaveInstanceStateEvent(Activity activity, Bundle savedInstanceState) {
        this.activity = activity;
        this.savedInstanceState = savedInstanceState;
    }

    public Bundle getSavedInstanceState() {
        return savedInstanceState;
    }

    public Activity getActivity() {
        return activity;
    }
}
