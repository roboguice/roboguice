package roboguice.shadow;

import com.xtremelabs.robolectric.internal.Implementation;
import com.xtremelabs.robolectric.internal.Implements;
import com.xtremelabs.robolectric.shadows.ShadowActivity;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import java.lang.reflect.Constructor;

@Implements(FragmentActivity.class)
public class ShadowFragmentActivity extends ShadowActivity {

    @Implementation
    public FragmentManager getSupportFragmentManager() {
        try {
            final Class c = Class.forName("android.support.v4.app.FragmentManagerImpl");
            final Constructor constructor = c.getDeclaredConstructor();
            constructor.setAccessible(true);
            return (FragmentManager) constructor.newInstance();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
