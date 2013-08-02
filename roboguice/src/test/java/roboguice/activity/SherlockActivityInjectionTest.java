package roboguice.activity;

import android.R;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.internal.ActionBarSherlockNative;
import roboguice.activity.SherlockActivityInjectionTest.ModuleA.A;
import roboguice.activity.SherlockActivityInjectionTest.ModuleB.B;
import roboguice.activity.SherlockActivityInjectionTest.ModuleC.C;
import roboguice.activity.SherlockActivityInjectionTest.ModuleD.D;
import com.google.inject.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import roboguice.RoboGuice;
import roboguice.inject.*;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.concurrent.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.robolectric.Robolectric.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class SherlockActivityInjectionTest {

    protected DummySherlockActivity activity;

    @Before
    public void setup() {
        RoboGuice.setBaseApplicationInjector(Robolectric.application, Stage.DEVELOPMENT, RoboGuice.newDefaultRoboModule(Robolectric.application), new ModuleA());
        ActionBarSherlock.registerImplementation(ActionBarSherlockRobolectric.class);
        activity = new DummySherlockActivity();
        activity.setIntent(new Intent(Robolectric.application, DummySherlockActivity.class).putExtra("foobar", "goober"));
        activity.onCreate(null);
    }

    @Test
    public void shouldInjectUsingDefaultConstructor() {
        assertThat(activity.emptyString, is(""));
    }

    @Test
    public void shouldInjectView() {
        assertThat(activity.text1, is(activity.findViewById(R.id.text1)));
    }

    @Test
    public void shouldInjectStringResource() {
        assertThat(activity.cancel, is("Cancel"));
    }

    @Test
    public void shouldInjectExtras() {
        assertThat(activity.foobar, is("goober"));
    }

    @Test
    public void shouldStaticallyInject() {
        assertThat(A.t, equalTo(""));
    }

    @Test
    public void shouldInjectActivityAndRoboSherlockActivity() {
        assertEquals(activity, activity.activity);
        assertEquals(activity, activity.roboSherlockActivity);
    }

    @Test(expected = ConfigurationException.class)
    public void shouldNotStaticallyInjectViews() {
        RoboGuice.setBaseApplicationInjector(Robolectric.application, Stage.DEVELOPMENT, RoboGuice.newDefaultRoboModule(Robolectric.application), new ModuleB());
        final B b = new B();
        b.onCreate(null);
    }

    @Test(expected = ConfigurationException.class)
    public void shouldNotStaticallyInjectExtras() {
        RoboGuice.setBaseApplicationInjector(Robolectric.application, Stage.DEVELOPMENT, RoboGuice.newDefaultRoboModule(Robolectric.application), new ModuleD());
        final D d = new D();
        d.onCreate(null);
    }

    @Test(expected = ConfigurationException.class)
    public void shouldNotStaticallyInjectPreferenceViews() {
        RoboGuice.setBaseApplicationInjector(Robolectric.application, Stage.DEVELOPMENT, RoboGuice.newDefaultRoboModule(Robolectric.application), new ModuleC());
        final C c = new C();
        c.onCreate(null);
    }

    @Test
    public void shouldInjectApplication() {
        final G g = new G();
        g.onCreate(null);

        assertThat(g.application, equalTo(Robolectric.application));
    }

    @Test
    public void shouldAllowBackgroundThreadsToFinishUsingContextAfterOnDestroy() throws Exception {
        final SoftReference<F> ref = new SoftReference<F>(new F());
        ref.get().onCreate(null);

        final BlockingQueue<Context> queue = new ArrayBlockingQueue<Context>(1);
        new Thread() {
            final Context context = RoboGuice.getInjector(ref.get()).getInstance(Context.class);

            @Override
            public void run() {
                queue.add(context);
            }
        }.start();

        ref.get().onDestroy();

        // Force an OoM
        // http://stackoverflow.com/questions/3785713/how-to-make-the-java-system-release-soft-references/3810234
        try {
            @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"}) final ArrayList<Object[]> allocations = new ArrayList<Object[]>();
            //noinspection InfiniteLoopStatement
            while (true) {
                allocations.add(new Object[(int) Runtime.getRuntime().maxMemory()]);
            }
        } catch (OutOfMemoryError e) {
            // Yeah!
        }

        assertNotNull(queue.poll(10, TimeUnit.SECONDS));
    }

    @Test
    public void shouldBeAbleToGetContextProvidersInBackgroundThreads() throws Exception {
        final F f = new F();
        f.onCreate(null);

        final FutureTask<Context> future = new FutureTask<Context>(new Callable<Context>() {
            final ContextScopedProvider<Context> contextProvider = RoboGuice.getInjector(f).getInstance(Key.get(new TypeLiteral<ContextScopedProvider<Context>>(){}));

            @Override
            public Context call() throws Exception {
                return contextProvider.get(f);
            }
        });

        Executors.newSingleThreadExecutor().execute(future);

        future.get();
    }

    public static class DummySherlockActivity extends RoboSherlockActivity {
        @Inject protected String emptyString;
        @Inject protected Activity activity;
        @Inject protected RoboSherlockActivity roboSherlockActivity;
        @InjectView(R.id.text1) protected TextView text1;
        @InjectResource(R.string.cancel) protected String cancel;
        @InjectExtra("foobar") protected String foobar;

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

    public static class BaseModule extends com.google.inject.AbstractModule {
        @Override
        protected void configure() {
            bind(RoboSherlockActivity.class)
                    .toProvider(Key.get(new TypeLiteral<NullProvider<RoboSherlockActivity>>(){}))
                    .in(ContextSingleton.class);
        }
    }

    public static class ModuleA extends BaseModule {
        @Override
        protected void configure() {
            super.configure();
            requestStaticInjection(A.class);
        }

        public static class A {
            @InjectResource(R.string.cancel) static String s;
            @Inject static String t;
        }
    }

    public static class ModuleB extends BaseModule {
        @Override
        protected void configure() {
            super.configure();
            requestStaticInjection(B.class);
        }

        public static class B extends RoboSherlockActivity {
            @InjectView(0) static View v;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
            }
        }
    }

    public static class ModuleC extends BaseModule {
        @Override
        public void configure() {
            super.configure();
            requestStaticInjection(C.class);
        }

        public static class C extends RoboSherlockActivity {
            @InjectPreference("xxx") static Preference v;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
            }
        }
    }

    public static class ModuleD extends BaseModule {
        @Override
        public void configure() {
            super.configure();
            requestStaticInjection(D.class);
        }

        public static class D extends RoboSherlockActivity {
            @InjectExtra("xxx") static String s;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
            }
        }
    }

    public static class F extends RoboSherlockActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
        }
    }

    public static class G extends RoboSherlockActivity {
        @Inject Application application;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
    }

    @ActionBarSherlock.Implementation(api = 0)
    public static class ActionBarSherlockRobolectric extends ActionBarSherlockNative {
        public ActionBarSherlockRobolectric(Activity activity, int flags) {
            super(activity, flags);
        }

        @Override
        public void setContentView(int layoutResId) {
            LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
            View contentView = layoutInflater.inflate(layoutResId, null);

            shadowOf(mActivity).setContentView(contentView);
        }

        @Override
        public void setContentView(View view) {
            shadowOf(mActivity).setContentView(view);
        }
    }
}
