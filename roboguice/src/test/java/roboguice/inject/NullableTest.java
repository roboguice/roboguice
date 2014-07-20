package roboguice.inject;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.annotation.Nullable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

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
