package views;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import model.User;
import model.UserRole;

/**
 * Class which controls the actions on login page and represent the view.
 * @author Dev Patel and Eric Chan
 */
public class LoginPageController extends BaseController {

	@FXML
	private TextField username;

	@FXML
	private Button submitBtn;

	@FXML
	private Label errorMsg;

	@Override
	public void setup() {
		super.setup();
		
		Runnable performLogin = () -> {
			User u = database.findUser(username.getText());
			if (u != null) {
				errorMsg.setText("");
				if(u.getUserRole() == UserRole.ADMIN) {
					app.changeToManageUsersPage(u);
				} else {
					app.changeToAlbumView(u);
				}
			} else {
				errorMsg.setText("Invalid username.");
			}
		};

		username.setOnKeyPressed((e) -> {
	        if (e.getCode() == KeyCode.ENTER)  {
	        	performLogin.run();
	        }
		});
		submitBtn.setOnAction((e) -> performLogin.run());
	}
}