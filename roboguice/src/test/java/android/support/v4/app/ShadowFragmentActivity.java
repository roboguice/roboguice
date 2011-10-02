package android.support.v4.app;

import com.xtremelabs.robolectric.internal.Implementation;
import com.xtremelabs.robolectric.internal.Implements;
import com.xtremelabs.robolectric.shadows.ShadowActivity;

import java.lang.reflect.Constructor;

@Implements(FragmentActivity.class)
public class ShadowFragmentActivity extends ShadowActivity {

    @Implementation
    public FragmentManager getSupportFragmentManager() {
        final FragmentManager tmp = new FragmentManagerImpl();
        return tmp;
    }
}
