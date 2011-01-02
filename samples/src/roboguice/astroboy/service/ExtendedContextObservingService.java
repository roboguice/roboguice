package roboguice.astroboy.service;

import roboguice.activity.event.OnKeyDownEvent;
import roboguice.event.Observes;
import roboguice.util.Ln;

/**
 * @author John Ericksen
 */
public class ExtendedContextObservingService extends ContextObservingClassEventService{

    @Override
    public boolean toastKeyDown(@Observes OnKeyDownEvent onKeyDownEvent) {
        Ln.v("onKeyDown %1$s", onKeyDownEvent.getEvent());
        return false;
    }
}
