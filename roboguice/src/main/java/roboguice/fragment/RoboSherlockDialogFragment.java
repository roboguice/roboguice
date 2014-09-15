package roboguice.fragment;

import roboguice.RoboGuice;

import com.actionbarsherlock.app.SherlockDialogFragment;

import android.os.Bundle;

/**
 * Provides an injected {@link SherlockDialogFragment} based on ActionBarSherlock's Dialog Fragments.
 * A RoboSherlockDialogFragment will see all its members and views injected after {@link #onViewCreated(View, Bundle)}. 
 * @author Michael Burton
 */
public abstract class RoboSherlockDialogFragment extends SherlockDialogFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RoboGuice.getInjector(getActivity()).injectMembersWithoutViews(this);
    }
}
