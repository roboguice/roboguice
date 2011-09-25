package roboguice.inject;

import com.google.inject.Injector;

public interface RoboInjector extends Injector {
    void injectViewMembers(Object instance);
    void injectMembersWithoutViews(Object instance);
}
