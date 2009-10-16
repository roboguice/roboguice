package roboguice.inject;

import com.google.inject.Inject;
import com.google.inject.Provider;

import android.app.Activity;
import android.content.Context;

public class ActivityProvider implements Provider<Activity> {
    @Inject Provider<Context> contextProvider;

    public Activity get() {
        return (Activity) contextProvider.get();
    }

}
