package roboguice.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class LnImplTest {

    @Test
    public void shouldFormatArgs_whenArgsAreNull() {
        // https://github.com/roboguice/roboguice/issues/223
        final String s = "Message: %s";
        final String expected = "Message: null";
        assertThat(new LnImpl().formatArgs(s, null), equalTo(expected));
    }
    
    @Test
    public void shouldFormatArgs_whenArgsIs0Length() {
        // https://github.com/roboguice/roboguice/issues/223
        final String s = "Message: %s";
        final String expected = "Message: %s";
        assertThat(new LnImpl().formatArgs(s), equalTo(expected));
    }

}
