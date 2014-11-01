package roboguice.fragment.event;

/**
 * Class representing the event raised by RoboFragment.onStart()
 *
 * @author Adam Tybor
 * @author John Ericksen
 * @author Cherry Development
 */
public class OnStartEvent<T> extends AbstractFragmentEvent<T> {

    public OnStartEvent(T fragment) {
        super(fragment);
    }
}
