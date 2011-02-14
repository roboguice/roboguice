package roboguice.inject;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class AssetManagerProvider implements Provider<AssetManager> {
    @Inject protected Context context;

    public AssetManager get() {
        return context.getAssets();
    }
}
