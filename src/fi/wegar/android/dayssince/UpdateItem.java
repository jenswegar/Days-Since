package fi.wegar.android.dayssince;

/**
 * Represents an object in the UpdateService processing queue
 * @author jenswegar
 *
 */
public class UpdateItem {

	
	/**
	 * The ID of the appWidget to which this UpdateItem is linked
	 */
	public int appWidgetId;
	
	/**
	 * The Flickr photoset ID used to update the referenced appWidget
	 */
	public String photoSetId;
	
	/**
	 * Constructor for creating a new UpdateItem
	 * @param appWidgetId
	 */
	public UpdateItem(int appWidgetId, String photoSetId) {
		this.appWidgetId = appWidgetId;
		this.photoSetId = photoSetId;
	}
	
}
