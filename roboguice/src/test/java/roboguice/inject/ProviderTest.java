package roboguice.inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import roboguice.activity.RoboActivity;

import android.content.Context;
import android.os.Bundle;

import javax.inject.Inject;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class ProviderTest {

    @Test(expected = AssertionError.class)
    public void shouldNotReturnProperContext() throws Exception {
        final A a = Robolectric.buildActivity(A.class).create().get();
        final B b = Robolectric.buildActivity(B.class).create().get();
        final FutureTask<Context> future = new FutureTask<Context>(new Callable<Context>() {
            @Override
            public Context call() throws Exception {
                return a.contextProvider.get(b);
            }
        });
        Executors.newSingleThreadExecutor().execute(future);

        assertThat(future.get(), equalTo((Context)a));
    }

    @Test
    public void shouldReturnProperContext() throws Exception {
        //noinspection UnusedDeclaration
        final B b = Robolectric.buildActivity(B.class).create().get();

        final C c = Robolectric.buildActivity(C.class).create().get();
        final FutureTask<Context> future = new FutureTask<Context>(new Callable<Context>() {
            @Override
            public Context call() throws Exception {
                return c.contextProvider.get(c);
            }
        });
        Executors.newSingleThreadExecutor().execute(future);

        assertThat(future.get(), equalTo((Context)c));
    }


    public static class A extends RoboActivity {
        @Inject ContextScopedProvider<Context> contextProvider;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
    }

    public static class B extends RoboActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
    }

    public static class C extends RoboActivity {
        @Inject ContextScopedProvider<Context> contextProvider;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
    }
}
