package roboguice.fragment.event;

public abstract class AbstractFragmentEvent<T> {
    protected final T fragment;

    public AbstractFragmentEvent(T fragment) {
        this.fragment = fragment;
    }

    public T getFragment() {
        return fragment;
    }
}
