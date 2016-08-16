package roboguice.inject;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import roboguice.RoboGuice;
import roboguice.activity.TestRoboActivity;

import android.content.Context;
import android.os.Bundle;

@RunWith(RobolectricTestRunner.class)
public class ProviderTest {

    @Before
    public void setUp() throws Exception {
        RoboGuice.setupBaseApplicationInjector(Robolectric.application);
    }

    @After
    public void tearDown() throws Exception {
        RoboGuice.Util.reset();
    }

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
        @SuppressWarnings("unused")
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


    public static class A extends TestRoboActivity {
        @Inject ContextScopedProvider<Context> contextProvider;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
    }

    public static class B extends TestRoboActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
    }

    public static class C extends TestRoboActivity {
        @Inject ContextScopedProvider<Context> contextProvider;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
    }
}
