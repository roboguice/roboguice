package roboguice.inject;

import com.google.inject.Inject;
import com.google.inject.Provider;

import android.app.Activity;
import android.app.Application;

public class GuiceApplicationProvider<T extends Application> implements Provider<T>{
    @Inject protected Provider<Activity> activityProvider;

    @SuppressWarnings("unchecked")
    public T get() {
        return (T) activityProvider.get().getApplication();
    }

}
