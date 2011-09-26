package roboguice.activity;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import roboguice.RoboGuice;
import roboguice.activity.ActivityInjectionTest.ModuleA.A;
import roboguice.activity.ActivityInjectionTest.ModuleB.B;
import roboguice.activity.ActivityInjectionTest.ModuleC.C;
import roboguice.activity.ActivityInjectionTest.ModuleD.D;
import roboguice.inject.*;

import android.R;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.inject.*;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.concurrent.*;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class ActivityInjectionTest {

    protected DummyActivity activity;

    @Before
    public void setup() {
        RoboGuice.setApplicationInjector(Robolectric.application, Stage.DEVELOPMENT, RoboGuice.createNewDefaultRoboModule(Robolectric.application), new ModuleA());
        activity = new DummyActivity();
        activity.setIntent( new Intent(Robolectric.application,DummyActivity.class).putExtra("foobar","goober") );
        activity.onCreate(null);
    }

    @Test
    public void shouldInjectScopedViews() {
        assertThat((String) activity.scopedTextView1.getText(), is("OK"));
        assertThat((String) activity.scopedTextView2.getText(), equalTo("Cancel"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerWhenInjectedViewIdIsNotPresent() {
        new BadInjectViewAnnotationActivity().onCreate(null);
    }

    @Test
    public void shouldInjectUsingDefaultConstructor() {
        assertThat(activity.emptyString,is(""));
    }

    @Test
    public void shouldInjectView() {
        assertThat(activity.text1,is(activity.findViewById(R.id.text1)));
    }

    @Test
    public void shouldInjectStringResource() {
        assertThat(activity.cancel,is("Cancel"));
    }

    @Test
    public void shouldInjectViewsIntoViews() {
        final InjectedView v = new InjectedView(activity);
        assertThat(v.v,equalTo(v.child));
    }

    @Test
    public void shouldInjectExtras() {
        assertThat(activity.foobar,is("goober"));
    }

    @Test
    public void shouldStaticallyInject() {
        assertThat(A.t, equalTo(""));
    }

    @Test
    public void shouldStaticallyInjectResources() {
        assertThat(A.s,equalTo("Cancel"));
    }

    @Test(expected = ConfigurationException.class)
    public void shouldNotStaticallyInjectViews() {
        RoboGuice.setApplicationInjector(Robolectric.application, Stage.DEVELOPMENT, RoboGuice.createNewDefaultRoboModule(Robolectric.application), new ModuleB());
        final B b = new B();
        b.onCreate(null);
    }

    @Test(expected = ConfigurationException.class)
    public void shouldNotStaticallyInjectExtras() {
        RoboGuice.setApplicationInjector(Robolectric.application, Stage.DEVELOPMENT, RoboGuice.createNewDefaultRoboModule(Robolectric.application), new ModuleD());
        final D d = new D();
        d.onCreate(null);
    }

    @Test(expected = ConfigurationException.class)
    public void shouldNotStaticallyInjectPreferenceViews() {
        RoboGuice.setApplicationInjector(Robolectric.application, Stage.DEVELOPMENT, RoboGuice.createNewDefaultRoboModule(Robolectric.application), new ModuleC());
        final C c = new C();
        c.onCreate(null);
    }


    @Test
    public void shouldAllowBackgroundThreadsToFinishUsingContextAfterOnDestroy() throws Exception {
        final SoftReference<F> ref = new SoftReference<F>(new F());
        ref.get().onCreate(null);

        final BlockingQueue<Context> queue = new ArrayBlockingQueue<Context>(1);
        new Thread()  {
            final Context context = RoboGuice.getInjector(ref.get()).getInstance(Context.class);

            @Override
            public void run() {
                queue.add( context );
            }
        }.start();

        ref.get().onDestroy();

        // Force an OoM
        // http://stackoverflow.com/questions/3785713/how-to-make-the-java-system-release-soft-references/3810234
        try {
            @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"}) final ArrayList<Object[]> allocations = new ArrayList<Object[]>();
            //noinspection InfiniteLoopStatement
            while(true)
                allocations.add( new Object[(int) Runtime.getRuntime().maxMemory()] );
        } catch( OutOfMemoryError e ) {
            // great!
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

    @Test
    public void shouldBeAbleToInjectViewsIntoPojos() {
        final E activity = new E();
        activity.onCreate(null);
        assertThat(activity.a.v,equalTo(activity.ref));
    }


    public static class E extends RoboActivity {

        @Inject PojoA a;

        View ref;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            ref = new View(this);
            ref.setId(100);
            setContentView(ref);
        }
    }



    public static class DummyActivity extends RoboActivity {
        @Inject protected String emptyString;
        @InjectView(R.id.text1) protected TextView text1;
        @InjectResource(R.string.cancel) protected String cancel;
        @InjectExtra("foobar") protected String foobar;
        @InjectView({R.id.summary, R.id.text2}) protected TextView scopedTextView1;
        @InjectView({R.id.title, R.id.text2}) protected TextView scopedTextView2;

        
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
            @InjectResource(android.R.string.cancel) static String s;
            @Inject static String t;
        }
    }


    public static class ModuleB extends com.google.inject.AbstractModule {
        @Override
        public void configure() {
            requestStaticInjection(B.class);
        }


        public static class B extends RoboActivity{
            @InjectView(0) static View v;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
            }
        }
    }


    public static class ModuleC extends com.google.inject.AbstractModule {
        @Override
        public void configure() {
            requestStaticInjection(C.class);
        }


        public static class C extends RoboActivity{
            @InjectPreference("xxx") static Preference v;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
            }
        }
    }


    public static class ModuleD extends com.google.inject.AbstractModule {
        @Override
        public void configure() {
            requestStaticInjection(D.class);
        }


        public static class D extends RoboActivity{
            @InjectExtra("xxx") static String s;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
            }
        }
    }

    public static class F extends RoboActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
        }
    }

    public class InjectedView extends FrameLayout {
        @InjectView(100) View v;

        View child;

        public InjectedView(Context context) {
            super(context);
            child = new View(context);
            child.setId(100);
            addView(child);

            RoboGuice.getInjector(context).injectMembers(this);
        }
    }

    public static class BadInjectViewAnnotationActivity extends RoboActivity {
        @InjectView({}) protected View badAnnotationView;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(new LinearLayout(this));
        }
    }

    public static class PojoA {
        @InjectView(100) View v;
    }
}
