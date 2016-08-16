package roboguice.testing;

import android.app.Application;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.lang.reflect.Field;
import roboguice.RoboGuice;
import roboguice.util.Ln;

/**
 * MockInject test module. Instances will automatically bind all @Mock annotated fields in a test
 * to RoboGuice.
 *
 * This class also offers a static helper : {@link #setup(Application, Object)}. That will do all the
 * wiring for you.
 *
 * A complete example can be found in {@code roboguice.testing.InjectedMockTest} in the test source folder
 * of this maven module.
 *
 * Mock annotations can be either {@link Mock} (from RoboGuice) or {@link org.mockito.Mock} (from Mockito).
 * There is a minor issue in EasyMock 3.2 that prevents it from working with Robolectric.
 * When EasyMock will fix this, we will also support EasyMock {@code @Mock} annotation.
 *
 * Put either Mockito or EasyMock in the test classpath and the right library will be choosen automatically.
 *
 * EasyMock has priority over Mockito in case both libs are detected.
 *
 * @author SNI.
 */
public class InjectMockTestModule extends AbstractModule {

    private boolean isUsingEasyMock;
    private boolean isUsingMockito;
    private Object test;

    public static void setup(Application application, Object test) {
        Module testModule = new InjectMockTestModule(test);
        Injector injector = RoboGuice.overrideApplicationInjector(application, testModule);
        injector.injectMembers(test);
    }

    public InjectMockTestModule(Object test) {
        this.test = test;
        isUsingEasyMock = canFindClass("org.easymock.EasyMock");
        isUsingMockito = canFindClass("org.mockito.Mockito");
        if (!isUsingMockito && !isUsingEasyMock) {
            throw new IllegalStateException("Neither easymock nor mockito can be found in the classpath. Please add one of them.");
        }
    }

    @Override protected void configure() {
        bindAllMocks(test);
    }

    public boolean isUsingEasyMock() {
        return isUsingEasyMock;
    }

    public boolean isUsingMockito() {
        return isUsingMockito;
    }

    public void setUsingEasyMock(boolean isUsingEasyMock) {
        this.isUsingEasyMock = isUsingEasyMock;
    }

    public void setUsingMockito(boolean isUsingMockito) {
        this.isUsingMockito = isUsingMockito;
    }

    private boolean canFindClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            Ln.d("Could not find class %s", className);
            return false;
        }
    }

    /**
     * Bind all {@link Mock} annotated field of a given test.
     *
     * @param test the test whose fields are going to be injected.
     */
    @SuppressWarnings("unchecked")
    private void bindAllMocks(Object test) {
        for (Field field : test.getClass().getDeclaredFields()) {
            Object mock = null;
            Mock mockAnnotation = field.getAnnotation(Mock.class);
            if (mockAnnotation != null) {
                if (isUsingEasyMock) {
                    EasyMockMockProvider mockProvider = new EasyMockMockProvider(mockAnnotation.value(), field.getType());
                    mock = mockProvider.get();
                    bind((Class) field.getType()).toInstance(mock);
                } else if (isUsingMockito) {
                    MockitoMockProvider mockProvider = new MockitoMockProvider(mockAnnotation.value(), field.getType());
                    mock = mockProvider.get();
                    bind((Class) field.getType()).toInstance(mock);
                }
            }

            org.mockito.Mock mockitoMockAnnotation = field.getAnnotation(org.mockito.Mock.class);
            if (mockitoMockAnnotation != null) {
                MockitoMockProvider mockProvider = new MockitoMockProvider(Mock.MockType.NORMAL, field.getType());
                mock = mockProvider.get();
                bind((Class) field.getType()).toInstance(mock);
            }
        }
    }
}
