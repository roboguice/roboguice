package roboguice.inject;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import roboguice.RoboGuice;
import roboguice.activity.RoboActivity;

import android.os.Bundle;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class ContextScopeTest {


    @Test
    public void shouldHaveContextInScopeMapAfterOnCreate() throws Exception {
        final MyActivity activity = new MyActivity();
        final ContextScope scope = RoboGuice.getApplicationInjector(Robolectric.application).getInstance(ContextScope.class);

        assertThat(scope.getScopedObjectMap(activity).size(), equalTo(0));
        activity.onCreate(null);
        assertThat(scope.getScopedObjectMap(activity).size(),equalTo(1));
    }

    public static class MyActivity extends RoboActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
    }

}
