package roboguice.inject;

import com.google.inject.Inject;
import com.google.inject.Provider;

import android.content.Context;
import android.content.res.AssetManager;

public class AssetManagerProvider implements Provider<AssetManager> {
    @Inject protected Context context;

    public AssetManager get() {
        return context.getAssets();
    }
}
