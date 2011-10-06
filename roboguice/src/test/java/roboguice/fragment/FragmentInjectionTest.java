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

import com.google.inject.Inject;

import static org.hamcrest.CoreMatchers.equalTo;
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

        assertThat(activity.ref.v, equalTo(activity.ref.ref));
        assertThat(activity.ref.context,equalTo((Context)activity));
    }


    @Test
    public void shouldBeAbleToInjectViewsIntoActivityAndFragment() {
        final ActivityB activity = new ActivityB();
        activity.onCreate(null);

        assertThat(activity.fragmentRef.v, equalTo(activity.fragmentRef.viewRef));
        assertThat(activity.v, equalTo(activity.viewRef));
    }






    public static class ActivityA extends RoboFragmentActivity {
        FragmentA ref;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            ref = new FragmentA();
            ref.onAttach(this);
            ref.onCreate(null);

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

}
