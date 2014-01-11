package roboguice.receiver;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import roboguice.RoboGuice;

/**
 * To ensure proper ContextScope usage, override the handleReceive method
 */
public abstract class RoboBroadcastReceiver extends BroadcastReceiver {

    /** Handles the receive event.  This method should not be overridden, instead override
     * the handleReceive method to ensure that the proper ContextScope is maintained.
     * @param context
     * @param intent
     */
    @Override
    public final void onReceive(Context context, Intent intent) {
        RoboGuice.getBaseApplicationInjector((Application) context.getApplicationContext()).injectMembers(this);
        handleReceive(context, intent);
    }

    /**
     * Template method that should be overridden to handle the broadcast event
     * Using this method ensures that the proper ContextScope is maintained before and after
     * the execution of the receiver.
     * @param context
     * @param intent
     */
    protected void handleReceive(Context context, Intent intent) {
        // proper template method to handle the receive
    }

}
