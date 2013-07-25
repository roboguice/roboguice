package roboguice.inject;

import android.os.Bundle;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import roboguice.activity.RoboActivity;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class ResourceListenerTest {

    @Ignore("Need to configure roboguice build to use maven android plugin for tests at least so it can access resources")
    @Test
    public void shouldInjectResources() {
        final A a = new A();
        a.onCreate(null);

        assertEquals("Cancel", a.cancel1);
        assertEquals("Cancel", a.cancel2);
    }

    @Ignore("Need to configure roboguice build to use maven android plugin for tests at least so it can access resources")
    @Test
    public void shouldInjectStaticResources() {
        new B().onCreate(null);

        assertEquals("Cancel", B.cancel);
    }


    public static class A extends RoboActivity {
        @InjectResource(android.R.string.cancel) protected String cancel1;
        @InjectResource(name="org.roboguice:string/cancel") protected String cancel2;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
    }


    public static class B extends RoboActivity {
        @InjectResource(android.R.string.cancel) protected static String cancel;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
    }
}
