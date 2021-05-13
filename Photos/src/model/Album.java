package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Optional;

import util.CalendarUtility;

/**
 * Model Class representing an album. An album has a owner and
 * it contains a set of pictures. Album also has a title.
 *
 * @author Dev Patel and Eric Chan
 */
public class Album implements Serializable {
    private static final long serialVersionUID = 1L;
	private String name;
	private User user;
	private ArrayList<Photo> photos;
	
	/**
	 * Constructor
	 * @param name: name for the album
	 * @param user: Owner for the album
	 */
	public Album(String name, User user) {
		this.name = name;
		this.user = user;
		photos = new ArrayList<>();
	}
	
	/**
	 * Get the list of the photos in the album
	 * @return List of photos in album
	 */
	public ArrayList<Photo> getPhotos() {
		return photos;
	}

	/**
	 * Add a photo to the album
	 * @param p photo to be added
	 */
	public void addPhoto(Photo p) {
		photos.add(p);
	}
	
	/**
	 * get the owner of the album
	 * @return Owner o album
	 */
	public User getUser() {
		return user;
	}
	
	/**
	 * Get title of album
	 * @return Album name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set the title of the album
	 * @param name Name to be set for the album
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * find number of photos in the album
	 * @return number of photos in the album
	 */
	public int getNumPhotos() {
		return photos.size();
	}
	
	/**
	 * Find the earliest date from the photos inside the album
	 * If album is empty, a black string is returned.
	 * @return the earliest date for photo in album
	 */
	public String getEarliestDate() {
		Optional<Photo> p = photos.stream().min((c1, c2) -> {
			return c1.getPhotoDate().compareTo(c2.getPhotoDate());
		});
		if(!p.isPresent()) {
			return "";
		} else {
			return CalendarUtility.calendarToDate(p.get().getPhotoDate());
		}
	}

	/**
	 * Find the latest date from the photos inside the album
	 * If album is empty, a black string is returned.
	 * @return the latest date for photo in album
	 */
	public String getLatestDate() {
		Optional<Photo> p = photos.stream().max((c1, c2) -> {
			return c1.getPhotoDate().compareTo(c2.getPhotoDate());
		});
		if(!p.isPresent()) {
			return "";
		} else {
			return CalendarUtility.calendarToDate(p.get().getPhotoDate());
		}
	}
	
	/**
	 * remove a specific Photo from the album
	 * @param p Photo to be removed
	 */
	public void removePic(Photo p) {
		photos.remove(p);
	}
}
