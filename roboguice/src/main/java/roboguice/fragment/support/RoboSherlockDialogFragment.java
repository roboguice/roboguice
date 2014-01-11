package roboguice.fragment.support;

import roboguice.RoboGuice;

import com.actionbarsherlock.app.SherlockDialogFragment;

import android.os.Bundle;
import android.view.View;

/**
 * Provides an injected {@link SherlockDialogFragment} based on the support library v4 AND ActionBarSherlock.
 * A RoboSherlockDialogFragment will see all its members and views injected after {@link #onViewCreated(View, Bundle)}. 
 * @author Mickaël Burton 
 */
public abstract class RoboSherlockDialogFragment extends SherlockDialogFragment {
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
