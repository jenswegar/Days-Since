package fi.wegar.android.dayssince;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class DaysWidget extends AppWidgetProvider {

	private static final String TAG = "DaysSince";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		
		// If no specific widgets requested, collect list of all
		if (appWidgetIds == null) {
			appWidgetIds = appWidgetManager.getAppWidgetIds(
					new ComponentName(context, DaysWidget.class));
		}
		
		final int N = appWidgetIds.length;
		
		// calculate the number of days since new years
		GregorianCalendar now = new GregorianCalendar();
		
		// create a date and string representing the new year
		GregorianCalendar newYearsDay = new GregorianCalendar( now.get(GregorianCalendar.YEAR), 0, 1);
		DateFormat fmt = new SimpleDateFormat("MMM dd, yyyy ");
		
		// perform fast updates on each running widget instance directly
		for(int i = 0; i < N; i++) {
			
			int appWidgetId = appWidgetIds[i];
			
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.main);
			remoteViews.setTextViewText(R.id.widget_textview, ""+now.get(GregorianCalendar.DAY_OF_YEAR) );
			
			remoteViews.setTextViewText(R.id.widget_header, context.getText(R.string.headerDisplay)+" "+fmt.format( newYearsDay.getTime() ) );

			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
		}
		
		Log.d(TAG, "UpdateService.requestUpdate called");
		// request remote service updates through update service
		UpdateService.requestUpdate(appWidgetIds);
		context.startService(new Intent(context, UpdateService.class));
	}
	
	public static RemoteViews buildUpdate(Context context, int photoCount) {
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.main);
		
		remoteViews.setTextViewText(R.id.widget_textview_flickr, String.valueOf(photoCount) );
		
		return remoteViews;
	}
}
