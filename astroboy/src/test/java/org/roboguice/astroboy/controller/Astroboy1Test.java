package org.roboguice.astroboy.controller;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import roboguice.RoboGuice;
import roboguice.activity.RoboActivity;
import android.content.Context;

/**
 * A simple testcase that tests the {@link Astroboy} pojo.
 *
 * This test has no particularly complicated activity or context dependencies,
 * so we don't bother initializing the activity or really doing anything with it
 * at all.
 */
@RunWith(RobolectricTestRunner.class)
public class Astroboy1Test {
    
    protected Context context;
    protected Astroboy astroboy;
    
    @Before
    public void setup() {
       RoboGuice.setUseAnnotationDatabases(false);
       context = Robolectric.buildActivity(RoboActivity.class).create().get();
       astroboy = RoboGuice.getInjector(context).getInstance(Astroboy.class);
    }

    @Test
    public void stringShouldEndInExclamationMark() {
        assertTrue(astroboy.punch().endsWith("!"));
    }
}
