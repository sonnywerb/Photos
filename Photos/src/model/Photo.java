package model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import util.CalendarUtility;

/**
 * Model Class representing a photo. A Photo has a caption, url and some set of
 * photo tags along with a photo capture date.
 *
 * @author Dev Patel and Eric Chan
 */
public class Photo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String caption;
	private String filePath;
	private HashMap<Tag, ArrayList<String>> tags;
	private Calendar photoDate;

	/**
	 * Constructor to create a photo with no tags and from a given file path.
	 * 
	 * @param url Path from which photo should be picked
	 */
	public Photo(String url) {
		this.filePath = url;
		this.tags = new HashMap<>();
		this.photoDate = Calendar.getInstance();
		photoDate.setTimeInMillis(new File(url).lastModified());
		this.caption = "Created on " + CalendarUtility.calendarToDate(photoDate);
	}

	/**
	 * Create a photo from other photo. the tags are also copied for the photo along
	 * with path and caption
	 * 
	 * @param other Photo to create a copy from
	 */
	public Photo(Photo other) {
		this.filePath = other.filePath;
		this.tags = new HashMap<>();
		for (Tag t : other.tags.keySet()) {
			tags.put(t, new ArrayList<>());
			for (String v : other.tags.get(t)) {
				tags.get(t).add(v);
			}
		}
		this.photoDate = Calendar.getInstance();
		photoDate.setTimeInMillis(new File(filePath).lastModified());
		this.caption = other.caption;
	}

	/**
	 * Get photo caption
	 * 
	 * @return photo caption
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * Set the caption for the photo
	 * 
	 * @param caption Caption to be set on photo
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}

	/**
	 * Get image file path on the disk
	 * 
	 * @return image file path on the disk
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * get the tags and their values associated on the photo
	 * It returns a list of tag values for each possible tag, all wrapped
	 * in a hashmap.
	 * @return Map with tags and their values
	 */
	public HashMap<Tag, ArrayList<String>> getTags() {
		return tags;
	}
	
	/**
	 * get the tag values for a given tag from the photo
	 * @param t Tag for which values needs to be retrieved
	 * @return List of tag values
	 */
	public ArrayList<String> getTagValues(Tag t) {
		return tags.getOrDefault(t, new ArrayList<>());
	}

	/**
	 * Add a new tag value to the photo. if tag is single value tag
	 * then the older value is discarded.
	 * @param tag Tag to add value for
	 * @param tagValue Tag value
	 */
	public void addTag(Tag tag, String tagValue) {
		if (!tags.containsKey(tag)) {
			tags.put(tag, new ArrayList<>());
		}
		if (tag.isSingleValueTag()) {
			tags.get(tag).clear();
		}
		tags.get(tag).add(tagValue);
	}

	/**
	 * Get the photo capture date
	 * @return Photo date as calendar object
	 */
	public Calendar getPhotoDate() {
		return photoDate;
	}	
	
	/**
	 * Remove the tag with specific value from the photo.
	 * @param t Tag object to be removed
	 * @param value Specific tag value to be removed
	 */
	public void removeTag(Tag t, String value) {
		if (t.isSingleValueTag()) {
			tags.remove(t);
		} else {
			ArrayList<String> vals = tags.get(t);
			vals.remove(value);
			if (vals.isEmpty()) {
				tags.remove(t);
			}
		}
	}
}
