package roboguice.inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import roboguice.activity.RoboActivity;
import roboguice.service.RoboService;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class ExtrasListenerTest {

    @Test
    public void shouldInjectActivity() {
        final MyRoboActivity a1 = Robolectric.buildActivity(MyRoboActivity.class).create().get();
        assertThat(a1.foo, equalTo(10));
    }

    @Test
    public void shouldInjectService() {
        final MyRoboService s1 = new MyRoboService();
        try {
            s1.onCreate();
            assertTrue(false);
        } catch( Exception e ) {
            // great
        }

    }

    protected static class MyRoboActivity extends RoboActivity {
        @InjectExtra("foo") protected int foo;

        @Override
        public Intent getIntent() {
            return new Intent(this,RoboActivity.class).putExtra("foo", 10);
        }
    }

    protected static class MyRoboService extends RoboService {
        @InjectExtra("foo") protected int foo;

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        
    }
}
