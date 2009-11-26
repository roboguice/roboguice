package roboguice.inject;

import android.content.res.Resources;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class StringResourceFactory implements ResourceFactory<String>{

    protected Resources         resources;

    @Inject
    public StringResourceFactory(Resources resources) {
        this.resources = resources;
    }

    public String get(int id) {
        return resources.getString(id);
    }
}
