package control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import database.PhotoAppDb;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Album;
import model.Photo;
import model.User;
import model.UserRole;
import views.AlbumsPhotoPageController;
import views.AlbumsViewController;
import views.BaseController;
import views.LoginPageController;
import views.ManageUsersPageController;
import views.PhotoAreaController;
import views.SearchPageController;

/**
 * This is the main class containing method to run the photo
 * application.
 * @author Dev Patel and Eric Chan
 */
public class Photos extends Application {
	
	/**
	 * Path of the serialized database file created in project directory
	 */
	public static final String DB_FILE = "data/database.ser";
	
	// Database object for the app.
	private PhotoAppDb database;
	
	// User currently logged into the application if any
	private User currentUser;

	// Currently opened album if any
	private Album currentAlbum;

	// Currently opened photo if any
	private Photo currentPhoto;
	
	// List of the photos move/copy queries
	private ArrayList<PhotoShift> photoShiftQueries;
	
	// Keeping a stage object, So that scene can be set for various pages
	private Stage primaryStage;

	/**
	 * Inner class to manage the copy and paste of photos inside the application
	 * @author Dev Patel and Eric Chan
	 */
	private class PhotoShift {
		Photo photo;
		boolean move; // true if it is a move operation, false if copy operation.
		Album parentAlbum;

		public PhotoShift(Photo photo, boolean move, Album parentAlbum) {
			this.photo = photo;
			this.move = move;
			this.parentAlbum = parentAlbum;
		}
	}

