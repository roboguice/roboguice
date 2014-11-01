package roboguice.fragment.event;

/**
 * Class representing the event raised by RoboFragment.onPause()
 *
 * @author Adam Tybor
 * @author John Ericksen
 * @author Cherry Development
 */
public class OnPauseEvent<T> extends AbstractFragmentEvent<T> {

    public OnPauseEvent(T fragment) {
        super(fragment);
    }
}
