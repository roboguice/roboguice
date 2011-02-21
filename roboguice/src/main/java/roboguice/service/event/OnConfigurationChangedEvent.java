package roboguice.service.event;

import android.content.res.Configuration;

/**
 * Class representing the event raised by RoboActivity.onConfigurationChanged()
 *
 * @author Adam Tybor
 * @author John Ericksen
 */
public class OnConfigurationChangedEvent {

    protected Configuration oldConfig;
    protected Configuration newConfig;

    public OnConfigurationChangedEvent(Configuration oldConfig, Configuration newConfig) {
        this.oldConfig = oldConfig;
        this.newConfig = newConfig;
    }

    public Configuration getOldConfig() {
        return oldConfig;
    }

    public Configuration getNewConfig() {
        return newConfig;
    }
}
