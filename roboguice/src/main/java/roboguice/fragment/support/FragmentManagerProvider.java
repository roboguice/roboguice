package roboguice.fragment.support;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import com.google.inject.Inject;
import com.google.inject.Provider;
import roboguice.inject.ContextSingleton;

@ContextSingleton
public class FragmentManagerProvider implements Provider<FragmentManager> {
    @Inject protected Activity activity;

    @Override
    public FragmentManager get() {
        return ((FragmentActivity)activity).getSupportFragmentManager();
    }
}
