package roboguice.inject;

import com.google.inject.Inject;
import com.google.inject.Provider;

import android.content.Context;
import android.content.res.Resources;


public class ResourcesProvider implements Provider<Resources> {
    protected Resources obj;

    @Inject
    public ResourcesProvider( Context context ) {
        obj = context.getResources();
    }

    public Resources get() {
        return obj;
    }
}