package roboguice.additionaltests;

import android.content.Context;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import roboguice.activity.RoboActivity;
import roboguice.additionaltests.view.ShouldInjectCustomViewsView;
import roboguice.inject.ContentView;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * These tests require Android resources, so cannot be run
 * from the roboguice module (which is not an android project
 * and has no resource processing step)
 */
@RunWith(RobolectricTestRunner.class)
public class ViewInjectionTest {

    @Test
    @Ignore("Unplugged till Mike reviews it. See RoboActivity.shouldInject...")
    public void shouldInjectCustomViews() {
        final A a = Robolectric.buildActivity(A.class).create().get();
        final ShouldInjectCustomViewsView customView = (ShouldInjectCustomViewsView) a.findViewById(R.id.shouldInjectCustomView);
        assertThat(customView.context, equalTo((Context)a));
        assertThat(customView.textView.getId(), equalTo(100));
    }

    @ContentView(R.layout.should_inject_custom_views)
    public static class A extends RoboActivity {
    }

}
