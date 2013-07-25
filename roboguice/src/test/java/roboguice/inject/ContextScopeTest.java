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
import roboguice.activity.RoboActivity;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class ContextScopeTest {


    @Test
    public void shouldHaveContextInScopeMapAfterOnCreate() throws Exception {
        final ActivityController<A> aController = Robolectric.buildActivity(A.class);
        final A a = aController.get();

        assertThat(a.getScopedObjectMap().size(), equalTo(0));
        aController.create();

        boolean found=false;
        for( Object o : a.getScopedObjectMap().values() )
            if( o==a )
                found = true;

        assertTrue("Couldn't find context in scope map", found);
    }

    @Test
    public void shouldBeAbleToOpenMultipleScopes() {
        final ContextScope scope = RoboGuice.getBaseApplicationInjector(Robolectric.application).getInstance(ContextScope.class);
        final Activity a = Robolectric.buildActivity(A.class).get();
        final Activity b = Robolectric.buildActivity(B.class).get();

        scope.enter(a);
        scope.enter(b);
        scope.exit(b);
        scope.exit(a);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotBeAbleToExitTheWrongScope() {
        final ContextScope scope = RoboGuice.getBaseApplicationInjector(Robolectric.application).getInstance(ContextScope.class);
        final Activity a = Robolectric.buildActivity(A.class).get();
        final Activity b = Robolectric.buildActivity(B.class).get();

        scope.enter(a);
        scope.enter(b);
        scope.exit(a);
    }

    @Test
    public void shouldHaveTwoItemsInScopeMapAfterOnCreate() throws Exception {
        final ActivityController<B> bController = Robolectric.buildActivity(B.class);
        final B b = bController.get();

        assertThat(b.getScopedObjectMap().size(), equalTo(0));
        bController.create();

        boolean found=false;
        for( Object o : b.getScopedObjectMap().values() )
            if( o==b )
                found = true;

        assertTrue("Couldn't find context in scope map", found);
        assertTrue(b.getScopedObjectMap().containsKey(Key.get(C.class)));
    }

    public static class A extends RoboActivity {
    }

    public static class B extends RoboActivity {
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
