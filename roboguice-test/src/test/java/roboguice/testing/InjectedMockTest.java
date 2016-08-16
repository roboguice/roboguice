package roboguice.testing;

import javax.inject.Inject;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import roboguice.RoboGuice;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Sample test for
 * @author SNI.
 */
@RunWith(RobolectricTestRunner.class)
public class InjectedMockTest {

    @Inject
    @Mock
    private Mockable mockable;

    @Inject
    private ObjectUnderTest objectUnderTest;

    @Before
    public void setup() {
        InjectMockTestModule.setup(Robolectric.application, this);
    }

    @After
    public void tearDown() {
        //don't forget to clean the test module after each test.
        RoboGuice.Util.reset();
    }

    @Test
    public void shouldJustWorkAsExpected() {
        //GIVEN
        EasyMock.expect(mockable.doStuff()).andReturn(2);
        EasyMock.replay(mockable);

        //WHEN
        int i = objectUnderTest.mockable.doStuff();

        //THEN
        assertThat( i, is(2));
        EasyMock.verify(mockable);
    }

    @Test
    public void shouldJustWorkAsExpectedTheSecondTime() {
        //GIVEN
        EasyMock.expect(mockable.doStuff()).andReturn(5);
        EasyMock.replay(mockable);

        //WHEN
        int i = objectUnderTest.mockable.doStuff();

        //THEN
        assertThat( i, is(5));
        EasyMock.verify(mockable);
    }

    //could be any class in the application under test that uses dependencies.
    private static class ObjectUnderTest {
        @Inject
        private Mockable mockable;
    }

    private interface Mockable {
        int doStuff();
    }
}
