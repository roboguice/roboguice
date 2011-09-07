package roboguice.shadow;

import com.xtremelabs.robolectric.internal.Implements;
import com.xtremelabs.robolectric.internal.RealObject;

import android.support.v4.app.FragmentManager;

@Implements(FragmentManager.class)
public class ShadowFragmentManager {
    @RealObject private FragmentManager fragmentManager;
}
