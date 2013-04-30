package roboguice.provided.fragment;

import roboguice.inject.ContextSingleton;

import android.app.Activity;
import android.app.FragmentManager;

import com.google.inject.Inject;
import com.google.inject.Provider;

@ContextSingleton
public class FragmentManagerProvider implements Provider<FragmentManager> {
    @Inject protected Activity activity;

    @Override
    public FragmentManager get() {
        return activity.getFragmentManager();
    }
}
