package roboguice.inject;

import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import roboguice.activity.RoboActivity;

import android.os.Bundle;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class ResourceListenerTest {

    @Ignore("Need to configure roboguice build to use maven android plugin for tests at least so it can access resources")
    @Test
    public void shouldInjectStaticResources() {

        new MyActivity().onCreate(null);

        assertEquals("Cancel", MyActivity.cancel);
    }


    public static class MyActivity extends RoboActivity {
        @InjectResource(android.R.string.cancel) protected static String cancel;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
    }
}
