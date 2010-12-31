package roboguice.astroboy.service;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;
import com.google.inject.Inject;
import roboguice.activity.RoboActivityEvent;
import roboguice.inject.ContextObserver;
import roboguice.inject.ContextObservers;
import roboguice.util.Ln;

public class ContextObservingService {
    @Inject Context context;

    @ContextObserver(RoboActivityEvent.ON_CREATE)
    public void onCreate(Bundle icicle) {
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

    @ContextObserver(RoboActivityEvent.ON_KEY_DOWN)
    public void onKeyDown(){
         Ln.v("onKeyDownCalled");
    }

    @ContextObserver(RoboActivityEvent.ON_KEY_DOWN)
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Ln.v("onKeyDown %1$s", event);
        if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            Toast.makeText(context, "You pressed the search button", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}
