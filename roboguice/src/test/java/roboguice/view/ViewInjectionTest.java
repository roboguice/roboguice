package roboguice.view;

import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import roboguice.RoboGuice;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.inject.Inject;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class ViewInjectionTest {

    @Test
    public void shouldInjectViewsIntoActivitiesAndViews() {
        final C activity = new C();
        activity.onCreate(null);

        assertThat(activity.v, equalTo((View)activity.ref));
        assertThat(activity.v.w, equalTo(activity.v.ref));
    }


    @Test
    public void shouldBeAbleToInjectViewsIntoPojos() {
        final B activity = new B();
        activity.onCreate(null);
        assertThat(activity.a.v,equalTo(activity.ref));
    }



    @Test
    public void shouldNotHoldReferencesToContext() {
        final SoftReference<A> activityRef = new SoftReference<A>(new A());
        activityRef.get().onCreate(null);

        assertThat(activityRef.get(), not(equalTo(null)));
        assertThat(activityRef.get().v, not(equalTo(null)));

        activityRef.get().onDestroy();

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

        assertThat(activityRef.get(), equalTo(null));

    }



    @Ignore("getWindow().getDecoreView() doesn't seem to return the root view in robolectric?")
    @Test
    public void shouldBeAbleToInjectReferencesToTaggedViews() {
        final D activity = new D();
        activity.onCreate(null);

        assertThat(activity.v, equalTo((View)activity.ref));
        assertThat(activity.v.w, equalTo(activity.v.ref));
    }


    public static class A extends RoboActivity {
        @InjectView(100) protected View v;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            final View x = new View(this);
            x.setId(100);
            setContentView(x);
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
        }
    }



    public static class B extends RoboActivity {

        @Inject PojoA a;

        View ref;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            ref = new View(this);
            ref.setId(100);
            setContentView(ref);
        }

        
        public static class PojoA {
            @InjectView(100) View v;
        }
    }




    public static class C extends RoboActivity {
        @InjectView(100) ViewA v;
        
        LinearLayout ref;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            ref = new ViewA(this);
            ref.setId(100);

            setContentView(ref);
        }

        public static class ViewA extends LinearLayout {
            @InjectView(101) View w;

            View ref;

            public ViewA(Context context) {
                super(context);

                ref = new View(getContext());
                ref.setId(101);
                addView(ref);

                RoboGuice.getInjector(getContext()).injectMembers(this);
            }

        }


    }

    public static class D extends RoboActivity {
        @InjectView(tag="100") ViewA v;

        LinearLayout ref;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            ref = new ViewA(this);
            ref.setTag("100");

            setContentView(ref);
        }

        public static class ViewA extends LinearLayout {
            @InjectView(tag="101") View w;

            View ref;

            public ViewA(Context context) {
                super(context);

                ref = new View(getContext());
                ref.setTag("101");
                addView(ref);

                RoboGuice.getInjector(getContext()).injectMembers(this);
            }

        }


    }

}
