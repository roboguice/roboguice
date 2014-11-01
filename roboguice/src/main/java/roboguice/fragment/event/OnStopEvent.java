package roboguice.fragment.event;

/**
 * Class representing the event raised by RoboFragment.onStop()
 *
 * @author Adam Tybor
 * @author John Ericksen
 * @author Cherry Development
 */
public class OnStopEvent<T> extends AbstractFragmentEvent<T> {

    public OnStopEvent(T fragment) {
        super(fragment);
    }
}
