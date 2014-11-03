package roboguice.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class StringsTest {

    @Test
    public void shouldProduceCorrectMd5String() {
        // http://code.google.com/p/roboguice/issues/detail?id=89
        final String s = "SiTrAax";
        final String expected = "96843ce5846566b00b5311c8904addfd";
        assertThat(Strings.md5(s), equalTo(expected));
    }
}
