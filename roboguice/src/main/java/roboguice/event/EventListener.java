package roboguice.event;

public interface EventListener<T> {
    public void onEvent(T event);
}
