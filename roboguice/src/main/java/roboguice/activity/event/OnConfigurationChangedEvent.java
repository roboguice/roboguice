package roboguice.activity.event;

import android.content.res.Configuration;

/**
 * Class representing the event raised by RoboActivity.onConfigurationChanged()
 *
 * @author Adam Tybor
 * @author John Ericksen
 */
public class OnConfigurationChangedEvent {

        protected Configuration config;

        public OnConfigurationChangedEvent(Configuration config) {
            this.config = config;
        }

        public Configuration getConfig() {
            return config;
        }
    }
