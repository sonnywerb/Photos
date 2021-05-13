package views;

import control.Photos;
import database.PhotoAppDb;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;

/**
 * Base controller class which controls some common functions such
 * as showing current username and logout button.
 * @author Dev Patel and Eric Chan
 */
public class BaseController {

	@FXML
	private Label currentUser;

	@FXML
	private Hyperlink logOut;
	
	protected Photos app;
	protected PhotoAppDb database;
	
	/**
	 * Setup the application with database instance and the photo app object
	 * @param app  Photo App object
	 * @param database Photo database object
	 */
	public void initalize(Photos app, PhotoAppDb database) {
		this.app = app;
		this.database = database;
		setup();
	}
	
	/**
	 * This method sets up the control for the view. It can be used to overload
	 * in child classes so that extra functionalities can be added by child classes.
	 * The setup method is called on application start.
	 */
	protected void setup() {
		if(logOut != null) {
			logOut.setOnAction(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent e) {
					app.changeToLoginView();
				}
			});
		}

		if(currentUser != null) {
			currentUser.setText("Welcome " + app.getCurrentUser().getUserName());
		}
	}
}
