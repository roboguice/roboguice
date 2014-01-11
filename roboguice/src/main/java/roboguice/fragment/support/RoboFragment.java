package roboguice.fragment.support;

import roboguice.RoboGuice;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Provides an injected {@link Fragment} based on the support library v4.
 * A RoboFragment will see all its members and views injected after {@link #onViewCreated(View, Bundle)}. 
 * @author Charles Munger
 */
public abstract class RoboFragment extends Fragment {
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
