package roboguice.activity;

import android.R;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.inject.Inject;
import com.google.inject.Stage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import roboguice.RoboGuice;
import roboguice.activity.ActivityInjectionTest.ModuleA.A;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class ActivityInjectionTest {

    protected DummyActivityTest activity;

    @Before
    public void setup() {
        RoboGuice.getOrCreateBaseApplicationInjector(Robolectric.application, Stage.DEVELOPMENT, RoboGuice.newDefaultRoboModule(Robolectric.application), new ModuleA());
        activity = Robolectric.buildActivity(DummyActivityTest.class).withIntent(new Intent(Robolectric.application, DummyActivityTest.class).putExtra("foobar", "goober")).create().get();
    }

    @Test
    public void shouldInjectUsingDefaultConstructor() {
        assertThat(activity.emptyString, is(""));
    }

    @Test
    public void shouldStaticallyInject() {
        assertThat(A.t, equalTo(""));
    }

    @Test
    public void shouldInjectActivityAndRoboActivity() {
        assertEquals(activity, activity.activity);
    }

    @Test
    public void shouldInjectApplication() {
        final G g = Robolectric.buildActivity(G.class).create().get();
        assertThat(g.application, equalTo(Robolectric.application));
    }

    public static class DummyActivityTest extends TestRoboActivity {
        @Inject
        protected String emptyString;
        @Inject
        protected Activity activity;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            final LinearLayout root = new LinearLayout(this);

            final TextView text1 = new TextView(this);
            root.addView(text1);
            text1.setId(R.id.text1);

            final LinearLayout included1 = addIncludedView(R.id.summary, R.string.ok);
            root.addView(included1);
            final LinearLayout included2 = addIncludedView(R.id.title, R.string.no);
            root.addView(included2);

            setContentView(root);
        }

        protected LinearLayout addIncludedView(int includedRootId, int stringResId) {
            LinearLayout container = new LinearLayout(this);
            container.setId(includedRootId);

            TextView textView = new TextView(this);
            container.addView(textView);
            textView.setId(R.id.text2);
            textView.setText(stringResId);
            return container;
        }
    }

    public static class ModuleA extends com.google.inject.AbstractModule {
        @Override
        protected void configure() {
            requestStaticInjection(A.class);
        }

        public static class A {
            @Inject
            static String t;
        }
    }

    public static class G extends TestRoboActivity {
        @Inject Application application;
    }
}
