package roboguice.fragment;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Key;

import roboguice.RoboGuice;
import roboguice.util.RoboContext;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Provides an injected {@link Fragment} based on support library v4.
 * A RoboFragment will see all its members and views injected after {@link #onViewCreated(View, Bundle)}.
 * @author Michael Burton
 */
public abstract class RoboFragment extends Fragment implements RoboContext {
    protected HashMap<Key<?>,Object> scopedObjects = new HashMap<Key<?>, Object>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RoboGuice.getInjector(this).injectMembersWithoutViews(this);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RoboGuice.getInjector(this).injectViewMembers(this);
    }
    
    @Override
    public Map<Key<?>, Object> getScopedObjectMap() {
        return scopedObjects;
    }

    @Override
    public void onDestroy() {
        RoboGuice.destroyInjector(this);
        super.onDestroy();
    }
}
