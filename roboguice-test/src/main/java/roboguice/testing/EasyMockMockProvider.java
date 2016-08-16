package roboguice.testing;

import com.google.inject.Provider;
import org.easymock.EasyMock;

/**
 * Provider of mock for a given class via EasyMock.
 * In principle, the mocks should be instances, bound to a class by Guice.
 * Nevertheless, instance see their fields automatically injected by Guice and this breaks
 * when instances are mocks. So we must use this provider to create mocks.
 *
 * @param <T> the class to mock
 * @see org.easymock.EasyMock#createMock(Class)
 */
public class EasyMockMockProvider<T> implements Provider<T> {
    protected T mock;

    public EasyMockMockProvider(Mock.MockType mockType, Class<T> clazz) {
        switch (mockType) {
            case NORMAL:
                this.mock = EasyMock.createMock(clazz);
                break;
            case STRICT:
                this.mock = EasyMock.createStrictMock(clazz);
                break;
            case NICE:
                this.mock = EasyMock.createNiceMock(clazz);
                break;
            default:
                throw new IllegalStateException();
        }
    }

    public EasyMockMockProvider(Class<T> clazz) {
        this(Mock.MockType.NORMAL, clazz);
    }

    public T get() {
        return mock;
    }
}
