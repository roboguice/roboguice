package roboguice.event;

/**
 * @author Adam Tabor
 * @author John Ericksen
 */
public class NoOpResultHandler implements EventResultHandler {
    @Override
    public void handleReturn(Object invoke) {
        //no-op
    }
}
