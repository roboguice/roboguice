package roboguice.inject;

import com.google.inject.Injector;

import android.app.Activity;

public interface RoboInjector extends Injector {
    void injectViewMembers(Activity activity);
    void injectViewMembers(android.support.v4.app.Fragment fragment);
    void injectMembersWithoutViews(Object instance);
    void injectViewMembers(android.app.Fragment fragment);
}
