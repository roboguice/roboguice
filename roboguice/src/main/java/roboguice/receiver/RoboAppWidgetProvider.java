package roboguice.receiver;

import roboguice.RoboGuice;

import com.google.inject.Injector;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;

public abstract class RoboAppWidgetProvider extends AppWidgetProvider {

    @Override
    public final void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final Injector injector = RoboGuice.getBaseApplicationInjector((Application) context.getApplicationContext());

        injector.injectMembers(this);
        onHandleUpdate(context, appWidgetManager, appWidgetIds);
    }

    @SuppressWarnings("UnusedParameters")
    public void onHandleUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // proper template method to handle the receive
    }
}
