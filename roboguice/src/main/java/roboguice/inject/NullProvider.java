package roboguice.inject;


import com.google.inject.Provider;

public class NullProvider<T> implements Provider<T> {
    @Override
    public T get() {
        return null;
    }


    static NullProvider<?> instance = new NullProvider<Object>();

    @SuppressWarnings("unchecked")
    public static <T> NullProvider<T> instance() {
        //noinspection unchecked
        return (NullProvider<T>) instance;
    }
}
