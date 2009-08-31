package roboguice.inject;

import com.google.inject.Provider;

import android.content.res.Resources;

public class StringResourceProvider implements Provider<String> {
    protected Resources resources;
    protected int id;

    public StringResourceProvider( Resources resources, int id ) {
        this.resources = resources;
        this.id = id;
    }

    public String get() {
        return resources.getString(id);
    }
}
