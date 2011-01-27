package roboguice.receiver;

import roboguice.RoboGuice;
import roboguice.inject.ContextScope;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.inject.Injector;

/**
 * Please be sure to call super.onReceive() when overriding onReceive()
 */
public abstract class RoboBroadcastReceiver extends BroadcastReceiver {
    protected ContextScope scope;

    @Override
    public void onReceive(Context context, Intent intent) {
        final Injector injector = RoboGuice.getInjector((Application)context.getApplicationContext());
        scope = injector.getInstance(ContextScope.class);
        scope.enter(context);
        injector.injectMembers(this);
    }

}
