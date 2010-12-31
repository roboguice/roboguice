package roboguice.astroboy.service;

import roboguice.inject.EventResultHandler;

/**
 * @author John Ericksen
 */
public class BooleanResultHandler implements EventResultHandler {

    boolean success;

    public void handleReturn(Object value) {
        if(value instanceof Boolean && (Boolean) value){
            success = true;
        }
    }

    public boolean isSuccess() {
        return success;
    }
}
