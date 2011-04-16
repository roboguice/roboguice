package roboguice.inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.annotation.Nullable;

@RunWith(RobolectricTestRunner.class)
public class NullableTest {

    @Test
    public void shouldRejectNullFields() throws Exception {
        assertThat(roboguice.inject.Nullable.isNullable(DummyClass.class.getDeclaredField("notNullable")), is(false));
    }

    @Test
    public void shouldAcceptNonNullFields() throws Exception {
        assertThat(roboguice.inject.Nullable.isNullable(DummyClass.class.getDeclaredField("nullable")), is(true));
    }


    public static class DummyClass {
        protected Object notNullable;
        @Nullable protected Object nullable;
    }
}
