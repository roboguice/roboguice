package roboguice.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class StringsTest {

    @Test
    public void shouldProduceCorrectMd5String() {
        // http://code.google.com/p/roboguice/issues/detail?id=89
        final String s = "SiTrAax";
        final String expected = "96843ce5846566b00b5311c8904addfd";
        assertThat(Strings.md5(s), equalTo(expected));
    }

    @Test
    public void shouldJoin_non_empty_collection() {
        //GIVEN
        List<String> objs = Arrays.asList("a","b");

        //WHEN
        String joined = Strings.join(":", objs);

        //THEN
        assertThat(joined, equalTo("a:b"));
    }

    @Test
    public void shouldJoin_empty_collection() {
        //GIVEN
        List<String> objs = Arrays.asList();

        //WHEN
        String joined = Strings.join(":", objs);

        //THEN
        assertThat(joined, equalTo(""));
    }

    @Test
    public void shouldJoin_skip_null_item() {
        //GIVEN
        List<String> objs = Arrays.asList(null,"a");

        //WHEN
        String joined = Strings.join(":", objs);

        //THEN
        assertThat(joined, equalTo("a"));
    }

    @Test
    public void shouldJoinAnd_non_empty_collection() {
        //GIVEN
        List<String> objs = Arrays.asList("a","b","c");

        //WHEN
        String joined = Strings.joinAnd(":", "-", objs);

        //THEN
        assertThat(joined, equalTo("a:b-c"));
    }

    @Test
    public void shouldJoinAnd_empty_collection() {
        //GIVEN
        List<String> objs = Arrays.asList();

        //WHEN
        String joined = Strings.joinAnd(":", "-", objs);

        //THEN
        assertThat(joined, equalTo(""));
    }

    @Test
    public void shouldJoinAnd_skip_null_item() {
        //GIVEN
        List<String> objs = Arrays.asList(null,"a","b","c");

        //WHEN
        String joined = Strings.joinAnd(":", "-", objs);

        //THEN
        assertThat(joined, equalTo("a:b-c"));
    }
}
