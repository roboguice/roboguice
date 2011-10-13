package org.roboguice.astroboy.test.controller;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.roboguice.astroboy.controller.Astroboy;
import roboguice.RoboGuice;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;

import com.google.inject.AbstractModule;
import com.google.inject.util.Modules;

import static org.easymock.EasyMock.*;

/**
 * A testcase that swaps in a TestVibrator to verify that
 * Astroboy's {@link org.roboguice.astroboy.controller.Astroboy#brushTeeth()} method
 * works properly.
 */
@RunWith(RobolectricTestRunner.class)
public class Astroboy2Test {
    
    protected Context context = new Activity();
    protected Vibrator vibratorMock = EasyMock.createMock(Vibrator.class);

    @Before
    public void setup() {
        // Override the default RoboGuice module
        RoboGuice.setBaseApplicationInjector(Robolectric.application, RoboGuice.DEFAULT_STAGE, Modules.override(RoboGuice.newDefaultRoboModule(Robolectric.application)).with(new MyTestModule()));
    }
    
    @After
    public void teardown() {
        // BUG! need to clear our custom injector
        //RoboGuice.
    }
    
    @Test
    public void brushingTeethShouldCausePhoneToVibrate() {
        // tell easymock what we expect, then switch easymock to "replay" mode: http://easymock.org/EasyMock3_0_Documentation.html
        vibratorMock.vibrate(aryEq(new long[]{0, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50}), eq(-1));
        expectLastCall().once();
        replay(vibratorMock);
        

        // get the astroboy instance
        final Astroboy astroboy = RoboGuice.getInjector(context).getInstance(Astroboy.class);

        // do the thing
        astroboy.brushTeeth();

        // verify that by doing the thing, vibratorMock.vibrate was called
        verify(vibratorMock);

    }


    public class MyTestModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Vibrator.class).toInstance(vibratorMock);
        }
    }
}
