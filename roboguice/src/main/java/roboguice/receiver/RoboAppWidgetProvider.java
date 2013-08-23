package roboguice.receiver;

import android.app.Application;
import android.appwidget.AppWidgetProvider;
import android.content.Context;

import com.google.inject.Injector;

public class RoboAppWidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		final Injector injector = RoboGuice.getBaseApplicationInjector((Application) context.getApplicationContext());

		injector.injectMembers(this);
		onHandleUpdate(context, appWidgetManager, appWidgetIds);
	}

	public void onHandleUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
  }
}
