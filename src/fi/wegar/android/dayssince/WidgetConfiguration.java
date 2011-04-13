package fi.wegar.android.dayssince;

import org.apache.http.util.LangUtils;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;

public class WidgetConfiguration extends Activity {

	private static final String TAG = "DaysSince.WidgetConfiguration";
	
	private static final String PREFS_NAME = "fi.wegar.android.dayssince.DaysWidget";
	private static final String PREFS_PHOTOSET_PREFIX = "photoSetId_";
	
	/**
	 * Storage point for the ID of the widget that we're configuring
	 */
	private int appWidgetId;
	
	/**
	 * The context to be used within onClick handlers
	 */
	private Context self = this;
	
	/**
	 * Setup the configuration activity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d(TAG, "Entered configuration onCreate");
		
		// fetch ID of launched widget so we can decide what to do once the user
		// has interacted with our configuration
		Intent launchIntent = getIntent();
		Bundle extras = launchIntent.getExtras();
		appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		
		Intent cancelResultValue = new Intent();
		cancelResultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		setResult(RESULT_CANCELED, cancelResultValue);
		
		// show the layout
		setContentView(R.layout.configuration);
		
		// setup Save button
		Button save = (Button) findViewById(R.id.saveButton);
		
		save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// get the photo id string from the EditText
				EditText photoInput = (EditText) findViewById(R.id.photoSetIdInput);
				
				// parse the value to an integer, since Flickr set ID's should always be integers
				String setId = photoInput.getText().toString();
				
				Log.d(TAG, "parsed set id is "+setId);
				
				// store under SharedPreferences under widget ID (because we can have multiple copies of the same widget)
				
				SharedPreferences sp = self.getSharedPreferences(PREFS_NAME, 0);
				SharedPreferences.Editor edit = sp.edit();
				
				edit.putString(PREFS_PHOTOSET_PREFIX+appWidgetId, setId);
				edit.commit();
				
				// update the app widget
				DaysWidget.updateAppWidget(self, AppWidgetManager.getInstance(self), appWidgetId, setId);
				
				// build OK result and finish
				Intent okResultValue = new Intent();
				okResultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
				setResult(RESULT_OK, okResultValue);
				finish();
			}
		});
		
		
		// setup Cancel button
		Button cancel = (Button) findViewById(R.id.cancelButton);
		
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// finish sends the intent we setup using setResult earlier
				finish();
			}
		});
		
	}
	
	public static String loadPhotoSetId(Context context, int appWidgetId) {
		
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, appWidgetId);
		//project365 set 72157625668398367
		String setId = prefs.getString(PREFS_PHOTOSET_PREFIX+appWidgetId, "0");
		
		return setId;
	}
}
