package org.roboguice.astroboy.controller;

import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.roboguice.astroboy.controller.Astroboy;
import roboguice.RoboGuice;

import android.app.Activity;
import android.content.Context;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.junit.Assert.assertThat;

/**
 * A simple testcase that tests the {@link Astroboy} pojo.
 *
 * This test has no particularly complicated activity or context dependencies,
 * so we don't bother initializing the activity or really doing anything with it
 * at all.
 */
@RunWith(RobolectricTestRunner.class)
public class Astroboy1Test {
    
    protected Context context = new Activity();
    protected Astroboy astroboy = RoboGuice.getInjector(context).getInstance(Astroboy.class);
    
    @Test
    public void stringShouldEndInExclamationMark() {
        assertThat(astroboy.punch(), endsWith("!"));
    }
}
