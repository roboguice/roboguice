package roboguice.activity;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import roboguice.RoboGuice;

import android.content.Context;

import com.google.inject.Injector;

import static junit.framework.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class ActivityContextLifetimeTest {
    @Test
    public void test_should_make_application_context_active_on_startup() {
        assertEquals(getActiveContext(), Robolectric.application);
    }

    @Test
    public void test_should_make_activity_context_active_during_onCreate() {
        RoboActivity activity = new RoboActivity();
        activity.onCreate(null);
        assertEquals(activity, getActiveContext());
    }

    @Test
    public void test_should_revert_active_context_after_onStop() {
        RoboActivity activity1 = new RoboActivity();
        activity1.onCreate(null);
        assertEquals(activity1, getActiveContext());

        RoboActivity activity2 = new RoboActivity();
        activity2.onCreate(null);
        assertEquals(activity2, getActiveContext());

        activity1.onStop();
        assertEquals(activity2, getActiveContext());
    }

    @Test
    public void test_should_revert_active_context_after_onDestory() {
        RoboActivity activity1 = new RoboActivity();
        activity1.onCreate(null);
        assertEquals(activity1, getActiveContext());

        RoboActivity activity2 = new RoboActivity();
        activity2.onCreate(null);
        assertEquals(activity2, getActiveContext());

        activity1.onDestroy();
        assertEquals(activity2, getActiveContext());
    }

    protected Context getActiveContext() {
        return getInjector().getInstance(Context.class);
    }

    protected Injector getInjector() {
        return RoboGuice.getApplicationInjector(Robolectric.application);
    }
}
