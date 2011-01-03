package roboguice.astroboy.service;

import roboguice.inject.ContextObservationManager;

import android.preference.Preference;

import com.google.inject.Inject;

/**
 * @author John Ericksen
 **/
public class PreferenceChangeEventToastListener implements Preference.OnPreferenceChangeListener {

    @Inject protected ContextObservationManager contextObserverManager;
    @Inject protected ToastContextObserverService toastService;

    public boolean onPreferenceChange(Preference preference, Object o) {
        //raise the ToastEvent
        contextObserverManager.notify(toastService.buildToastEvent("hello toast"));
        return true;
    }
}
