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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
        final RoboFragmentActivityA activity = new RoboFragmentActivityA();
        activity.onCreate(null);

        assertThat(activity.ref.v, equalTo(activity.ref.ref));
        assertThat(activity.ref.context,equalTo((Context)activity));
    }



    public static class RoboFragmentActivityA extends RoboFragmentActivity {
        @Inject FragmentManager fragmentManager;

        RoboFragmentA ref;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            ref = new RoboFragmentA();
            ref.onAttach(this);
            ref.onCreate(null);

            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(100,ref);
            transaction.commit();
        }
    }

    public static class RoboFragmentA extends RoboFragment {
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
