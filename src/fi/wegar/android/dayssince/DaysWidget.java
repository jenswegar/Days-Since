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
		
		Log.d(TAG, "appWidgetIds length at start:"+appWidgetIds.length);
		
		final int N =  appWidgetIds.length;
		
		// perform fast updates on each running widget instance directly
		for(int i=0; i < N; i++) {
			int widgetId = appWidgetIds[i];	
			
			updateWidgetDisplayDefaults(appWidgetManager, context, widgetId);
			
		}
		
		// request remote service updates through update service
		UpdateService.requestUpdate(context, appWidgetIds);
		context.startService(new Intent(context, UpdateService.class));
	}
	
	private static int getDaysSinceNewYear() {
		// calculate the number of days since new years
		GregorianCalendar now = new GregorianCalendar();

		return now.get(GregorianCalendar.DAY_OF_YEAR);
	}
	
	/**
	 * Creates a string representation of the last new year date
	 * 
	 * @return A String
	 */
	private static String getNewYearAsString() {
		// calculate the number of days since new years
		GregorianCalendar now = new GregorianCalendar();
		
		// create a date and string representing the new year
		GregorianCalendar newYearsDay = new GregorianCalendar( now.get(GregorianCalendar.YEAR), 0, 1);
		DateFormat fmt = new SimpleDateFormat("MMM dd, yyyy ");
	
		return fmt.format( newYearsDay.getTime() );
	}
	
	/**
	 * Updates view that displays remote service result
	 * 
	 * @param context
	 * @param photoCount
	 * @return
	 */
	public static RemoteViews buildUpdate(Context context, int photoCount) {
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.main);
		
		remoteViews.setTextViewText(R.id.widget_textview_flickr, String.valueOf(photoCount) );
		
		return remoteViews;
	}
	
	/**
	 * Updates the values of widgets that do not require a remote service call
	 * 
	 * @param appWidgetManager
	 * @param context
	 * @param widgetId
	 */
	public static void updateWidgetDisplayDefaults(AppWidgetManager appWidgetManager, Context context, int widgetId) {
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.main);
		remoteViews.setTextViewText(R.id.widget_textview, ""+getDaysSinceNewYear() );
		
		remoteViews.setTextViewText(R.id.widget_header, context.getText(R.string.headerDisplay)+" "+getNewYearAsString() );

		Log.d(TAG, "update widget id: "+widgetId);
		appWidgetManager.updateAppWidget(widgetId, remoteViews);
		
	}
	
	public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
			int widgetId, String setId) {
		
		updateWidgetDisplayDefaults(appWidgetManager, context, widgetId);

		// add widgetId to an array 
		int[] appWidgetIds = new int[1];
		appWidgetIds[0] = widgetId;
		
		// request remote service updates through update service
		UpdateService.requestUpdate(context, appWidgetIds);
		context.startService(new Intent(context, UpdateService.class));
		
	}
}

