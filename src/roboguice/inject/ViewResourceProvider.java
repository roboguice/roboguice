package roboguice.inject;

import com.google.inject.Provider;

import android.app.Activity;
import android.view.View;

public class ViewResourceProvider<T extends View> implements Provider<T> {
    protected Activity activity;
    protected int id;

    public ViewResourceProvider( Activity activity, int id ) {
        this.activity = activity;
        this.id = id;
    }

    @SuppressWarnings("unchecked")
    public T get() {
        return (T) activity.findViewById(id);
    }
}
