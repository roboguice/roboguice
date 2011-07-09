package roboguice.inject;

import android.app.Activity;
import android.view.View;

import com.google.inject.Injector;

public interface RoboInjector extends Injector {
    void injectViewMembers(View root);
    void injectViewMembers(Activity activity);
}
