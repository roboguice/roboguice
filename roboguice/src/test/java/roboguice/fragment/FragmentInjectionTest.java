package roboguice.fragment;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ActivityController;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;

import com.google.inject.Inject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

@RunWith(RobolectricTestRunner.class)
public class FragmentInjectionTest {

    // http://stackoverflow.com/questions/11333354/how-can-i-test-fragments-with-robolectric
    protected static void startFragment( FragmentActivity activity, Fragment fragment ) {
        final FragmentManager fragmentManager = activity.getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(fragment, null);
        fragmentTransaction.commit();

    }


    @Test
    public void shouldInjectPojosAndViewsIntoFragments() {
        final ActivityA activityA = Robolectric.buildActivity(ActivityA.class).create().start().resume().get();

        assertNotNull(activityA.fragmentRef.ref);
        assertThat(activityA.fragmentRef.v, equalTo(activityA.fragmentRef.ref));
        assertThat(activityA.fragmentRef.context,equalTo((Context)activityA));
    }


    @Test
    public void shouldBeAbleToInjectViewsIntoActivityAndFragment() {
        final ActivityB activityB = Robolectric.buildActivity(ActivityB.class).create().start().resume().get();

        assertNotNull(activityB.fragmentRef.viewRef);
        assertNotNull(activityB.viewRef);
        assertThat(activityB.fragmentRef.v, equalTo(activityB.fragmentRef.viewRef));
        assertThat(activityB.v, equalTo(activityB.viewRef));
    }


    @Test(expected = NullPointerException.class)
    public void shouldNotBeAbleToInjectFragmentViewsIntoActivity() {
        Robolectric.buildActivity(ActivityC.class).create().start().resume().get();
    }


    @Test
    public void shouldNotCrashWhenRotatingScreen() {
        final ActivityController<ActivityD> activityD1Controller = Robolectric.buildActivity(ActivityD.class).create().resume();
        final ActivityD activityD1 = activityD1Controller.get();

        final ActivityController<ActivityD> activityD2Controller = Robolectric.buildActivity(ActivityD.class);
        final ActivityD activityD2 = activityD2Controller.get();

        assertNotNull(activityD1.fragmentRef.ref);
        assertThat(activityD1.fragmentRef.v, equalTo(activityD1.fragmentRef.ref));

        activityD1Controller.pause();

        activityD2Controller.create().resume();

        assertNotNull(activityD2.fragmentRef.ref);
        assertThat(activityD2.fragmentRef.v, equalTo(activityD2.fragmentRef.ref));
    }
    
    @Test
    public void shouldUseFragmentScopePerFragment() {
        final ActivityE activityE = Robolectric.buildActivity(ActivityE.class).create().start().resume().get();

        assertNotNull(activityE.fragmentRef1.ref);
        assertNotNull(activityE.fragmentRef2.ref);
        assertThat(activityE.fragmentRef1.v, not(equalTo(activityE.fragmentRef2.v)));
        assertThat(activityE.fragmentRef1.foo, not(equalTo(activityE.fragmentRef2.foo)));
    }

    public static class ActivityA extends RoboFragmentActivity {
        FragmentA fragmentRef;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            fragmentRef = new FragmentA();
            startFragment(this, fragmentRef);
        }

        public static class FragmentA extends RoboFragment {
            @InjectView(101) View v;
            @Inject Context context;
            View ref;

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                ref = new View(getActivity());
                ref.setId(101);
                return ref;
            }
        }

    }


    public static class ActivityB extends RoboFragmentActivity {
        @InjectView(100) View v;
        View viewRef;
        FragmentB fragmentRef;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            viewRef =  new View(this);
            viewRef.setId(100);
            setContentView(viewRef);
            fragmentRef = new FragmentB();
            startFragment(this, fragmentRef);
        }

        public static class FragmentB extends RoboFragment {
            @InjectView(101) View v;
            View viewRef;

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                viewRef = new View(getActivity());
                viewRef.setId(101);
                return viewRef;
            }
        }
    }

    public static class ActivityC extends RoboFragmentActivity {
        @InjectView(101) View v;

        View viewRef;
        FragmentC fragmentRef;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView( new View(this) );
            fragmentRef = new FragmentC();
            startFragment(this, fragmentRef);
        }

        public static class FragmentC extends RoboFragment {
            @InjectView(101) View v;
            View viewRef;

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                viewRef = new View(getActivity());
                viewRef.setId(101);
                return viewRef;
            }
        }

    }

    public static class ActivityD extends RoboFragmentActivity {
        FragmentD fragmentRef;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            fragmentRef = new FragmentD();
            startFragment(this,fragmentRef);
            setContentView(new FrameLayout(this));
        }

        public static class FragmentD extends RoboFragment {
            @InjectView(101) View v;
            View ref;

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                ref = new View(getActivity());
                ref.setId(101);
                return ref;
            }
        }
    }
    
    public static class ActivityE extends RoboFragmentActivity {
        FragmentE fragmentRef1;
        FragmentE fragmentRef2;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            fragmentRef1 = new FragmentE();
            startFragment(this,fragmentRef1);
            fragmentRef2 = new FragmentE();
            startFragment(this,fragmentRef2);
            setContentView(new FrameLayout(this));
        }

        public static class FragmentE extends RoboFragment {
            @InjectView(101) View v;
            View ref;
            @Inject FooImpl foo;

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                ref = new View(getActivity());
                ref.setId(101);
                return ref;
            }
        }
        
        static class FooImpl {}
    }

}
