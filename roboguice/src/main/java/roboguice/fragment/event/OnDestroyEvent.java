package roboguice.fragment.event;

/**
 * Class representing the event raised by RoboFragment.onDestroy()
 *
 * @author Adam Tybor
 * @author John Ericksen
 * @author Cherry Development
 */
public class OnDestroyEvent<T> extends AbstractFragmentEvent<T> {

    public OnDestroyEvent(T fragment) {
        super(fragment);
    }
}
