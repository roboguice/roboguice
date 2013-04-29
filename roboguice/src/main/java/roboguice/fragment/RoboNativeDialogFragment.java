package roboguice.fragment;

import roboguice.RoboGuice;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
public abstract class RoboNativeDialogFragment extends DialogFragment {
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
