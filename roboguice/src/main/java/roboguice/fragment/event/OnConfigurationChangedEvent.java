package roboguice.fragment.event;

import android.content.res.Configuration;

/**
 * Class representing the event raised by RoboFragment.onConfigurationChanged()
 *
 * @author Adam Tybor
 * @author John Ericksen
 * @author Cherry Development
 */
public class OnConfigurationChangedEvent<T> extends AbstractFragmentEvent<T> {

    protected Configuration oldConfig;
    protected Configuration newConfig;

    public OnConfigurationChangedEvent(T fragment, Configuration oldConfig, Configuration newConfig) {
        super(fragment);
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
