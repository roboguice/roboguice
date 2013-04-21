package roboguice.context.event;

import android.content.Context;
import android.content.res.Configuration;

/**
 * Class representing the event raised by Context.onConfigurationChanged()
 *
 * @author Adam Tybor
 * @author John Ericksen
 */
public class OnConfigurationChangedEvent<T extends Context> {

    protected Configuration oldConfig;
    protected Configuration newConfig;
    protected T context;

    public OnConfigurationChangedEvent(T context, Configuration oldConfig, Configuration newConfig) {
        this.oldConfig = oldConfig;
        this.newConfig = newConfig;
        this.context = context;
    }

    public Configuration getOldConfig() {
        return oldConfig;
    }

    public Configuration getNewConfig() {
        return newConfig;
    }

    public T getContext() {
        return context;
    }
}
