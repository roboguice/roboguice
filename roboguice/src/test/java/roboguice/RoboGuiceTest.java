package roboguice;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;

import org.hamcrest.core.IsInstanceOf;
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import roboguice.activity.RoboActivity;

import com.google.inject.AbstractModule;
import com.google.inject.Stage;

import android.app.Activity;
import android.app.Application;

@RunWith(RobolectricTestRunner.class)
public class RoboGuiceTest {

    @Before
    public void setup() {
        RoboGuice.injectors.clear();
    }

    @Test
    public void destroyInjectorShouldRemoveContext() {
        final Activity activity = Robolectric.buildActivity(RoboActivity.class).get();
        RoboGuice.getInjector(activity);

        assertThat(RoboGuice.injectors.size(), equalTo(1));

        RoboGuice.destroyInjector(activity);
        assertThat(RoboGuice.injectors.size(), equalTo(1));

        RoboGuice.destroyInjector(Robolectric.application);
        assertThat(RoboGuice.injectors.size(), equalTo(0));
    }

    @Test
    public void resetShouldRemoveContext() {
        final Activity activity = Robolectric.buildActivity(RoboActivity.class).get();
        RoboGuice.getInjector(activity);

        assertThat(RoboGuice.injectors.size(), equalTo(1));

        RoboGuice.Util.reset();
        assertThat(RoboGuice.injectors.size(), equalTo(0));
    }

    // https://github.com/roboguice/roboguice/issues/87
    @Test
    public void shouldOnlyCallConfigureOnce() {
        final int[] i = { 0 };
        RoboGuice.setBaseApplicationInjector(Robolectric.application, Stage.DEVELOPMENT, RoboGuice.newDefaultRoboModule(Robolectric.application),
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        ++i[0];
                    }
                });
        assertThat(i[0], equalTo(1));
    }

    @Test
    public void shouldOverrideModulesForTests() {
        RoboGuice.overrideApplicationInjector(Robolectric.application,
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(Bar.class).to(Bar1.class);
                    }
                });
        Foo foo = RoboGuice.getInjector(Robolectric.application).getInstance(Foo.class);
        assertThat(foo, IsNull.notNullValue());
        assertThat(foo.bar, IsInstanceOf.instanceOf(Bar1.class));
        assertThat(foo.bar, IsInstanceOf.instanceOf(Bar1.class));
        //we received default robo module injections as well
        assertThat(foo.application, IsNull.notNullValue());
    }


    static class Foo {
        @Inject Application application;
        @Inject Bar bar;
    }

    static class Bar {
    }

    static class Bar1 extends Bar{
    }

}
