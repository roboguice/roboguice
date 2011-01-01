package roboguice.activity.event;

import android.content.res.Configuration;

public class OnConfigurationChangedEvent {

        private Configuration config;

        public OnConfigurationChangedEvent(Configuration config) {
            this.config = config;
        }

        public Configuration getConfig() {
            return config;
        }
    }