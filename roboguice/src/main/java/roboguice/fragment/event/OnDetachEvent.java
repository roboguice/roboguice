package roboguice.fragment.event;

/**
 * Class representing the event raised by RoboFragment.onDetach()
 *
 * @author Adam Tybor
 * @author John Ericksen
 * @author Cherry Development
 */
public class OnDetachEvent<T> extends AbstractFragmentEvent<T> {

    public OnDetachEvent(T fragment) {
        super(fragment);
    }
}
