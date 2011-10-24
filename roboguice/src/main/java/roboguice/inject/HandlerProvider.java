package roboguice.inject;

import android.os.Handler;
import android.os.Looper;

import com.google.inject.Provider;

public class HandlerProvider implements Provider<Handler> {
    @Override
    public Handler get() {
        return new Handler(Looper.getMainLooper());
    }
}
