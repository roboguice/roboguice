package roboguice.inject;

import com.google.inject.Inject;
import com.google.inject.Provider;

import android.content.Context;

public class SystemServiceProvider<T> implements Provider<T>{

    @Inject protected Provider<Context> context;
    protected String service;

    public SystemServiceProvider( String service ) {
        this.service = service;
    }

    @SuppressWarnings("unchecked")
    public T get() {
        return (T) context.get().getSystemService(service);
    }

}
