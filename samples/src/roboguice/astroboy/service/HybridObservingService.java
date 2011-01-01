package roboguice.astroboy.service;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.Toast;
import com.google.inject.Inject;
import roboguice.activity.event.*;
import roboguice.inject.ContextObserver;
import roboguice.inject.ContextObservers;
import roboguice.inject.ContextObserves;
import roboguice.util.Ln;

/**
 * @author John Ericksen
 */
public class HybridObservingService {
    @Inject
    Context context;

    @ContextObserver(OnCreateEvent.class)
    public void onCreate() {
        Ln.v("onCreate");
    }

    @ContextObservers({
            @ContextObserver(OnPauseEvent.class),
            @ContextObserver(OnStopEvent.class),
            @ContextObserver(OnDestroyEvent.class)
    })
    public void onOutOfScopeLogger() {
        Ln.v("onOutOfScope; No idea which method was called");
    }

    public void onKeyDown(@ContextObserves OnKeyDownEvent onKeyDownEvent) {
        Ln.v("onKeyDown %1$s", onKeyDownEvent.getEvent());
        if (onKeyDownEvent.getKeyCode() == KeyEvent.KEYCODE_SEARCH) {
            Toast.makeText(context, "You pressed the search button", Toast.LENGTH_SHORT).show();
        }
    }
}
