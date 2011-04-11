package fi.wegar.android.dayssince;

import java.net.URL;

import android.os.AsyncTask;

public class GetPhotoListTask extends AsyncTask<URL, Void, Integer> {

	@Override
	protected Integer doInBackground(URL... params) {
		
		return new Integer(56);
	}

	protected void onPostExecute(Integer numPhotos) {
		
	}
}
