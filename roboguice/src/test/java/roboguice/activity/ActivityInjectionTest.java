package roboguice.activity;

import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import roboguice.inject.InjectView;

import android.R;
import android.os.Bundle;
import android.widget.TextView;

import com.google.inject.Inject;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class ActivityInjectionTest {

    protected DummyActivity activity;
    
    @Before
    public void setup() {
        activity = new DummyActivity();
        activity.onCreate(null);
    }

    @Test
    public void shouldInjectUsingDefaultConstructor() {
        assertThat(activity.emptyString,is(""));
    }

    @Test
    public void shouldInjectView() {
        assertThat(activity.text1,is(activity.findViewById(R.id.text1)));
    }


    public static class DummyActivity extends RoboActivity {
        @Inject protected String emptyString;
        @InjectView(R.id.text1) protected TextView text1;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            final TextView root = new TextView(this);
            root.setId(R.id.text1);                       
            setContentView(root);
        }
    }
}
