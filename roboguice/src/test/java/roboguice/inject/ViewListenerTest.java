package roboguice.inject;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import roboguice.activity.RoboActivity;

import android.os.Bundle;
import android.view.View;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class ViewListenerTest {

    @Ignore("Something is still holding onto the activity, I haven't identified whether it's a problem with the test or something real yet")
    @Test
    public void shouldNotHoldReferencesToContext() {
        final SoftReference<MyActivity> activityRef = new SoftReference<MyActivity>(new MyActivity());
        activityRef.get().onCreate(null);

        Robolectric.resetStaticState();

        assertThat(activityRef.get(), not(equalTo(null)));
        assertThat(activityRef.get().v, not(equalTo(null)));


        // Force an OoM
        // http://stackoverflow.com/questions/3785713/how-to-make-the-java-system-release-soft-references/3810234
        try {
            final ArrayList<Object[]> allocations = new ArrayList<Object[]>();
            int i=0;
            while(i ==0)
                allocations.add( new Object[(int) Runtime.getRuntime().maxMemory()] );
        } catch( OutOfMemoryError e ) {
            // great!
        }

        assertThat(activityRef.get(), equalTo(null));

    }



    public static class MyActivity extends RoboActivity {
        @InjectView(100) protected View v;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            final View x = new View(this);
            x.setId(100);
            setContentView(x);
        }
    }

}
