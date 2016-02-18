package roboguice.inject;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Field;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.tester.android.content.TestSharedPreferences;

import roboguice.RoboGuice;
import roboguice.activity.TestRoboActivity;

import com.google.inject.Inject;
import com.google.inject.AbstractModule;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

@SuppressWarnings("ResultOfMethodCallIgnored")
@RunWith(RobolectricTestRunner.class)
public class SharedPreferencesProviderTest {

    @Test
    public void shouldInjectDefaultSharedPrefs() throws Exception {
        final A a = Robolectric.buildActivity(A.class).create().get();

        final Field f = TestSharedPreferences.class.getDeclaredField("filename");
        f.setAccessible(true);

        assertThat(f.get(a.prefs), notNullValue());
        assertThat((String) f.get(a.prefs), is(not("")));
        assertThat(f.get(a.prefs), equalTo(f.get(PreferenceManager.getDefaultSharedPreferences(a))));
    }

    @Test
    public void shouldInjectNamedSharedPrefs() throws Exception {
        RoboGuice.getOrCreateBaseApplicationInjector(Robolectric.application,RoboGuice.DEFAULT_STAGE, RoboGuice.newDefaultRoboModule(Robolectric.application), new ModuleA() );
        try {

            final A a = Robolectric.buildActivity(A.class).create().get();
            final Field f = TestSharedPreferences.class.getDeclaredField("filename");
            f.setAccessible(true);

            assertEquals("FOOBAR", f.get(a.prefs));

        } finally {
            RoboGuice.Util.reset();
        }
    }

    @Test
    public void shouldFallbackOnOldDefaultIfPresent() throws Exception {
        final File oldDefault = new File("shared_prefs/default.xml");
        final File oldDir = new File("shared_prefs");

        oldDir.mkdirs();
        oldDefault.createNewFile();
        try {
            final A a = Robolectric.buildActivity(A.class).create().get();
            final Field f = TestSharedPreferences.class.getDeclaredField("filename");
            f.setAccessible(true);

            assertThat(f.get(a.prefs), notNullValue());
            assertThat((String) f.get(a.prefs), is(not("")));
            assertEquals("default.xml", f.get(a.prefs));

        } finally {
            oldDefault.delete();
            oldDir.delete();
        }
    }

    @Test
    public void shouldNotFallbackOnOldDefaultIfNamedFileSpecified() throws Exception {
        RoboGuice.getOrCreateBaseApplicationInjector(Robolectric.application,RoboGuice.DEFAULT_STAGE, RoboGuice.newDefaultRoboModule(Robolectric.application), new ModuleA() );

        final File oldDefault = new File("shared_prefs/default.xml");
        final File oldDir = new File("shared_prefs");

        oldDir.mkdirs();
        oldDefault.createNewFile();
        try {
            final A a = Robolectric.buildActivity(A.class).create().get();
            final Field f = TestSharedPreferences.class.getDeclaredField("filename");
            f.setAccessible(true);

            assertThat(f.get(a.prefs), notNullValue());
            assertThat((String) f.get(a.prefs), is(not("")));
            assertEquals("FOOBAR", f.get(a.prefs));

        } finally {
            RoboGuice.Util.reset();
            oldDefault.delete();
            oldDir.delete();
        }
    }

    public static class A extends TestRoboActivity {
        @Inject
        SharedPreferences prefs;
    }

    public static class ModuleA extends AbstractModule {
        @Override
        protected void configure() {
            bindConstant().annotatedWith(SharedPreferencesName.class).to("FOOBAR");
        }
    }
}
