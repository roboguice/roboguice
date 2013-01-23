package org.roboguice.astroboy.controller;

import android.app.Application;
import android.content.Context;
import android.os.Vibrator;
import com.google.inject.AbstractModule;
import com.google.inject.util.Modules;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import roboguice.RoboGuice;
import roboguice.activity.RoboActivity;

import static org.mockito.Mockito.*;

/**
 * A testcase that swaps in a TestVibrator to verify that
 * Astroboy's {@link org.roboguice.astroboy.controller.Astroboy#brushTeeth()} method
 * works properly.
 */
public class Astroboy2Test {
    protected Application application = mock(Application.class, RETURNS_DEEP_STUBS);
    protected Context context = mock(RoboActivity.class, RETURNS_DEEP_STUBS);
    protected Vibrator vibratorMock = mock(Vibrator.class);

    @Before
    public void setup() {
        // Override the default RoboGuice module
        RoboGuice.setBaseApplicationInjector(application, RoboGuice.DEFAULT_STAGE, Modules.override(RoboGuice.newDefaultRoboModule(application)).with(new MyTestModule()));

        when(context.getApplicationContext()).thenReturn(application);
        when(application.getApplicationContext()).thenReturn(application);

    }
    
    @After
    public void teardown() {
        // Don't forget to tear down our custom injector to avoid polluting other test classes
        RoboGuice.util.reset();
    }
    
    @Test
    public void brushingTeethShouldCausePhoneToVibrate() {

        // get the astroboy instance
        final Astroboy astroboy = RoboGuice.getInjector(context).getInstance(Astroboy.class);

        // do the thing
        astroboy.brushTeeth();

        // verify that by doing the thing, vibratorMock.vibrate was called
        verify(vibratorMock).vibrate(new long[]{0, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50},-1);

    }


    public class MyTestModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Vibrator.class).toInstance(vibratorMock);
        }
    }
}
