package roboguice.inject;

import android.app.Activity;
import android.content.res.Resources;

import com.google.inject.Inject;
import com.google.inject.Provider;


public class ResourcesProvider implements Provider<Resources> {
    protected Resources obj;

    @Inject
    public ResourcesProvider( Activity activity ) {
        obj = activity.getResources();
    }

    public Resources get() {
        return obj;
    }
}