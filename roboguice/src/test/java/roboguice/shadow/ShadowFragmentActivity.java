package roboguice.shadow;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.internal.Implementation;
import com.xtremelabs.robolectric.internal.Implements;
import com.xtremelabs.robolectric.shadows.ShadowActivity;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

@Implements(FragmentActivity.class)
public class ShadowFragmentActivity extends ShadowActivity {

    @Implementation
    public FragmentManager getSupportFragmentManager() {
        return Robolectric.newInstanceOf(FragmentManager.class);
    }
}
