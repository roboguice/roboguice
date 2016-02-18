package roboguice.inject;

import android.app.Activity;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Singleton;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ActivityController;
import roboguice.RoboGuice;
import roboguice.activity.TestRoboActivity;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class ContextScopeTest {

    @Test
    public void shouldHaveContextInScopeMapAfterOnCreate() throws Exception {
        final ActivityController<A> aController = Robolectric.buildActivity(A.class);
        final A a = aController.get();

        aController.create();

        boolean found = false;
        for (Object o : RoboGuice.getInjector(a).getScopedObjects().values())
            if (o == a) {
                found = true;
            }

        assertTrue("Couldn't find context in scope map", found);
    }

    @Test
    public void shouldBeAbleToOpenMultipleScopes() {
        final ContextScope scope = RoboGuice.getOrCreateBaseApplicationInjector(Robolectric.application).getInstance(ContextScope.class);
        final Activity a = Robolectric.buildActivity(A.class).get();
        final Activity b = Robolectric.buildActivity(B.class).get();

        scope.enter(a, RoboGuice.getInjector(a).getScopedObjects());
        scope.enter(b, RoboGuice.getInjector(b).getScopedObjects());
        scope.exit(b);
        scope.exit(a);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotBeAbleToExitTheWrongScope() {
        final ContextScope scope = RoboGuice.getOrCreateBaseApplicationInjector(Robolectric.application).getInstance(ContextScope.class);
        final Activity a = Robolectric.buildActivity(A.class).get();
        final Activity b = Robolectric.buildActivity(B.class).get();

        scope.enter(a, RoboGuice.getInjector(a).getScopedObjects());
        scope.enter(b, RoboGuice.getInjector(b).getScopedObjects());
        scope.exit(a);
    }

    @Test
    public void shouldHaveTwoItemsInScopeMapAfterOnCreate() throws Exception {
        final ActivityController<B> bController = Robolectric.buildActivity(B.class);
        final B b = bController.get();

        bController.create();

        boolean found = false;
        for (Object o : RoboGuice.getInjector(b).getScopedObjects().values())
            if (o == b) {
                found = true;
            }

        assertTrue("Couldn't find context in scope map", found);
        assertTrue(RoboGuice.getInjector(b).getScopedObjects().containsKey(Key.get(C.class)));
    }

    public static class A extends TestRoboActivity {
    }

    public static class B extends TestRoboActivity {
        @Inject C c; // context scoped
        @Inject D d; // unscoped
        @Inject E e; // singleton
    }

    @ContextSingleton
    public static class C {}

    public static class D {}

    @Singleton
    public static class E {}
}
