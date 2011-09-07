package roboguice.fragment;

import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import roboguice.RobolectricRoboTestRunner;
import roboguice.activity.RoboFragmentActivity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.google.inject.Inject;

import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricRoboTestRunner.class)
public class FragmentInjectionTest {

    @Test
    public void shadowFragmentActivityGetApplicationContextShouldNotReturnNull() {
        Assert.assertNotNull(new FragmentActivity().getApplicationContext());
    }

    @Ignore("Robolectric doesn't have a FragmentManager shadow and mine isn't working yet")
    @Test
    public void shouldInjectPojosIntoFragments() {
        final RoboFragmentActivityA activity = new RoboFragmentActivityA();
        activity.onCreate(null);
        assertNotNull(activity.fragmentManager);
    }

    @Ignore("not implemented yet")
    @Test
    public void shouldInjectViewsIntoFragments() {
        throw new UnsupportedOperationException();
    }

    public static class RoboFragmentActivityA extends RoboFragmentActivity {
        @Inject FragmentManager fragmentManager;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
    }
}
