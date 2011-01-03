package roboguice.astroboy.service;

import roboguice.event.EventResultHandler;

/**
 * @author John Ericksen
 */
public class BooleanResultHandler implements EventResultHandler {

    protected boolean success;

    public void handleReturn(Object value) {
        if(value instanceof Boolean && (Boolean) value){
            success = true;
        }
    }

    public boolean isSuccess() {
        return success;
    }
}
