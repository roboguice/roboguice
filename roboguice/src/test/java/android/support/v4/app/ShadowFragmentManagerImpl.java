package android.support.v4.app;

import com.xtremelabs.robolectric.internal.Implementation;
import com.xtremelabs.robolectric.internal.Implements;

import android.support.v4.app.FragmentTransaction;

@Implements(FragmentManagerImpl.class)
public class ShadowFragmentManagerImpl {

    @Implementation
    public FragmentTransaction beginTransaction() {
        throw new UnsupportedOperationException();
    }
}
