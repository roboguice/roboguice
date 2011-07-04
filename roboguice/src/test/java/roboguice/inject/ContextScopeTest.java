package roboguice.inject;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import roboguice.RoboGuice;
import roboguice.activity.RoboActivity;

import android.os.Bundle;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class ContextScopeTest {


    @Test
    public void shouldHaveContextInScopeMapAfterOnCreate() throws Exception {
        final A a = new A();
        final ContextScope scope = RoboGuice.getApplicationInjector(Robolectric.application).getInstance(ContextScope.class);

        assertThat(scope.getScopedObjectMap(a).size(), equalTo(0));
        a.onCreate(null);
        assertThat(scope.getScopedObjectMap(a).size(),equalTo(1));
    }


    @Test
    public void shouldHaveTwoItemsInScopeMapAfterOnCreate() throws Exception {
        final B b = new B();
        final ContextScope scope = RoboGuice.getApplicationInjector(Robolectric.application).getInstance(ContextScope.class);

        assertThat(scope.getScopedObjectMap(b).size(), equalTo(0));
        b.onCreate(null);
        assertThat(scope.getScopedObjectMap(b).size(),equalTo(2));
    }

    public static class A extends RoboActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
    }

    public static class B extends RoboActivity {
        @Inject C c; // context scoped
        @Inject D d; // unscoped
        @Inject E e; // singleton

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
    }

    @Context
    public static class C {}

    public static class D {}

    @Singleton
    public static class E {}
}
