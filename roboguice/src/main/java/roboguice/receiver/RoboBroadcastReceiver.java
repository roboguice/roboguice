package roboguice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.google.inject.Injector;
import roboguice.application.RoboApplication;
import roboguice.inject.ContextScope;

/**
 * Please be sure to call super.onReceive() when overriding onReceive()
 */
public abstract class RoboBroadcastReceiver extends BroadcastReceiver {
    protected ContextScope scope;

    @Override
    public void onReceive(Context context, Intent intent) {
        final Injector injector = ((RoboApplication) context.getApplicationContext()).getInjector();
        scope = injector.getInstance(ContextScope.class);
        scope.enter(context);
        injector.injectMembers(this);
    }

}
