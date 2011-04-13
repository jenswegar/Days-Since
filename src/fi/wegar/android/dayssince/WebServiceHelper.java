package fi.wegar.android.dayssince;

import java.io.IOException;

import org.xml.sax.SAXException;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photosets.Photoset;
import com.aetrion.flickr.photosets.PhotosetsInterface;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class WebServiceHelper {

	private static final String TAG = "DaysSince.WebServiceHelper";
	
	private static Flickr mFlickr;
	
	public static int updatePhotoCount(String photoSetId) {
		Log.d(TAG, "updatePhotoCount started");
		
		if(mFlickr == null) {
			mFlickr = new Flickr("86f964e77865436b1459923d05d11798");
		}
		
		PhotosetsInterface mPhotoSet = mFlickr.getPhotosetsInterface();
		Photoset mPhotoInfo = null;
		
		try {
			mPhotoInfo = mPhotoSet.getInfo(photoSetId);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage() );
		} catch (SAXException e) {
			Log.e(TAG, e.getMessage() );
		} catch (FlickrException e) {
			Log.e(TAG, e.getErrorMessage() );
		}
		
		if(mPhotoInfo != null) {
			Log.d(TAG, "Photo list fetched");
			Log.d(TAG, "Number of photos = "+mPhotoInfo.getPhotoCount() );
			return mPhotoInfo.getPhotoCount();
		} else {
			return 0;
		}
	}
}
