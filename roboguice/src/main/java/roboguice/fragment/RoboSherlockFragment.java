package roboguice.fragment;

import roboguice.RoboGuice;

import com.actionbarsherlock.app.SherlockFragment;

import android.os.Bundle;
import android.view.View;

/**
 * Provides an injected {@link SherlockFragment} based on ActionBarSherlock's Fragments.
 * A RoboSherlockFragment will see all its members and views injected after {@link #onViewCreated(View, Bundle)}. 
 * @author Michael Burton
 */
public abstract class RoboSherlockFragment extends SherlockFragment {
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
