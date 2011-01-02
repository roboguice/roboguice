package roboguice.astroboy.service;

import android.content.Context;
import android.preference.Preference;
import com.google.inject.Inject;
import roboguice.activity.event.RoboActivityEventFactory;
import roboguice.event.EventManager;

/**
 * @author John Ericksen
 **/
public class PreferenceChangeEventToastListener implements Preference.OnPreferenceChangeListener {

    @Inject
    protected EventManager contextObserverManager;
    @Inject
    protected RoboActivityEventFactory roboActivityEventFactory;
    @Inject
    protected Context context;
    @Inject
    protected ToastContextObserverService toastService;

    public boolean onPreferenceChange(Preference preference, Object o) {
        //raise the ToastEvent
        contextObserverManager.notify(context, toastService.buildToastEvent("hello toast"));
        return true;
    }
}