	/**
	 * method to setup the application.
	 * It loads the database from the serialized file if it exists, or else
	 * sets up the database with admin and stock users.
	 */
	public Photos() {
		FileInputStream streamIn;
		try {
			streamIn = new FileInputStream(DB_FILE);
			ObjectInputStream ois = new ObjectInputStream(streamIn);
			database = (PhotoAppDb) ois.readObject();
			ois.close();
		} catch (FileNotFoundException e) {
			// If the database file is not found, then we need to setup initial Data in the
			// database
			System.out.println("No old database found.");
			initializeDatabase();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		photoShiftQueries = new ArrayList<>();
	}

	// Utility method to create the initial database
	// Setting up Admin and Stock account.
	private void initializeDatabase() {
		
		System.out.println("Setting up fresh database");
		
		database = new PhotoAppDb();
		database.addUser("admin", UserRole.ADMIN);
		database.addTagType("location", true);
		database.addTagType("persons", false);

		database.addUser("stock", UserRole.USER);
		Album a = new Album("stock", database.findUser("stock"));
		database.addAlbum(a);
		
		a.addPhoto(new Photo(new File("data/facebook.png").getPath()));
		a.addPhoto(new Photo(new File("data/saturn.png").getPath()));
		a.addPhoto(new Photo(new File("data/tumblr.png").getPath()));
		a.addPhoto(new Photo(new File("data/twitter.png").getPath()));
		a.addPhoto(new Photo(new File("data/ufo.png").getPath()));
		a.addPhoto(new Photo(new File("data/youtube.png").getPath()));
	}

	// utility method to save database state on app close
	private void saveDatabase() {
		System.out.println("Saving database state.");
		// Save database to serialized file.
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(DB_FILE));
			oos.writeObject(database);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * this method sets up the scene for the app window
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		primaryStage.setTitle("Photo Album App");
		primaryStage.show();

		// Method which gets called when application is closed.
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				saveDatabase();
				Platform.exit();
				System.exit(0);
			}
		});
		
		// start on the login view.
		changeToLoginView();
	}
	
	// Helper method to load a particular scene with given view
	private <T extends BaseController> void loadFxml(String fxmlPath) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(fxmlPath));
			AnchorPane root = (AnchorPane) loader.load();
			T controller = loader.getController();
			controller.initalize(this, database);
			primaryStage.setScene(new Scene(root));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method changes the window scene to bring the login page
	 */
	public void changeToLoginView() {
		currentUser = null;
		saveDatabase();
		this.<LoginPageController>loadFxml("/views/fxml/LoginPage.fxml");
	}

	/**
	 * This method changes the window scene to bring the album views
	 * @param user: The user for which albums needs to be displayed
	 */
	public void changeToAlbumView(User user) {
		currentUser = user;
		currentAlbum = null;
		currentPhoto = null;
		this.<AlbumsViewController>loadFxml("/views/fxml/AlbumsPage.fxml");
	}

	/**
	 * This method changes the window scene to show the photos of
	 * particular album
	 * @param album: The album for which photos needs to be displayed
	 */
	public void changeToAlbumsPhotoPage(Album album) {
		currentAlbum = album;
		currentPhoto = null;
		this.<AlbumsPhotoPageController>loadFxml("/views/fxml/AlbumsPhotoPage.fxml");
	}

	/**
	 * This method changes the window scene to show a particular
	 * photo in separate window.
	 * @param pic: The picture which needs to be loaded on photo area
	 */
	public void changeToPhotoAreaView(Photo pic) {
		currentPhoto = pic;
		this.<PhotoAreaController>loadFxml("/views/fxml/PhotoArea.fxml");
	}

	/**
	 * This method changes the window scene to search for photos
	 */
	public void changeToSearchPage() {
		currentPhoto = null;
		currentAlbum = null;
		this.<SearchPageController>loadFxml("/views/fxml/SearchPage.fxml");
	}

	/**
	 * This method changes the window scene to manage the users for an admin
	 * @param u: The user who is currently logged in
	 */
	public void changeToManageUsersPage(User u) {
		currentUser = u;
		currentPhoto = null;
		currentAlbum = null;
		this.<ManageUsersPageController>loadFxml("/views/fxml/ManageUsers.fxml");
	}

	/**
	 * This method changes the window scene to manage the tags for a user.
	 */
	public void changeToManageTagsPage() {
		currentPhoto = null;
		currentAlbum = null;
		this.<ManageUsersPageController>loadFxml("/views/fxml/ManageTags.fxml");
	}
	
	////////////////////////////////////////////////////////////////////////
	// PhotoShift operations.
	
	/**
	 * Method to get the queries for photo move/copy
	 * @return the list of queries.
	 */
	public ArrayList<PhotoShift> getPhotoShiftQueries() {
		return photoShiftQueries;
	}

	/**
	 * Method to initiate a copy operation on the picture.
	 * @param p Photo which needs to be copied.
	 */
	public void copyPhoto(Photo p) {
		photoShiftQueries.add(new PhotoShift(p, false, currentAlbum));
	}

	/**
	 * Method to initiate a copy operation on the picture.
	 * @param p Photo which needs to be moved.
	 */
	public void movePhoto(Photo p) {
		photoShiftQueries.add(new PhotoShift(p, true, currentAlbum));
	}

	/**
	 * Method to perform the photo copy/paste operation. It will paste/move 
	 * the photos which are initiated by copyPhoto or movePhoto methods
	 */
	public void pastePhotos() {
		for (PhotoShift ps : photoShiftQueries) {
			if (ps.move) {
				ps.parentAlbum.removePic(ps.photo);
				currentAlbum.addPhoto(ps.photo);
			} else {
				currentAlbum.addPhoto(new Photo(ps.photo));
			}
		}
		photoShiftQueries.clear();
	}

	////////////////////////////////////////////////////////////////////////
	// Utility Methods for Controllers
	
	/**
	 * Get the currently logged in user
	 * @return User if logged in else null
	 */
	public User getCurrentUser() {
		return currentUser;
	}

	/**
	 * Get the currently opened album if any
	 * @return Album if some album is opened else null
	 */
	public Album getCurrentAlbum() {
		return currentAlbum;
	}

	/**
	 * Get the currently opened photo if any
	 * @return photo if some photo is opened else null
	 */
	public Photo getCurrentPic() {
		return currentPhoto;
	}
	
	/**
	 * Get the stage for the FX application window, so that alerts
	 * can be created on it.
	 * @return Window stage.
	 */
	public Stage getStage() {
		return primaryStage;
	}

	/**
	 * Main method to start the application
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
