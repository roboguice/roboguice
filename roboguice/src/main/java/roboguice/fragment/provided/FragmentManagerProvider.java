package roboguice.fragment.provided;

import android.app.Activity;
import android.app.FragmentManager;
import com.google.inject.Inject;
import com.google.inject.Provider;
import roboguice.inject.ContextSingleton;

@ContextSingleton
public class FragmentManagerProvider implements Provider<FragmentManager> {
    @Inject protected Activity activity;

    @Override
    public FragmentManager get() {
        return activity.getFragmentManager();
    }
}
