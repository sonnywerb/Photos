package views;

import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;
import model.Album;
import model.Photo;
import model.Tag;

/**
 * Class which displays a search page. It searches the pics with specific criteria
 * owned by the currently logged user
 * @author Dev Patel and Eric Chan
 */
public class SearchPageController extends BaseController {

	@FXML
	private TableView<Pair<String, String>> tagsTable;

	@FXML
	private Label picCaption;

	@FXML
	private Button nextPic;

	@FXML
	private Button prevPic;

	@FXML
	private Button createAlbumWithSelection;

	@FXML
	private Button searchButton;

	@FXML
	private TextField searchCritera;

	@FXML
	private DatePicker fromDate;

	@FXML
	private DatePicker toDate;

	@FXML
	private ListView<Photo> photosList;

	@FXML
	private ScrollPane detailsPane;

	@FXML
	private ComboBox<String> tag1;
	
	@FXML
	private ComboBox<String> tag2;
	
	@FXML
	private ComboBox<String> operator;
	
	@FXML
	private TextField tagValue1;
	
	@FXML
	private TextField tagValue2;
	
	@FXML
	private Hyperlink albums;
	
	private List<Photo> searchResults;
	
	private EventHandler<ActionEvent> handler;

	@Override
	public void setup() {
		super.setup();
		tagsTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("key"));
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

		int index = photosList.getSelectionModel().getSelectedIndex();

		if (index <  photosList.getItems().size() - 1) {
			nextPic.setOnAction((e) -> {
				photosList.getSelectionModel().select((index + 1));
			});
		} else {
			nextPic.setDisable(true);
		}

		if (index > 0) {
			prevPic.setOnAction((e) -> {
				photosList.getSelectionModel().select((index - 1));
			});
		} else {
			prevPic.setDisable(true);
		}
		
		photosList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				detailsPane.setVisible(true);
				picCaption.setText(newSelection.getCaption());
				populateTagsAsPerCurrentPic();
			} else {
				detailsPane.setVisible(false);
			}
		});

		tag1.getItems().addAll(database.getAllTags(app.getCurrentUser()));
		tag2.getItems().addAll(database.getAllTags(app.getCurrentUser()));
		operator.getItems().addAll(Arrays.asList("AND", "OR"));
		
		tag1.getSelectionModel().select(0);
		tag2.getSelectionModel().select(0);
		operator.getSelectionModel().select(0);
		
		detailsPane.setVisible(false);
		detailsPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		detailsPane.setVbarPolicy(ScrollBarPolicy.NEVER);
		
		searchButton.setOnAction((e) -> {
			LocalDate end = toDate.getValue();
			if(toDate.getEditor().getText().isBlank()) {
				end = null;
			}
			LocalDate start = fromDate.getValue();
			if(fromDate.getEditor().getText().isBlank()) {
				start = null;
			}
			LocalDate startDate = start;
			LocalDate endDate = end;

			String op = operator.getSelectionModel().getSelectedItem();
			Tag t1 = database.findTagWithName(tag1.getSelectionModel().getSelectedItem(), app.getCurrentUser());
			Tag t2 = database.findTagWithName(tag2.getSelectionModel().getSelectedItem(), app.getCurrentUser());
			String t1SearchVal = tagValue1.getText().trim();
			String t2SearchVal = tagValue2.getText().trim();
			
			ArrayList<Photo> userPhotos = database.getPhotosForUser(app.getCurrentUser());
			searchResults = userPhotos.stream().filter(p -> {
				long picMillis = p.getPhotoDate().getTimeInMillis();
				LocalDate picDate = Instant.ofEpochMilli(picMillis).atZone(ZoneId.systemDefault()).toLocalDate();

				if(startDate != null && startDate.isAfter(picDate)) {
					return false;
				}
				if(endDate != null && endDate.isBefore(picDate)) {
					return false;
				}
				if(t1SearchVal.isBlank() && t2SearchVal.isBlank()) {
					return true;
				}
				if(t1SearchVal.isBlank()) {
					// Only t2 needs to be applied.
					return p.getTagValues(t2).contains(t2SearchVal);
				}
				if(t2SearchVal.isBlank()) {
					// Only t1 needs to be applied.
					return p.getTagValues(t1).contains(t1SearchVal);
				}
				
				// Both tags are present.
				if(op.equals("AND")) {
					return p.getTagValues(t1).contains(t1SearchVal) && p.getTagValues(t2).contains(t2SearchVal);
				}
				if(op.equals("OR")) {
					return p.getTagValues(t1).contains(t1SearchVal) || p.getTagValues(t2).contains(t2SearchVal);
				}
				
				return true;
			}).collect(Collectors.toList());
			photosList.getItems().clear();
			photosList.getItems().addAll(searchResults);
			if(!searchResults.isEmpty()) {
				photosList.getSelectionModel().select(0);
				createAlbumWithSelection.setDisable(false);
			} else {
				createAlbumWithSelection.setDisable(true);
			}
			
		});
		
		albums.setOnAction((e) -> {
			app.changeToAlbumView(app.getCurrentUser());
		});
		
		if(handler != null) {
			createAlbumWithSelection.removeEventHandler(ActionEvent.ACTION, handler);
		}
		
		handler = (e) -> {
			TextInputDialog addDialog = new TextInputDialog();
			addDialog.setTitle("Create Album with Search result");
			addDialog.setHeaderText("Enter album name");
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
					for(Photo p: searchResults) {
						al.addPhoto(p);
					}
					app.changeToAlbumView(app.getCurrentUser());
				}
			});
		};
		
		createAlbumWithSelection.setOnAction(handler);
		createAlbumWithSelection.setDisable(true);
	}

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
	};
}
