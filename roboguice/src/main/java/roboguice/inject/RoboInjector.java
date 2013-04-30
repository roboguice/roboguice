package roboguice.inject;

import android.app.Activity;

import com.google.inject.Injector;

public interface RoboInjector extends Injector {
    void injectViewMembers(Activity activity);
    void injectViewMembers(android.support.v4.app.Fragment fragment);
    void injectMembersWithoutViews(Object instance);
    void injectViewMembers(android.app.Fragment fragment);
}
