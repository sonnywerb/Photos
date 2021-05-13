package views;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.Pair;
import model.Album;
import model.Photo;
import model.Tag;

/**
 * View class to control the actions on the album photos page.
 *
 * @author Dev Patel and Eric Chan
 */
public class AlbumsPhotoPageController extends BaseController {

	@FXML
	private TableView<Pair<String, String>> tagsTable;

	@FXML
	private Label picCaption;

	@FXML
	private Button nextPic;

	@FXML
	private Button prevPic;

	@FXML
	private Button addPic;

	@FXML
	private Button copyPic;

	@FXML
	private Button movePic;

	@FXML
	private Button pastePic;

	@FXML
	private Button deletePic;

	@FXML
	private Button addTag;

	@FXML
	private Button deleteTag;

	@FXML
	private Button openInNewTab;

	@FXML
	private Button changeCaption;

	@FXML
	private ListView<Photo> photosList;

	@FXML
	private Hyperlink albumName;

	@FXML
	private ScrollPane detailsPane;
	
	// current album for which photos are displayed
	private Album currentAlbum;
	
	/**
	 * Method which sets up the view for albums photos
	 */
	@Override
	public void setup() {
		super.setup();
		currentAlbum = app.getCurrentAlbum();
		tagsTable.getColumns().get(0).setSortable(false);
		tagsTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("key"));
		tagsTable.getColumns().get(1).setSortable(false);
		tagsTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("value"));
		tagsTable.setPlaceholder(new Label("No Tags for photo."));

		photosList.setCellFactory(param -> new ListCell<Photo>() {

			private ImageView imageView = new ImageView();

			@Override
			public void updateItem(Photo p, boolean empty) {
				super.updateItem(p, empty);
				if (empty) {
					setText(null);
					setGraphic(null);
					return;
				}

				Image i = new Image(new File(p.getFilePath()).toURI().toString(), 90, 90, false, false);
				imageView.setImage(i);
				imageView.setStyle("-fx-padding: 6;");
				setGraphic(imageView);
			}
		});

		addPic.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Select Picture");
				fileChooser.setInitialDirectory(new File(database.getLastSearchedDir()));
				File selectedFile = fileChooser.showOpenDialog(app.getStage());
				if (selectedFile != null) {
					Photo p = new Photo(selectedFile.toURI().getPath());
					currentAlbum.addPhoto(p);
					photosList.getItems().add(p);
					database.setLastSearchedDir(selectedFile.getParent());

					if (photosList.getSelectionModel().getSelectedItem() == null) {
						photosList.getSelectionModel().select(0);
					}
				}
			}
		});

		pastePic.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Pasting Photo");
				alert.setHeaderText("Your photo is ready to be pasted.");
				alert.setContentText("Are you sure?");

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK) {
					app.pastePhotos();
					refreshData();
				}
			}
		});

		copyPic.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// Only one photo should be copied
				app.getPhotoShiftQueries().clear();
				app.copyPhoto(getCurrentlySelectedPic());

				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Copy Photo");
				alert.setHeaderText("Your photo has been copied.");
				alert.setContentText("you can paste it into another album by using paste button");
				alert.showAndWait();
				
				pastePic.setDisable(false);
			}
		});

		movePic.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// Only one photo should be copied
				app.getPhotoShiftQueries().clear();
				app.movePhoto(getCurrentlySelectedPic());

				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Move Photo");
				alert.setHeaderText("Your photo is ready to be moved.");
				alert.setContentText("you can paste it into another album by using paste button");
				alert.showAndWait();
				
				pastePic.setDisable(false);
			}
		});

		deletePic.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Delete Photo");
				alert.setHeaderText("you are going to delete Photo from album: " + currentAlbum.getName());
				alert.setContentText("Are you sure?");

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK) {
					Photo p = getCurrentlySelectedPic();
					currentAlbum.removePic(p);
					photosList.getItems().remove(p);
				}
			}
		});

		addTag.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				Dialog<Pair<String, String>> dialog = new Dialog<>();
				dialog.setTitle("Photo tags");
				dialog.setHeaderText("Add Tags");
				DialogPane dialogPane = dialog.getDialogPane();
				dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

				TextField textField = new TextField();
				textField.setPromptText("Tag Value");

				ObservableList<String> options = FXCollections.observableArrayList(database.getAllTags(app.getCurrentUser()));
				ComboBox<String> comboBox = new ComboBox<>(options);
				comboBox.getSelectionModel().selectFirst();
				dialogPane.setContent(new VBox(8, comboBox, textField));

				Platform.runLater(comboBox::requestFocus);
				dialog.setResultConverter((ButtonType button) -> {
					if (button == ButtonType.OK) {
						return new Pair<String, String>(comboBox.getValue(), textField.getText());
					}
					return null;
				});

				Optional<Pair<String, String>> optionalResult = dialog.showAndWait();
				optionalResult.ifPresent((Pair<String, String> results) -> {
					String tagValue = results.getValue().trim();
					if (!tagValue.isBlank()) {
						Tag tag = database.findTagWithName(results.getKey(), app.getCurrentUser());
						getCurrentlySelectedPic().addTag(tag, tagValue);
						populateTagsAsPerCurrentPic();
					}
				});
			}
		});

		openInNewTab.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				app.changeToPhotoAreaView(getCurrentlySelectedPic());
			}
		});

		albumName.setText(currentAlbum.getName());
		albumName.setOnAction((e) -> {
			app.changeToAlbumView(app.getCurrentUser());
		});

		changeCaption.setOnAction((e) -> {
			TextInputDialog addDialog = new TextInputDialog(picCaption.getText());
			addDialog.setTitle("Change caption");
			addDialog.setHeaderText("Photo Caption Change");
			addDialog.setContentText("Enter new caption:");

			Optional<String> result = addDialog.showAndWait();
			result.ifPresent(caption -> {
				getCurrentlySelectedPic().setCaption(caption);
				picCaption.setText(caption);
			});
		});
		
		Runnable removeCurrentTag = () -> {
			Pair<String, String> row = tagsTable.getSelectionModel().getSelectedItem();
			Tag t = database.findTagWithName(row.getKey(), app.getCurrentUser());
			getCurrentlySelectedPic().removeTag(t, row.getValue());
			populateTagsAsPerCurrentPic();
		};

		tagsTable.setRowFactory(new Callback<TableView<Pair<String, String>>, TableRow<Pair<String, String>>>() {

			@Override
			public TableRow<Pair<String, String>> call(TableView<Pair<String, String>> arg0) {
				final TableRow<Pair<String, String>> row = new TableRow<>();
				final ContextMenu rowMenu = new ContextMenu();

				MenuItem removeItem = new MenuItem("Delete Tag");
				removeItem.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						removeCurrentTag.run();
					}
				});

				rowMenu.getItems().addAll(removeItem);

				// only display context menu for non-empty rows:
				row.contextMenuProperty()
						.bind(Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(rowMenu));
				return row;
			}
		});
		
		deleteTag.setOnAction((e) -> removeCurrentTag.run());

		nextPic.setOnAction((e) -> {
			int selectedIndex = photosList.getSelectionModel().getSelectedIndex();
			if (selectedIndex < photosList.getItems().size() - 1) {
				photosList.getSelectionModel().select(selectedIndex + 1);
				photosList.scrollTo(selectedIndex + 1);
			}
		});
		prevPic.setOnAction((e) -> {
			int selectedIndex = photosList.getSelectionModel().getSelectedIndex();
			if (selectedIndex > 0) {
				photosList.getSelectionModel().select(selectedIndex - 1);
				photosList.scrollTo(selectedIndex - 1);
			}
		});

		///////////////////////////////////
		refreshData();
	}
	
	// Get the photo which is selected on the listview.
	private Photo getCurrentlySelectedPic() {
		return photosList.getSelectionModel().getSelectedItem();
	}

	// Local routine to populate the tags on the table.
	private void populateTagsAsPerCurrentPic() {
		tagsTable.getItems().clear();
		HashMap<Tag, ArrayList<String>> tags = getCurrentlySelectedPic().getTags();
		for (Tag tag : tags.keySet()) {
			for (String value : tags.get(tag)) {
				tagsTable.getItems().add(new Pair<String, String>(tag.getTagKey(), value));
			}
		}
		if(!tagsTable.getItems().isEmpty()) {
			tagsTable.getSelectionModel().select(0);
			deleteTag.setDisable(false);
		} else {
			deleteTag.setDisable(true);
		}
	};
	
	// method which refreshes the data on the whole view based on the photos retrieved
	// from database for the current album
	private void refreshData() {
		photosList.getItems().clear();
		photosList.getItems().addAll(FXCollections.observableArrayList(currentAlbum.getPhotos()));
		photosList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				deletePic.setDisable(false);
				copyPic.setDisable(false);
				addTag.setDisable(false);
				movePic.setDisable(false);
				openInNewTab.setDisable(false);
				detailsPane.setVisible(true);
				picCaption.setText(newSelection.getCaption());
				populateTagsAsPerCurrentPic();
			} else {
				deletePic.setDisable(true);
				copyPic.setDisable(true);
				addTag.setDisable(true);
				movePic.setDisable(true);
				openInNewTab.setDisable(true);
				detailsPane.setVisible(false);
			}
		});
		
		if (!app.getPhotoShiftQueries().isEmpty()) {
			pastePic.setDisable(false);
		} else {
			pastePic.setDisable(true);
		}

		deletePic.setDisable(true);
		copyPic.setDisable(true);
		addTag.setDisable(true);
		movePic.setDisable(true);
		openInNewTab.setDisable(true);
		detailsPane.setVisible(false);
		if (!photosList.getItems().isEmpty()) {
			photosList.getSelectionModel().select(0);
		}
	}

}
