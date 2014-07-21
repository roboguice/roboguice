package org.roboguice.astroboy.controller;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import roboguice.RoboGuice;
import roboguice.activity.RoboActivity;
import android.app.Application;
import android.content.Context;
import android.os.Vibrator;

import com.google.inject.AbstractModule;
import com.google.inject.util.Modules;

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
        RoboGuice.overrideApplicationInjector(application, new MyTestModule());

        when(context.getApplicationContext()).thenReturn(application);
        when(application.getApplicationContext()).thenReturn(application);

    }
    
    @After
    public void teardown() {
        // Don't forget to tear down our custom injector to avoid polluting other test classes
        RoboGuice.Util.reset();
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
