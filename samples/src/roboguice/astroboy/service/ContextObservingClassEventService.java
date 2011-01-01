package roboguice.astroboy.service;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.Toast;
import com.google.inject.Inject;
import roboguice.activity.event.OnCreateEvent;
import roboguice.activity.event.OnDestroyEvent;
import roboguice.activity.event.OnKeyDownEvent;
import roboguice.inject.ContextObserves;
import roboguice.util.Ln;

/**
 * Example of the ContextObserves usage with the defined RoboActivity Events
 *
 * @author John Ericksen
 */
public class ContextObservingClassEventService {

    @Inject
    private Context context;

    public void logOnCreate(@ContextObserves OnCreateEvent event) {
        Ln.v("onCreate");
    }

    public void logOnDestroy(@ContextObserves OnDestroyEvent event){
        Ln.v("onDestroy");
    }
    
    public boolean toastKeyDown(@ContextObserves OnKeyDownEvent onKeyDownEvent) {
        Ln.v("onKeyDown %1$s", onKeyDownEvent.getEvent());
        if (onKeyDownEvent.getKeyCode() == KeyEvent.KEYCODE_SEARCH) {
            Toast.makeText(context, "You pressed the search button", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}
