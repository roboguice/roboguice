package roboguice.astroboy.service;

import android.content.Context;
import android.preference.Preference;
import com.google.inject.Inject;
import roboguice.activity.RoboActivityEventFactory;
import roboguice.inject.ContextObserverClassEventManager;

/**
 * @author John Ericksen
 **/
public class PreferenceChangeEventToastListener implements Preference.OnPreferenceChangeListener {

    @Inject
    private ContextObserverClassEventManager contextObserverClassEventManager;
    @Inject
    private RoboActivityEventFactory roboActivityEventFactory;
    @Inject
    private Context context;
    @Inject
    private ToastContextObserverService toastService;

    public boolean onPreferenceChange(Preference preference, Object o) {
        //raise the ToastEvent
        contextObserverClassEventManager.notify(context, toastService.buildToastEvent("hello toast"));
        return true;
    }
}
