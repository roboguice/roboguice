package roboguice.astroboy.service;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.Toast;
import com.google.inject.Inject;
import roboguice.activity.event.*;
import roboguice.event.Observer;
import roboguice.event.Observers;
import roboguice.event.Observes;
import roboguice.util.Ln;

/**
 * @author John Ericksen
 */
public class HybridObservingService {
    @Inject
    protected Context context;

    @Observer(OnCreateEvent.class)
    public void onCreate() {
        Ln.v("onCreate");
    }

    @Observers({
            @Observer(OnPauseEvent.class),
            @Observer(OnStopEvent.class),
            @Observer(OnDestroyEvent.class)
    })
    public void onOutOfScopeLogger() {
        Ln.v("onOutOfScope; No idea which method was called");
    }

    public void onKeyDown(@Observes OnKeyDownEvent onKeyDownEvent) {
        Ln.v("onKeyDown %1$s", onKeyDownEvent.getEvent());
        if (onKeyDownEvent.getKeyCode() == KeyEvent.KEYCODE_SEARCH) {
            Toast.makeText(context, "You pressed the search button", Toast.LENGTH_SHORT).show();
        }
    }
}
