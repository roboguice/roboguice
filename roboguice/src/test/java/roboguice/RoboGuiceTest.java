package roboguice;

import com.xtremelabs.robolectric.Robolectric;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import roboguice.activity.RoboActivity;
import roboguice.test.RobolectricRoboTestRunner;

import android.app.Activity;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricRoboTestRunner.class)
public class RoboGuiceTest {
    
    @Before
    public void setup() {
        RoboGuice.injectors.clear();
    }
    
    @Test
    public void destroyInjectorShouldRemoveContext() {
        final Activity activity = new RoboActivity();
        RoboGuice.getInjector(activity);
        
        assertThat(RoboGuice.injectors.size(), equalTo(1));
        
        RoboGuice.destroyInjector(activity);
        assertThat(RoboGuice.injectors.size(), equalTo(1));

        RoboGuice.destroyInjector(Robolectric.application);
        assertThat(RoboGuice.injectors.size(), equalTo(0));
    }

    @Test
    public void resetShouldRemoveContext() {
        final Activity activity = new RoboActivity();
        RoboGuice.getInjector(activity);
        
        assertThat(RoboGuice.injectors.size(), equalTo(1));
        
        RoboGuice.util.reset();
        assertThat(RoboGuice.injectors.size(), equalTo(0));
    }
}
