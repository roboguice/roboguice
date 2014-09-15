package roboguice.fragment.provided;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import roboguice.activity.RoboActivity;

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

	@Test
    public void shadowActivityGetApplicationContextShouldNotReturnNull() {
        assertNotNull(new Activity().getApplicationContext());
    }
	
    @Test
    public void shouldInjectPojosAndViewsIntoFragments() {
        final ActivityA activity = Robolectric.buildActivity(ActivityA.class).create().start().resume().get();
        activity.fragmentRef.onViewCreated(activity.fragmentRef.onCreateView(null,null,null), null);

        assertNotNull(activity.fragmentRef.ref);
        assertThat(activity.fragmentRef.context,equalTo((Context)activity));
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
            @Inject Context context;

            View ref;

            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
            }
            
            @Override
            public void onViewCreated(View view, Bundle savedInstanceState) {
                super.onViewCreated(view, savedInstanceState);
            }
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                ref = new View(getActivity());
                ref.setId(101);
                return ref;
            }
        }

    }

}
