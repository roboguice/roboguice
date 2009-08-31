package roboguice.inject;

import com.google.inject.Inject;
import com.google.inject.Provider;

import android.app.Activity;
import android.content.ContentResolver;

public class ContentResolverProvider implements Provider<ContentResolver> {
    @Inject protected Activity activity;

    public ContentResolver get() {
        return activity.getContentResolver();
    }

}
