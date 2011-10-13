package org.roboguice.astroboy.test;

import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.roboguice.astroboy.controller.Astroboy;
import roboguice.RoboGuice;

import android.app.Activity;
import android.content.Context;

import static org.junit.Assert.assertTrue;

/**
 * A simple testcase that tests the {@link Astroboy} pojo.
 *
 * This test has no particularly complicated activity or context dependencies,
 * so we don't bother initializing the activity or really doing anything with it
 * at all.
 */
@RunWith(RobolectricTestRunner.class)
public class AstroboyTest {
    
    protected Context context = new Activity();
    protected Astroboy astroboy = RoboGuice.getInjector(context).getInstance(Astroboy.class);
    
    @Test
    public void stringShouldEndInExclamationMark() {
        assertTrue(astroboy.punch().endsWith("!"));
    }
}
