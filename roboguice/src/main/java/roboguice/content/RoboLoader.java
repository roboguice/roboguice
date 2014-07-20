package roboguice.content;

import roboguice.RoboGuice;

import android.content.Context;
import android.support.v4.content.Loader;

/**
 * Provides injection to loaders.
 * @author cherrydev
 */
public abstract class RoboLoader<D> extends Loader<D> {

    public RoboLoader(Context context) {
        super(context);
        RoboGuice.injectMembers(context, this);
    }

}
