package roboguice.inject;

import com.google.inject.Inject;
import com.google.inject.Provider;

import android.app.Application;

public class FragmentScopeProvider implements Provider<FragmentScope> {
    @Inject protected Application application;

    public FragmentScope get() {
        return new FragmentScope(application);
    }
}
