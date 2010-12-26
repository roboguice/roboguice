package roboguice.astroboy.service;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;
import com.google.inject.Inject;
import roboguice.inject.ContextObserver;
import roboguice.util.Ln;

public class ContextObservingService {
    @Inject Context context;

    @ContextObserver
    public void onCreate(Bundle icicle) {
        Ln.v("onCreate");
    }

    @ContextObserver
    public void onPause() {
        Ln.v("onPause");
    }

    @ContextObserver
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Ln.v("onKeyDown %1$s", event);
        if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            Toast.makeText(context, "You pressed the search button", Toast.LENGTH_SHORT).show();
            // return that this is event is handled so the search box doesn't open
            return true;
        }
        // Let android handle anything other than search
        return false;
    }
}
