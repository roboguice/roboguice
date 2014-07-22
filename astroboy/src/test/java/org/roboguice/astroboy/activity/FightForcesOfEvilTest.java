package org.roboguice.astroboy.activity;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.roboguice.astroboy.controller.Astroboy;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import roboguice.RoboGuice;

import com.google.inject.AbstractModule;

/**
 * A testcase that swaps in a dependency of a RoboActivity to verify that
 * it properly uses it.
 */
@RunWith(RobolectricTestRunner.class)
public class FightForcesOfEvilTest {
    private Astroboy astroboyMock = mock(Astroboy.class);

    @Before
    public void setup() {
        // Override the default RoboGuice module
        RoboGuice.overrideApplicationInjector(Robolectric.application, new MyTestModule());
    }

    @After
    public void teardown() {
        // Don't forget to tear down our custom injector to avoid polluting other test classes
        RoboGuice.Util.reset();
    }

    @Test
    public void createTriggersPunch() throws InterruptedException {
        Robolectric.buildActivity(FightForcesOfEvilActivity.class).create().start();
        Thread.sleep(6*1000);
        verify(astroboyMock, Mockito.times(10)).punch();
    }


    public class MyTestModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Astroboy.class).toInstance(astroboyMock);
        }
    }
}
