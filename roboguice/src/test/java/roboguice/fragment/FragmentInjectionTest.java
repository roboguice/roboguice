package roboguice.fragment;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import roboguice.RobolectricRoboTestRunner;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.inject.Inject;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricRoboTestRunner.class)
public class FragmentInjectionTest {

    @Test
    public void shadowFragmentActivityGetApplicationContextShouldNotReturnNull() {
        Assert.assertNotNull(new FragmentActivity().getApplicationContext());
    }

    @Test
    public void shouldInjectPojosAndViewsIntoFragments() {
        final ActivityA activity = new ActivityA();
        activity.onCreate(null);
        activity.fragmentRef.onViewCreated(activity.fragmentRef.onCreateView(null,null,null), null);

        assertNotNull(activity.fragmentRef.ref);
        assertThat(activity.fragmentRef.v, equalTo(activity.fragmentRef.ref));
        assertThat(activity.fragmentRef.context,equalTo((Context)activity));
    }


    @Test
    public void shouldBeAbleToInjectViewsIntoActivityAndFragment() {
        final ActivityB activity = new ActivityB();
        activity.onCreate(null);
        activity.fragmentRef.onViewCreated(activity.fragmentRef.onCreateView(null,null,null), null);

        assertNotNull(activity.fragmentRef.viewRef);
        assertNotNull(activity.viewRef);
        assertThat(activity.fragmentRef.v, equalTo(activity.fragmentRef.viewRef));
        assertThat(activity.v, equalTo(activity.viewRef));
    }


    @Test(expected = NullPointerException.class)
    public void shouldNotBeAbleToInjectFragmentViewsIntoActivity() {
        final ActivityC activity = new ActivityC();
        activity.onCreate(null);
        activity.fragmentRef.onViewCreated(activity.fragmentRef.onCreateView(null,null,null), null);
    }


    @Test
    public void shouldNotCrashWhenRotatingScreen() {
        final ActivityD activity1 = new ActivityD();
        final ActivityD activity2 = new ActivityD();

        activity1.onCreate(null);
        activity1.onResume();
        activity1.fragmentRef.onViewCreated(activity1.fragmentRef.onCreateView(null,null,null), null);

        assertNotNull(activity1.fragmentRef.ref);
        assertThat(activity1.fragmentRef.v, equalTo(activity1.fragmentRef.ref));

        activity1.onPause();

        activity2.onCreate(null); // crash here?
        activity2.onResume();
        activity2.fragmentRef.onViewCreated(activity2.fragmentRef.onCreateView(null,null,null), null);

        assertNotNull(activity2.fragmentRef.ref);
        assertThat(activity2.fragmentRef.v, equalTo(activity2.fragmentRef.ref));
    }



    public static class ActivityA extends RoboFragmentActivity {
        FragmentA fragmentRef;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            fragmentRef = new FragmentA();
            fragmentRef.onAttach(this);
            fragmentRef.onCreate(null);

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

            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

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
            fragmentRef.onAttach(this);
            fragmentRef.onCreate(null);

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

            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

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
            fragmentRef.onAttach(this);
            fragmentRef.onCreate(null);

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
            fragmentRef.onAttach(this);
            fragmentRef.onCreate(null);

            setContentView(new FrameLayout(this));
            
        }

        @Override
        protected void onPause() {
            super.onPause();
        }

        @Override
        protected void onResume() {
            super.onResume();
        }

        @Override
        protected void onStop() {
            super.onStop();
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
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

            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

            }
        }

    }

}
