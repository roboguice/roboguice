package roboguice.astroboy.service;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.Toast;
import com.google.inject.Inject;
import roboguice.activity.RoboActivityEvent;
import roboguice.activity.RoboActivityEventFactory;
import roboguice.inject.ContextObserver;
import roboguice.inject.ContextObservers;
import roboguice.inject.ContextParameterObserver;
import roboguice.util.Ln;

/**
 * @author John Ericksen
 */
public class HybridObservingService {
    @Inject
    Context context;

    @ContextObserver(RoboActivityEvent.ON_CREATE)
    public void onCreate() {
        Ln.v("onCreate");
    }

    @ContextObservers({
            @ContextObserver(RoboActivityEvent.ON_PAUSE),
            @ContextObserver(RoboActivityEvent.ON_STOP),
            @ContextObserver(RoboActivityEvent.ON_DESTROY)
    })
    public void onOutOfScopeLogger() {
        Ln.v("onOutOfScope; No idea which method was called");
    }

    public void onKeyDown(@ContextParameterObserver RoboActivityEventFactory.OnKeyDownEvent onKeyDownEvent) {
        Ln.v("onKeyDown %1$s", onKeyDownEvent.getEvent());
        if (onKeyDownEvent.getKeyCode() == KeyEvent.KEYCODE_SEARCH) {
            Toast.makeText(context, "You pressed the search button", Toast.LENGTH_SHORT).show();
        }
    }
}
