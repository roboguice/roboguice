package roboguice.fragment.provided;

import roboguice.RoboGuice;

import android.annotation.TargetApi;
import android.app.ListFragment;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

/**
 * Provides an injected {@link ListFragment} based on the native HONEY_COMB+ fragments.
 * A RoboListFragment will see all its members and views injected after {@link #onViewCreated(View, Bundle)}.
 * @author Charles Munger
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public abstract class RoboListFragment extends ListFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RoboGuice.getInjector(getActivity()).injectMembersWithoutViews(this);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RoboGuice.getInjector(getActivity()).injectViewMembers(this);
    }
}
