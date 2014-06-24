package roboguice.view;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ActivityController;

import roboguice.RoboGuice;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import com.google.inject.Inject;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

@RunWith(RobolectricTestRunner.class)
public class ViewInjectionTest {

    @Test
    public void shouldInjectViewsIntoActivitiesAndViews() {
        final C activity = Robolectric.buildActivity(C.class).create().get();

        assertThat(activity.v, equalTo((View)activity.ref));
        assertThat(activity.v.w, equalTo(activity.v.ref));
    }


    @Test
    public void shouldBeAbleToInjectViewsIntoPojos() {
        final B activity = Robolectric.buildActivity(B.class).create().get();
        assertThat(activity.a.v,equalTo(activity.ref));
    }



    @Test
    public void shouldNotHoldReferencesToContext() {
        ActivityController<A> controller= Robolectric.buildActivity(A.class).create();
        final SoftReference<A> activityRef = new SoftReference<A>(controller.get());

        assertThat(activityRef.get(), not(equalTo(null)));
        assertThat(activityRef.get().v, not(equalTo(null)));

        controller.destroy();
        //noinspection UnusedAssignment
        controller=null;


        // Force an OoM
        // http://stackoverflow.com/questions/3785713/how-to-make-the-java-system-release-soft-references/3810234
        boolean oomHappened = false;
        try {
            @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"}) final ArrayList<Object[]> allocations = new ArrayList<Object[]>();
            int size;
            while( (size = Math.min(Math.abs((int)Runtime.getRuntime().freeMemory()),Integer.MAX_VALUE))>0 )
                allocations.add( new Object[size] );

        } catch( OutOfMemoryError e ) {
            // great!
            oomHappened = true;
        }


        assertTrue(oomHappened);
        assertThat(activityRef.get(), equalTo(null));

    }

    @Test
    public void shouldBeAbleToInjectReferencesToTaggedViews() {
        final D activity = Robolectric.buildActivity(D.class).create().get();

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
