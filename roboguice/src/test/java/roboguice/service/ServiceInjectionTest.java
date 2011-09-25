package roboguice.service;

import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class ServiceInjectionTest {

    @Test
    public void shouldBeAbleToInjectInRoboService() {
        final MyRoboService roboService = new MyRoboService();
        roboService.onCreate();

        assertThat( roboService.context, equalTo((Context)roboService) );
    }

    @Test
    public void shouldBeAbleToInjectInRoboIntentService() {
        final MyRoboIntentService roboService = new MyRoboIntentService("");
        roboService.onCreate();

        assertThat( roboService.context, equalTo((Context)roboService) );
    }

    static public class MyRoboService extends RoboService {
        @Inject Context context;

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

    static public class MyRoboIntentService extends RoboIntentService {
        @Inject Context context;

        public MyRoboIntentService(String name) {
            super(name);
        }

        @Override
        protected void onHandleIntent(Intent intent) {
        }
    }
}
