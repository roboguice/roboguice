package roboguice.test.shadow;

import com.xtremelabs.robolectric.internal.Implementation;
import com.xtremelabs.robolectric.internal.Implements;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

@Implements(Fragment.class)
public class ShadowFragment {
    protected FragmentActivity activity;
    protected View view;

    @Implementation
    public FragmentActivity getActivity() {
        return activity;
    }

    @Implementation
    public View getView() {
        return view;
    }

    @Implementation
    public void onViewCreated( View v, Bundle savedInstanceState ) {
        view = v;
    }

    @Implementation
    public void onAttach( Activity a ) {
        activity = (FragmentActivity)a;
    }
}
