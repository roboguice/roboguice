package roboguice.fragment.support;

import roboguice.RoboGuice;

import com.actionbarsherlock.app.SherlockListFragment;

import android.os.Bundle;
import android.view.View;

/**
 * Provides an injected {@link SherlockListFragment} based on the support library v4 AND ActionBarSherlock.
 * A RoboSherlockListFragment will see all its members and views injected after {@link #onViewCreated(View, Bundle)}. 
 * @author Mickaël Burton 
 */
public abstract class RoboSherlockListFragment extends SherlockListFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RoboGuice.getInjector(getActivity()).injectMembersWithoutViews(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RoboGuice.getInjector(getActivity()).injectViewMembers(this);
    }
}
