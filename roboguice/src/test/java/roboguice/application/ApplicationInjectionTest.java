package roboguice.application;

import com.xtremelabs.robolectric.Robolectric;
import org.junit.Test;
import org.junit.runner.RunWith;
import roboguice.RoboGuice;
import roboguice.test.RobolectricRoboTestRunner;

import android.content.Context;

import com.google.inject.Inject;

import java.util.Random;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricRoboTestRunner.class)
public class ApplicationInjectionTest {

    @Test
    public void shouldBeAbleToInjectIntoApplication() {
        Robolectric.application = new AppA();
        Robolectric.application.onCreate();

        final AppA a = (AppA)Robolectric.application;
        assertNotNull(a.random);
    }


    @Test
    public void shouldBeAbleToInjectContextScopedItemsIntoApplication() {
        Robolectric.application = new AppB();
        Robolectric.application.onCreate();

        final AppB a = (AppB)Robolectric.application;
        assertThat( a.context, equalTo((Context)a) );
    }




    public static class AppA extends RoboApplication {
        @Inject Random random;

        @Override
        public void onCreate() {
            super.onCreate();
            RoboGuice.getInjector(this).injectMembers(this);
        }
    }

    public static class AppB extends RoboApplication {
        @Inject Context context;

        @Override
        public void onCreate() {
            super.onCreate();
            RoboGuice.getInjector(this).injectMembers(this);
        }
    }


}
