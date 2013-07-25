package roboguice.application;

import android.app.Application;
import android.content.Context;
import com.google.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import roboguice.RoboGuice;

import java.util.Random;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
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




    public static class AppA extends Application {
        @Inject Random random;

        @Override
        public void onCreate() {
            super.onCreate();
            RoboGuice.getInjector(this).injectMembers(this);
        }

        @Override
        public String getPackageName() {
            return "org.robolectric.default";
        }
    }

    public static class AppB extends Application {
        @Inject Context context;

        @Override
        public void onCreate() {
            super.onCreate();
            RoboGuice.getInjector(this).injectMembers(this);
        }

        @Override
        public String getPackageName() {
            return "org.robolectric.default";
        }
    }


}
