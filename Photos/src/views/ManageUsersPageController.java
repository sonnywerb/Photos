package views;

import java.util.Optional;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Pair;
import model.User;
import model.UserRole;

/**
 * Class controlling the display of users on manage Users page for admin page.
 * @author Dev Patel and Eric Chan
 */
public class ManageUsersPageController extends BaseController {

	@FXML
	private TableView<User> usersTable;

	@FXML
	private Button createUser;

	@FXML
	private Button deleteUser;

	@Override
	public void setup() {
		super.setup();

		@SuppressWarnings("unchecked")
		TableColumn<User, String> tableColumn1 = (TableColumn<User, String>) usersTable.getColumns().get(0);
		tableColumn1.setSortable(false);
		tableColumn1.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<User, String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(CellDataFeatures<User, String> cell) {
						return new ReadOnlyObjectWrapper<String>(
								(usersTable.getItems().indexOf(cell.getValue()) + 1) + "");
					}
				});
		
		usersTable.getColumns().get(1).setSortable(false);
		usersTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("userName"));
		usersTable.getColumns().get(2).setSortable(false);
		usersTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("userRole"));
		usersTable.setPlaceholder(new Label("No Users Available"));

		createUser.setOnAction((e) -> {
			Dialog<Pair<String, String>> dialog = new Dialog<>();
			dialog.setTitle("Manage Users");
			dialog.setHeaderText("Add User");
			DialogPane dialogPane = dialog.getDialogPane();
			dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

			TextField textField = new TextField();
			textField.setPromptText("User Name");

			ObservableList<String> options = FXCollections.observableArrayList("ADMIN", "USER");
			ComboBox<String> comboBox = new ComboBox<>(options);
			comboBox.getSelectionModel().selectFirst();
			dialogPane.setContent(new VBox(8, comboBox, textField));

			Platform.runLater(comboBox::requestFocus);
			dialog.setResultConverter((ButtonType button) -> {
				if (button == ButtonType.OK) {
					return new Pair<String, String>(textField.getText(), comboBox.getValue());
				}
				return null;
			});

			Optional<Pair<String, String>> optionalResult = dialog.showAndWait();
			optionalResult.ifPresent((Pair<String, String> results) -> {
				String tagValue = results.getValue().trim();
				String username = results.getKey().trim();

				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Error");
				if (username.isEmpty()) {
					alert.setContentText("Username can not be empty!");
					alert.show();
				} else if (database.findUser(username) != null) {
					alert.setContentText("A user already exists with this username!");
					alert.show();
				} else {
					database.addUser(username, UserRole.valueOf(tagValue));
					refreshData();
				}
			});
		});

		deleteUser.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				User u = getCurrentlySelectedUser();
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Delete User");
				alert.setHeaderText("You are going to delete: " + u.getUserName());
				alert.setContentText("Are you sure?");

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK) {
					database.removeUser(u);
					refreshData();
				}
			}
		});

		///////////////////////////////////
		refreshData();
	}

	private User getCurrentlySelectedUser() {
		return usersTable.getSelectionModel().getSelectedItem();
	}
	
	// Refresh the view based on the users retrieved from the database
	private void refreshData() {
		usersTable.getItems().clear();
		usersTable.getItems()
				.addAll(FXCollections.observableArrayList(database.getUsersExceptCurrent(app.getCurrentUser())));
		usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				deleteUser.setDisable(false);
			} else {
				deleteUser.setDisable(true);
			}
		});

		deleteUser.setDisable(true);
		if (!usersTable.getItems().isEmpty()) {
			usersTable.getSelectionModel().select(0);
		}
	}

}
