package roboguice.inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import javax.annotation.Nullable;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
