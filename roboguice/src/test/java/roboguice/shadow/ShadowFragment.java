package roboguice.shadow;

import com.xtremelabs.robolectric.internal.Implementation;
import com.xtremelabs.robolectric.internal.Implements;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

@Implements(Fragment.class)
public class ShadowFragment {
    protected FragmentActivity activity;

    @Implementation
    public FragmentActivity getActivity() {
        return activity;
    }

    @Implementation
    public void onAttach( Activity a ) {
        activity = (FragmentActivity)a;
    }
}
