package roboguice.additionaltests;

import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import roboguice.activity.RoboActivity;
import roboguice.additionaltests.view.CustomViewUnderTest;
import roboguice.inject.ContentView;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * These tests require Android resources, so cannot be run
 * from the roboguice module (which is not an android project
 * and has no resource processing step)
 */
@RunWith(RobolectricTestRunner.class)
public class ViewInjectionTest {

    @Test
    public void shouldInjectCustomViewsAutomaticallyFromXml() {
        final A a = Robolectric.buildActivity(A.class).create().get();
        final CustomViewUnderTest customView = (CustomViewUnderTest) a.findViewById(R.id.shouldInjectCustomView);
        assertThat(customView.getContext(), equalTo((Context)a));
        assertThat(customView.textView.getId(), equalTo(100));
        assertThat(customView.textView.getText(), notNullValue());
        assertThat(customView.vibrator, notNullValue());
    }

    @Test
    public void shouldInjectCustomViewsAutomaticallyFromCode() {
        final A a = Robolectric.buildActivity(A.class).create().get();
        final CustomViewUnderTest customView = new CustomViewUnderTest(a);
        assertThat(customView.getContext(), equalTo((Context)a));
        assertThat(customView.textView.getId(), equalTo(100));
        assertThat(customView.textView.getText(), notNullValue());
        assertThat(customView.vibrator, notNullValue());
    }

    @ContentView(R.layout.should_inject_custom_views)
    public static class A extends RoboActivity {
    }

}
