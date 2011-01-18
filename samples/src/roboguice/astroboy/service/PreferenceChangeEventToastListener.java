package roboguice.astroboy.service;


import roboguice.astroboy.service.ToastContextObserverService.ToastEvent;
import roboguice.event.EventManager;

import android.content.Context;
import android.preference.Preference;

import com.google.inject.Inject;

/**
 * @author John Ericksen
 **/
public class PreferenceChangeEventToastListener implements Preference.OnPreferenceChangeListener {

    @Inject protected Context context;
    @Inject protected EventManager eventManager;
    @Inject protected ToastContextObserverService toastService;

    public boolean onPreferenceChange(Preference preference, Object o) {
        //raise the ToastEvent
        eventManager.fire(context, new ToastEvent("hello toast"));
        return true;
    }
}
