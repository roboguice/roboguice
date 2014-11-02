package roboguice.testing;

import com.google.inject.CreationException;
import com.google.inject.Injector;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static roboguice.testing.Mock.MockType.NICE;
import static roboguice.testing.Mock.MockType.STRICT;

/**
 * @author SNI.
 */
@RunWith(RobolectricTestRunner.class)
public class InjectMockTestModuleTest {

    @Before
    public void setup() {
        RoboGuice.Util.reset();
    }

    @After
    public void tearDown() {
        RoboGuice.Util.reset();
    }

    @Test
    public void shouldInjectEasyMockMocks() {

        //GIVEN
        TestWithInjectMock test = new TestWithInjectMock();

        //WHEN
        InjectMockTestModule.setup(Robolectric.application, test);

        //THEN
        assertNotNull(test.mockable);
        Mockable mockable = RoboGuice.getInjector(Robolectric.application).getInstance(Mockable.class);
        assertThat(mockable, is(test.mockable));
    }

    @Test
    public void shouldInjectMockitoMocksWhenSetToUsingMockito() {
        //GIVEN
        TestWithInjectMock test = new TestWithInjectMock();

        //WHEN
        InjectMockTestModule testModule = new InjectMockTestModule(test);
        testModule.setUsingMockito(true);
        testModule.setUsingEasyMock(false);
        Injector injector = RoboGuice.overrideApplicationInjector(Robolectric.application, testModule);
        injector.injectMembers(test);

        //THEN
        assertNotNull(test.mockable);
        Mockable mockable = RoboGuice.getInjector(Robolectric.application).getInstance(Mockable.class);
        assertThat(mockable, is(test.mockable));
    }

    @Test
    public void shouldInjectMockitoMocksWhenUsingMockitoAnnotation() {
        //GIVEN
        TestWithMockitoMock test = new TestWithMockitoMock();

        //WHEN
        InjectMockTestModule testModule = new InjectMockTestModule(test);
        testModule.setUsingMockito(true);
        testModule.setUsingEasyMock(false);
        Injector injector = RoboGuice.overrideApplicationInjector(Robolectric.application, testModule);
        injector.injectMembers(test);

        //THEN
        assertNotNull(test.mockable);
        Mockable mockable = RoboGuice.getInjector(Robolectric.application).getInstance(Mockable.class);
        assertThat(mockable, is(test.mockable));
    }

    @Test(expected = CreationException.class)
    public void shouldFailToInjectNiceMocksWithMockito() {
        //GIVEN
        TestWithInjectNiceMock test = new TestWithInjectNiceMock();

        //WHEN
        InjectMockTestModule testModule = new InjectMockTestModule(test);
        testModule.setUsingMockito(true);
        testModule.setUsingEasyMock(false);
        Injector injector = RoboGuice.overrideApplicationInjector(Robolectric.application, testModule);
        injector.injectMembers(test);

        //THEN
        fail();
    }

    @Test(expected = CreationException.class)
    public void shouldFailToInjectStrictMocksWithMockito() {
        //GIVEN
        TestWithInjectStrictMock test = new TestWithInjectStrictMock();

        //WHEN
        InjectMockTestModule testModule = new InjectMockTestModule(test);
        testModule.setUsingMockito(true);
        testModule.setUsingEasyMock(false);
        Injector injector = RoboGuice.overrideApplicationInjector(Robolectric.application, testModule);
        injector.injectMembers(test);

        //THEN
        fail();
    }

    private static class TestWithInjectMock {
        @Inject
        @Mock
        private Mockable mockable;
    }

    private static class TestWithInjectNiceMock {
        @Inject
        @Mock(NICE)
        private Mockable mockable;
    }

    private static class TestWithInjectStrictMock {
        @Inject
        @Mock(STRICT)
        private Mockable mockable;
    }

    private static class TestWithMockitoMock {
        @Inject
        @org.mockito.Mock
        private Mockable mockable;
    }

    private interface Mockable {
        int doStuff();
    }
}
