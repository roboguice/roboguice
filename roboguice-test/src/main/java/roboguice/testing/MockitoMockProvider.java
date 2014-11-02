package roboguice.testing;

import com.google.inject.Provider;
import org.mockito.Mockito;

/**
 * Provider of mock for a given class via Mockito.
 * In principle, the mocks should be instances, bound to a class by Guice.
 * Nevertheless, instance see their fields automatically injected by Guice and this breaks
 * when instances are mocks. So we must use this provider to create mocks.
 *
 * @param <T> the class to mock
 * @see Mockito#mock(Class)
 */
public class MockitoMockProvider<T> implements Provider<T> {
    protected T mock;

    public MockitoMockProvider(Mock.MockType mockType, Class<T> clazz) {
        switch (mockType) {

            case NORMAL:
                this.mock = Mockito.mock(clazz);
                break;
            case STRICT:
                throw new IllegalArgumentException("Mockito doesn't support strict mocks");
            case NICE:
                throw new IllegalArgumentException("Mockito doesn't support nice mocks");
            default:
                throw new IllegalStateException();
        }
    }

    public MockitoMockProvider(Class<T> clazz) {
        this(Mock.MockType.NORMAL, clazz);
    }

    public T get() {
        return mock;
    }
}
