package roboguice.fragment.provided;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import roboguice.RoboGuice;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import com.google.inject.Inject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

@RunWith(RobolectricTestRunner.class)
@Config(shadows= {roboguice.fragment.provided.shadow.ShadowNativeFragment.class, roboguice.fragment.provided.shadow.ShadowNativeFragmentActivity.class})
public class FragmentInjectionTest {

    @Before 
    public void setup() {
        RoboGuice.setUseAnnotationDatabases(true);
    }

	@Test
    public void shadowActivityGetApplicationContextShouldNotReturnNull() {
        Assert.assertNotNull(new Activity().getApplicationContext());
    }
	
    @Test
    public void shouldInjectPojosAndViewsIntoFragments() {
        final ActivityA activity = Robolectric.buildActivity(ActivityA.class).create().get();
        activity.onCreate(null);
        activity.fragmentRef.onViewCreated(activity.fragmentRef.onCreateView(null,null,null), null);

        assertNotNull(activity.fragmentRef.ref);
        assertThat(activity.fragmentRef.v, equalTo(activity.fragmentRef.ref));
        assertThat(activity.fragmentRef.context,equalTo((Context)activity));
    }


    @Test
    public void shouldBeAbleToInjectViewsIntoActivityAndFragment() {
        final ActivityB activity = Robolectric.buildActivity(ActivityB.class).create().get();
        activity.onCreate(null);
        activity.fragmentRef.onViewCreated(activity.fragmentRef.onCreateView(null,null,null), null);

        assertNotNull(activity.fragmentRef.viewRef);
        assertNotNull(activity.viewRef);
        assertThat(activity.fragmentRef.v, equalTo(activity.fragmentRef.viewRef));
        assertThat(activity.v, equalTo(activity.viewRef));
    }


    @Test(expected = NullPointerException.class)
    public void shouldNotBeAbleToInjectFragmentViewsIntoActivity() {
        final ActivityC activity = Robolectric.buildActivity(ActivityC.class).create().get();
        activity.onCreate(null);
        activity.fragmentRef.onViewCreated(activity.fragmentRef.onCreateView(null,null,null), null);
    }


    @Test
    public void shouldNotCrashWhenRotatingScreen() {
        final ActivityD activity1 = Robolectric.buildActivity(ActivityD.class).create().get();
        final ActivityD activity2 = Robolectric.buildActivity(ActivityD.class).create().get();

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



    public static class ActivityA extends RoboActivity {
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
        }

    }


    public static class ActivityB extends RoboActivity {
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
        }

    }

    public static class ActivityC extends RoboActivity {
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



    public static class ActivityD extends RoboActivity {
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
