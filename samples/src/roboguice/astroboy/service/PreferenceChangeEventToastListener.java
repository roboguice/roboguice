package roboguice.astroboy.service;

import android.content.Context;
import android.preference.Preference;
import com.google.inject.Inject;
import roboguice.activity.event.RoboActivityEventFactory;
import roboguice.inject.ContextObservationManager;

/**
 * @author John Ericksen
 **/
public class PreferenceChangeEventToastListener implements Preference.OnPreferenceChangeListener {

    @Inject
    private ContextObservationManager contextObserverManager;
    @Inject
    private RoboActivityEventFactory roboActivityEventFactory;
    @Inject
    private Context context;
    @Inject
    private ToastContextObserverService toastService;

    public boolean onPreferenceChange(Preference preference, Object o) {
        //raise the ToastEvent
        contextObserverManager.notify(context, toastService.buildToastEvent("hello toast"));
        return true;
    }
}
