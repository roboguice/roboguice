package roboguice.application;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;

import roboguice.RoboGuice;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.res.RobolectricPackageManager;
import com.xtremelabs.robolectric.shadows.ShadowContextWrapper;

import com.google.inject.Inject;
import com.google.inject.Stage;

import android.app.Application;
import android.content.Context;

@RunWith(CustomRoboTestRunner.class)
public class ApplicationInjectionTest {

    private void setUp() {
        ShadowContextWrapper shadowApp = Robolectric.shadowOf(Robolectric.application);
        shadowApp.setPackageName("com.mycompany.myapp");
        shadowApp.setPackageManager(new RobolectricPackageManager(Robolectric.application, null));
        RoboGuice
                .setBaseApplicationInjector(Robolectric.application, Stage.DEVELOPMENT, RoboGuice.newDefaultRoboModule(Robolectric.application), new ModuleA());
    }

    @Test
    public void shouldBeAbleToInjectIntoApplication() {

        Robolectric.application = new AppA();
        setUp();
        Robolectric.application.onCreate();

        final AppA a = (AppA) Robolectric.application;
        assertNotNull(a.random);
    }

    @Test
    public void shouldBeAbleToInjectContextScopedItemsIntoApplication() {
        Robolectric.application = new AppB();
        setUp();
        Robolectric.application.onCreate();

        final AppB a = (AppB) Robolectric.application;
        assertThat(a.context, equalTo((Context) a));
    }

    public static class AppA extends Application {
        @Inject
        Random random;

        @Override
        public void onCreate() {
            super.onCreate();
            RoboGuice.getInjector(this).injectMembers(this);
        }
    }

    public static class AppB extends Application {
        @Inject
        Context context;

        @Override
        public void onCreate() {
            super.onCreate();
            RoboGuice.getInjector(this).injectMembers(this);
        }
    }

    public static class ModuleA extends com.google.inject.AbstractModule {
        @Override
        protected void configure() {
        }

    }

}
