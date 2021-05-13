package views;

import java.util.Optional;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import model.Album;

/**
 * View class to display all the albums for the currently logged user
 *
 * @author Dev Patel and Eric Chan
 */
public class AlbumsViewController extends BaseController {

	class HyperlinkCell implements Callback<TableColumn<Album, String>, TableCell<Album, String>> {

		@Override
		public TableCell<Album, String> call(TableColumn<Album, String> arg) {
			TableCell<Album, String> cell = new TableCell<Album, String>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					Hyperlink h = new Hyperlink(item);
					setGraphic(h);

					h.setOnAction((e) -> {
						Album currentAlbum = getTableRow().getItem();
						app.changeToAlbumsPhotoPage(currentAlbum);
					});
				}
			};
			return cell;
		}
	}

	@FXML
	private TableView<Album> albumTable;

	@FXML
	private Button addAlbum;

	@FXML
	private Button deleteAlbum;

	@FXML
	private Button renameAlbum;

	@FXML
	private Button searchPhotos;

	@FXML
	private Button manageTags;

	/**
	 * Method which sets up the view for showing all the albums
	 */
	@Override
	public void setup() {
		super.setup();

		@SuppressWarnings("unchecked")
		TableColumn<Album, String> tableColumn1 = (TableColumn<Album, String>) albumTable.getColumns().get(0);
		tableColumn1.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Album, String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(CellDataFeatures<Album, String> cell) {
						return new ReadOnlyObjectWrapper<String>(
								(albumTable.getItems().indexOf(cell.getValue()) + 1) + "");
					}
				});

		@SuppressWarnings("unchecked")
		TableColumn<Album, String> tableColumn2 = (TableColumn<Album, String>) albumTable.getColumns().get(1);
		tableColumn2.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumn2.setCellFactory(new HyperlinkCell());

		albumTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("numPhotos"));
		albumTable.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("earliestDate"));
		albumTable.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("latestDate"));

		// Enable delete and rename Album only on selection of the row.
		albumTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				renameAlbum.setDisable(false);
				deleteAlbum.setDisable(false);
			} else {
				deleteAlbum.setDisable(true);
				renameAlbum.setDisable(true);
			}
		});

		addAlbum.setOnAction((e) -> {
			TextInputDialog addDialog = new TextInputDialog();
			addDialog.setTitle("Add Album");
			addDialog.setHeaderText("Enter album name:");
			addDialog.setContentText("Name:");

			Optional<String> result = addDialog.showAndWait();
			result.ifPresent(name -> {

				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Error");
				if (name.isEmpty()) {
					alert.setContentText("Album Name can not be empty!");
					alert.show();
				} else if (database.findAlbumWithName(app.getCurrentUser(), name) != null) {
					alert.setContentText("An Album already exists with this Name!");
					alert.show();
				} else {
					Album al = new Album(name, app.getCurrentUser());
					database.addAlbum(al);
					System.out.println("Album created.");
					albumTable.getItems().add(al);
				}
			});
		});

		deleteAlbum.setOnAction((e) -> {
			Album currentAlbum = getCurrentlySelectedAlbum();
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Delete Album");
			alert.setHeaderText("you are going to delete Album: " + currentAlbum.getName());
			alert.setContentText("Are you sure?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				database.removeAlbum(currentAlbum);
				albumTable.getItems().remove(currentAlbum);
				System.out.println("Album Deleted.");
			}
		});

		renameAlbum.setOnAction((e) -> {
			Album currentAlbum = getCurrentlySelectedAlbum();
			TextInputDialog addDialog = new TextInputDialog(currentAlbum.getName());
			addDialog.setTitle("Rename Album");
			addDialog.setHeaderText("You are going to rename Album: " + currentAlbum.getName());
			addDialog.setContentText("Enter new name for album:");
			addDialog.setResult(currentAlbum.getName());

			Optional<String> result = addDialog.showAndWait();
			result.ifPresent(name -> {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Error");
				if (name.isEmpty()) {
					alert.setContentText("Album Name can not be empty!");
					alert.show();
				} else if (name.equals(currentAlbum.getName())) {
					// No need to do anything.
				} else {
					if (database.findAlbumWithName(app.getCurrentUser(), name) != null) {
						alert.setContentText("An Album already exists with this Name!");
						alert.show();
					} else {
						currentAlbum.setName(name);
						System.out.println("Album renamed.");
						albumTable.refresh();
					}
				}
			});
		});

		searchPhotos.setOnAction((e) -> {
			app.changeToSearchPage();
		});

		manageTags.setOnAction((e) -> {
			app.changeToManageTagsPage();
		});

		deleteAlbum.setDisable(true);
		renameAlbum.setDisable(true);

		// Add the data for the current user into the album view.
		albumTable.getItems()
				.addAll(FXCollections.observableArrayList(database.getAlbumsForUser(app.getCurrentUser())));

		if (!albumTable.getItems().isEmpty()) {
			albumTable.getSelectionModel().select(0);
		}

	}

	private Album getCurrentlySelectedAlbum() {
		return albumTable.getSelectionModel().getSelectedItem();
	}
}
