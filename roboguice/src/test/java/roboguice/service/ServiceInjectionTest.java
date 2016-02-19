package roboguice.service;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import roboguice.RoboGuice;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class ServiceInjectionTest {

    @Before
    public void setUp() throws Exception {
        RoboGuice.setupBaseApplicationInjector(Robolectric.application);
    }

    @After
    public void tearDown() throws Exception {
        RoboGuice.Util.reset();
    }

    @Test
    public void shouldBeAbleToInjectInRoboService() {
        final TestRoboServiceA roboService = new TestRoboServiceA();
        roboService.onCreate();

        assertThat(roboService.context, equalTo((Context) roboService));
    }

    @Test
    public void shouldBeAbleToInjectInRoboIntentService() {
        final TestRoboIntentServiceA roboService = new TestRoboIntentServiceA("");
        roboService.onCreate();

        assertThat(roboService.context, equalTo((Context) roboService));
    }

    public static class TestRoboServiceA extends TestRoboService {
        @Inject Context context;

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

    public static class TestRoboIntentServiceA extends TestRoboIntentService {
        @Inject Context context;

        public TestRoboIntentServiceA(String name) {
            super(name);
        }

        @Override
        protected void onHandleIntent(Intent intent) {
        }
    }
}
