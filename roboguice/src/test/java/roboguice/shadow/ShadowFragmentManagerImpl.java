package roboguice.shadow;

import com.xtremelabs.robolectric.internal.Implementation;

import android.support.v4.app.FragmentTransaction;

//@Implements(FragmentManagerImpl.class) This implements FragmentManagerImpl.class, but FragmentManagerImpl.class is not public so we can't reference it directly
public class ShadowFragmentManagerImpl {

    @Implementation
    public FragmentTransaction beginTransaction() {
        throw new UnsupportedOperationException();
    }
}
