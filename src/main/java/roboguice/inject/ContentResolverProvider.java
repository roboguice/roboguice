package roboguice.inject;

import com.google.inject.Inject;
import com.google.inject.Provider;

import android.content.ContentResolver;
import android.content.Context;

public class ContentResolverProvider implements Provider<ContentResolver> {
    @Inject protected Context activity;

    public ContentResolver get() {
        return activity.getContentResolver();
    }

}
