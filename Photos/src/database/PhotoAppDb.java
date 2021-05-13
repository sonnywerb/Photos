package database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import model.Album;
import model.Photo;
import model.Tag;
import model.User;
import model.UserRole;

/**
 * This class simulates a real word database connectivity
 * As We do not have a real database, so this will act as a database using which we
 * can update / store our data. It basically is a in memory database.
 *
 * @author Dev Patel and Eric Chan
 */
public class PhotoAppDb implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // list of albums in the application
	private List<Album> albums;
	
    // list of users in the application
	private List<User> users;
	
    // list of possible tags in the application
	private List<Tag> possibleTagTypes;
	
    // path of directory where user searched for the photos last time
	private String lastSearchedDir;
	
	/**
	 * Constructor to set up the database members
	 */
	public PhotoAppDb() {
		albums = new ArrayList<>();
		users = new ArrayList<>();
		possibleTagTypes = new ArrayList<>();
		lastSearchedDir = System.getProperty("user.home");
	}

	/**
	 * Method to add a user to database with a given role
	 * @param username Username for user to be added
	 * @param role  (admin/user)
	 */
	public void addUser(String username, UserRole role) {
		users.add(new User(username, role));
	}
	
	/**
	 * Method to create a new tag type. These are general tags and not owned by a specific user
	 * @param tagKey Name for the tag
	 * @param singleValueTag: whether the tag can contain only one value or multiple
	 */
	public void addTagType(String tagKey, boolean singleValueTag) {
		possibleTagTypes.add(new Tag(tagKey, singleValueTag));
	}

	/**
	 * Method to create a new tag type owned by a specific user
	 * @param tagKey Name for the tag
	 * @param singleValueTag: whether the tag can contain only one value or multiple
	 * @param u: owner of the tag type.
	 */
	public void addTagType(String tagKey, boolean singleValueTag, User u) {
		possibleTagTypes.add(new Tag(tagKey, singleValueTag, u));
	}
	
	/**
	 * Method to add a new album to database.
	 * @param album Album to be added
	 */
	public void addAlbum(Album album) {
		albums.add(album);
	}
	
	/**
	 * Method to find a user with given username
	 * @param username username for the user to be found
	 * @return user object if found else null
	 */
	public User findUser(String username) {
		for(User u: users) {
			if(u.getUserName().equals(username)) {
				return u;
			}
		}
		return null;
	}

	/**
	 * Return the list of albums for given user
	 * @param currentUser User object for currently logged user 
	 * @return List of albums, empty list if no albums for the user
	 */
	public ArrayList<Album> getAlbumsForUser(User currentUser) {
		ArrayList<Album> results = new ArrayList<>();
		for(Album a: albums) {
			if(a.getUser().equals(currentUser)) {
				results.add(a);
			}
		}
		return results;
	}

	/**
	 * Method to find the album onwed by a specific user and having the specific name
	 * @param currentUser User object for currently logged user 
	 * @param albumName Album name to find 
	 * @return album object, null if no album is found with given criteria
	 */
	public Album findAlbumWithName(User currentUser, String albumName) {
		for(Album a: albums) {
			if(a.getUser().equals(currentUser) && a.getName().equals(albumName)) {
				return a;
			}
		}
		return null;
	}

	/**
	 * Method to delete a album from the database
	 * @param currentAlbum Album object to be removed
	 */
	public void removeAlbum(Album currentAlbum) {
		albums.remove(currentAlbum);
	}
	
	/**
	 * Method to find the tag object with tag name and owner of the tag
	 * @param tagKey Name of tag
	 * @param u User who owns the tag
	 * @return tag object, null if nothing found
	 */
	public Tag findTagWithName(String tagKey, User u) {
		for(Tag t: possibleTagTypes) {
			if(t.getTagKey().equals(tagKey) && (t.getOwner() == null || t.getOwner() == u)) {
				return t;
			}
		}
		return null;
	}

	/**
	 * Get all the tag names which are possible for a given user. it contains both user owned
	 * as well as general tags like location and persosn.
	 * @param u User whose tags need to be found
	 * @return List of tag names
	 */
	public List<String> getAllTags(User u) {
		return  possibleTagTypes.stream().filter(t -> t.getOwner() == null || t.getOwner() == u).map(x -> x.getTagKey()).collect(Collectors.toList());
	}

	/**
	 * Get all the tag names which are owned by a given user. it does not contain general 
	 * tags like location and persosn.
	 * @param u User whose tags need to be found
	 * @return List of tag names
	 */
	public List<Tag> getUserOwnerTags(User u) {
		return  possibleTagTypes.stream().filter(t -> t.getOwner() == u).collect(Collectors.toList());
	}

	/**
	 * Get the last directory where user searched for photos. For the first time, it defaults to 
	 * user home directory,
	 * @return the path of directory
	 */
	public String getLastSearchedDir() {
		return lastSearchedDir;
	}

	/**
	 * Set the directory path where user searched at last for adding the pics
	 * @param lastSearchedDir Directory path to be set
	 */
	public void setLastSearchedDir(String lastSearchedDir) {
		this.lastSearchedDir = lastSearchedDir;
	}

	/**
	 * Get the index of the pic in the parent album. Each album contains multiple
	 * pictures.
	 * @param album Album in which photo needs to be searched
	 * @param currentPic photo which needs to be searched
	 * @return The pic index
	 */
	public int getPicIndexInAlbum(Album album, Photo currentPic) {
		return album.getPhotos().indexOf(currentPic);
	}
	
	/**
	 * Get the list of photos owned by the given user
	 * @param currentUser User object for currently logged user 
	 * @return Empty list if user has no photos, else list of photos
	 */
	public ArrayList<Photo> getPhotosForUser(User currentUser) {
		ArrayList<Photo> results = new ArrayList<>();
		for(Album a: albums) {
			if(a.getUser().equals(currentUser)) {
				results.addAll(a.getPhotos());
			}
		}
		return results;
	}

	/**
	 * Get the list of users existing in the system except the current user.
	 * @param currentUser User object for currently logged user 
	 * @return list of users
	 */
	public List<User> getUsersExceptCurrent(User currentUser) {
		return users.stream().filter(x -> x != currentUser).collect(Collectors.toList());
	}

	/**
	 * Method to remove a specific user from the system. On removing the user, 
	 * its albums and owned tags are removed as well
	 * @param u User object to remove
	 */
	public void removeUser(User u) {
		users.remove(u);
		albums = albums.stream().filter(x -> x.getUser() != u).collect(Collectors.toList());
		possibleTagTypes = possibleTagTypes.stream().filter(t -> t.getOwner() != u).collect(Collectors.toList());
	}

	/**
	 * Method to remove the tag. It also removes the tag from the photos if photos has that tag
	 * @param t Tag object to be removed.
	 */
	public void removeTag(Tag t) {
		albums.forEach(a -> {
			a.getPhotos().forEach(p -> {
				p.getTags().remove(t);
			});
		});
		possibleTagTypes.remove(t);
	}
	
}
