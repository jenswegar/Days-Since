package fi.wegar.android.dayssince;

import java.util.LinkedList;
import java.util.Queue;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class UpdateService extends Service implements Runnable {

	private static final String ACTION_UPDATE_ALL = "fi.wegar.android.dayssince.UPDATE_ALL";
	
	/**
	 * Used for debugging logs to identify logs from this class
	 */
	private static final String TAG = "DaysSince.UpdateService";
	/**
	 * Object used to lock synchronized statements and methods
	 */
	private static Object sLock = new Object();
	private static Queue<UpdateItem> sAppWidgetIDs = new LinkedList<UpdateItem>();

	private static boolean sThreadRunning = false;
	
	/**
	 * Queues an update request for the given appWidgetIDs. 
	 * The actual update should be started by calling onStart in the parent service.
	 * 
	 * @param appWidgetIDs
	 */
	public static void requestUpdate(Context context, int[] appWidgetIDs) {
		synchronized(sLock) {
			for(int appWidgetId : appWidgetIDs) {
				
				
				// fetch prefs for the given appWidgetId
				UpdateItem item = new UpdateItem(appWidgetId, WidgetConfiguration.loadPhotoSetId(context, appWidgetId) );
				
				// no point in updating if the photoset id has not yet been defined
				if(item.photoSetId != null) {
					Log.d(TAG, "Adding appWidgetID "+item.appWidgetId+", set id="+item.photoSetId);
					sAppWidgetIDs.add(item);
				}
			}
		}
	}

	/**
	 * @return True if more appWidgetIDs are in the queue to be updated 
	 */
	private static boolean hasMoreUpdates() {
		synchronized(sLock) {
			boolean hasMore = !sAppWidgetIDs.isEmpty();
			if(!hasMore) {
				// no more updates available, so stop thread
				sThreadRunning = false;
			}
			return hasMore;
		}
	}
	
	/**
	 * 
	 * @return The next appWidgetId to update
	 */
	private static UpdateItem getNextUpdate() {
		synchronized(sLock) {
			if(sAppWidgetIDs.peek() == null) {
				return null;
			} else {
				return sAppWidgetIDs.poll();	
			}
			
		}
	}
	
	/**
	 * Starts the actual update service on a new thread.
	 * 
	 * A new thread is only created if one doesn't exist already.
	 * 
	 */
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		if(ACTION_UPDATE_ALL.equals( intent.getAction() ) ) {
			Log.d(TAG, "Requested ACTION_UPDATE_ALL");
			AppWidgetManager manager = AppWidgetManager.getInstance(this);
			requestUpdate( this, manager.getAppWidgetIds( new ComponentName(this, DaysWidget.class) ) );
		}
		
		synchronized(sLock) {
			if( !sThreadRunning ) {
				sThreadRunning = true;
				new Thread(this).start();
			}
		}
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG, "Service onDestroy called");

		super.onDestroy();
	}
	
	/**
	 * Only implemented to honor Runnable contract, no real purpose in this class.
	 * @private
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void run() {
		Log.d(TAG, "Processing thread started");
		
		while( hasMoreUpdates() ) {
			UpdateItem item = getNextUpdate();
			int count = 0;
			try{
				count = WebServiceHelper.updatePhotoCount( item.photoSetId );
			} catch(Exception e) {
				
			} finally {
				RemoteViews updateViews = DaysWidget.buildUpdate(this, count);
				AppWidgetManager.getInstance(this).updateAppWidget(item.appWidgetId, updateViews);
			}
		}
		Log.d(TAG, "View update complete");
		stopSelf();
	}
}
