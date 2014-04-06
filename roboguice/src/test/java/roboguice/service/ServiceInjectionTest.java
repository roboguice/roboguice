package roboguice.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import roboguice.RoboGuice;
import roboguice.inject.InjectView;

import com.google.inject.ConfigurationException;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;

@RunWith(RobolectricTestRunner.class)
public class ServiceInjectionTest {

    @Before 
    public void setup() {
        RoboGuice.useAnnotationDatabases = false;
    }

    @Test
    public void shouldBeAbleToInjectInRoboService() {
        final RoboServiceA roboService = new RoboServiceA();
        roboService.onCreate();

        assertThat( roboService.context, equalTo((Context)roboService) );
    }

    @Test
    public void shouldBeAbleToInjectInRoboIntentService() {
        final RoboIntentServiceA roboService = new RoboIntentServiceA("");
        roboService.onCreate();

        assertThat( roboService.context, equalTo((Context)roboService) );
    }

    @Test(expected=ConfigurationException.class)
    public void shouldNotAllowViewsInServices() {
        final RoboServiceB roboService = new RoboServiceB();
        roboService.onCreate();
    }

    static public class RoboServiceA extends RoboService {
        @Inject Context context;

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

    static public class RoboIntentServiceA extends RoboIntentService {
        @Inject Context context;

        public RoboIntentServiceA(String name) {
            super(name);
        }

        @Override
        protected void onHandleIntent(Intent intent) {
        }
    }

    static public class RoboServiceB extends RoboService {
        @InjectView(100) View v;

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

}
