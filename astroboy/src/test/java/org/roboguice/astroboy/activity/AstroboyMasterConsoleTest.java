package org.roboguice.astroboy.activity;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.roboguice.astroboy.controller.AstroboyRemoteControl;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ActivityController;

import roboguice.RoboGuice;
import android.app.Activity;
import android.os.Vibrator;

import com.google.inject.AbstractModule;

/**
 * A testcase that swaps in a dependency of a RoboActivity to verify that
 * it properly uses it.
 */
@RunWith(RobolectricTestRunner.class)
public class AstroboyMasterConsoleTest {
    protected Vibrator vibratorMock = mock(Vibrator.class);
    private AstroboyRemoteControl astroboyRemoteControlMock = mock(AstroboyRemoteControl.class, RETURNS_DEEP_STUBS);
    private AstroboyMasterConsole astroboyMasterConsole;
    private ActivityController<AstroboyMasterConsole> astroboyMasterConsoleController;

    @Before
    public void setup() {
        // Override the default RoboGuice module
        astroboyMasterConsoleController = Robolectric.buildActivity(AstroboyMasterConsole.class);
        astroboyMasterConsole = astroboyMasterConsoleController.get();
        RoboGuice.overrideApplicationInjector(Robolectric.application, new MyTestModule());
        astroboyMasterConsoleController.create().start();
    }

    @After
    public void teardown() {
        // Don't forget to tear down our custom injector to avoid polluting other test classes
        RoboGuice.Util.reset();
    }

    @Test
    public void clickOnBrushTeethTriggersRemoteControl() {
        astroboyMasterConsole.brushTeethButton.callOnClick();
        verify(astroboyRemoteControlMock).brushTeeth();
    }


    public class MyTestModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Vibrator.class).toInstance(vibratorMock);
            bind(Activity.class).toInstance(astroboyMasterConsole);
            bind(AstroboyRemoteControl.class).toInstance(astroboyRemoteControlMock);
        }
    }
}
