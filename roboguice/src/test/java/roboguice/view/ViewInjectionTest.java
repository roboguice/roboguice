package roboguice.view;

import com.xtremelabs.robolectric.RobolectricTestRunner;
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class ViewInjectionTest {

    @Test
    public void shouldInjectViewsIntoActivitiesAndViews() {
        final RoboActivityA a = new RoboActivityA();
        a.onCreate(null);

        assertThat( a.v, equalTo((View)a.ref));
        assertThat( a.v.w, equalTo(a.v.ref) );
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

        
        public static class PojoA {
            @InjectView(100) View v;
        }
    }




    public static class RoboActivityA extends RoboActivity {
        @InjectView(100) ViewA v;
        
        LinearLayout ref;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            ref = new ViewA(this);
            ref.setId(100);

            setContentView(ref);
        }


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
