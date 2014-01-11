package roboguice;

import android.app.Activity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import roboguice.activity.RoboActivity;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class RoboGuiceTest {
    
    @Before
    public void setup() {
        RoboGuice.injectors.clear();
    }
    
    @Test
    public void destroyInjectorShouldRemoveContext() {
        final Activity activity = Robolectric.buildActivity(RoboActivity.class).get();
        RoboGuice.getInjector(activity);
        
        assertThat(RoboGuice.injectors.size(), equalTo(1));
        
        RoboGuice.destroyInjector(activity);
        assertThat(RoboGuice.injectors.size(), equalTo(1));

        RoboGuice.destroyInjector(Robolectric.application);
        assertThat(RoboGuice.injectors.size(), equalTo(0));
    }

    @Test
    public void resetShouldRemoveContext() {
        final Activity activity = Robolectric.buildActivity(RoboActivity.class).get();
        RoboGuice.getInjector(activity);
        
        assertThat(RoboGuice.injectors.size(), equalTo(1));
        
        RoboGuice.util.reset();
        assertThat(RoboGuice.injectors.size(), equalTo(0));
    }
}
