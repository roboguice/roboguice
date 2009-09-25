package roboguice.inject;

import com.google.inject.Inject;
import com.google.inject.Provider;

import android.app.Activity;
import android.content.res.Resources;


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