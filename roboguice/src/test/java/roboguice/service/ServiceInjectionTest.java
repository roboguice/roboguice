package roboguice.service;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class ServiceInjectionTest {

    @Test
    public void shouldBeAbleToInjectInRoboService() {
        final RoboServiceA roboService = new RoboServiceA();
        roboService.onCreate();

        assertThat(roboService.context, equalTo((Context) roboService));
    }

    @Test
    public void shouldBeAbleToInjectInRoboIntentService() {
        final RoboIntentServiceA roboService = new RoboIntentServiceA("");
        roboService.onCreate();

        assertThat(roboService.context, equalTo((Context) roboService));
    }

    public static class RoboServiceA extends RoboService {
        @Inject Context context;

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

    public static class RoboIntentServiceA extends RoboIntentService {
        @Inject Context context;

        public RoboIntentServiceA(String name) {
            super(name);
        }

        @Override
        protected void onHandleIntent(Intent intent) {
        }
    }
}
