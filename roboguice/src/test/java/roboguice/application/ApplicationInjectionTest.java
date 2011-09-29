package roboguice.application;

import com.xtremelabs.robolectric.Robolectric;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import roboguice.RoboGuice;
import roboguice.RobolectricRoboTestRunner;

import android.app.Application;

import com.google.inject.Inject;

import java.util.Random;

import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricRoboTestRunner.class)
public class ApplicationInjectionTest {

    @Before
    public void setup() {
    }

    @Test
    public void shouldBeAbleToInjectIntoApplication() {
        Robolectric.application = new AppA();
        Robolectric.application.onCreate();

        final AppA a = (AppA)Robolectric.application;
        //assertThat( a.context, equalTo((Context)a) );
        assertNotNull(a.random);
    }



    public static class AppA extends Application {
        //@Inject Context context; // ContextScoped injection is not yet supported
        @Inject Random random;

        @Override
        public void onCreate() {
            super.onCreate();
            RoboGuice.getInjector(this).injectMembers(this);
        }
    }
}
