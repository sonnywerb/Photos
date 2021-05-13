package views;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;
import model.Album;
import model.Photo;
import model.Tag;
import util.CalendarUtility;

/**
 * Class which displays a specific photo into a separate area with its attribute
 * @author Dev Patel and Eric Chan
 */
public class PhotoAreaController extends BaseController {

	@FXML
	private TableView<Pair<String, String>> tagsTable;

	@FXML
	private Hyperlink albumName;

	@FXML
	private Label picDate;

	@FXML
	private TextArea picCaption;

	@FXML
	private Button nextPic;

	@FXML
	private Button prevPic;

	@FXML
	private ImageView picView;
	
	// Method to set the photo on the view
	private void setPhoto(Photo photo) {
		picView.setImage(new Image(new File(photo.getFilePath()).toURI().toString()));
		picView.setPreserveRatio(true);
		picView.setStyle("-fx-padding: 6;");

		picCaption.setText(photo.getCaption());

		picDate.setText(CalendarUtility.calendarToDate(photo.getPhotoDate()));

		// Also update tags.
		tagsTable.getItems().clear();

		// Also, update the tags now.
		HashMap<Tag, ArrayList<String>> tags = photo.getTags();
		for (Tag tag : tags.keySet()) {
			for (String value : tags.get(tag)) {
				tagsTable.getItems().add(new Pair<String, String>(tag.getTagKey(), value));
			}
		}
	}

	@Override
	public void setup() {
		super.setup();
		tagsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tagsTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("key"));
		tagsTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("value"));
		tagsTable.setPlaceholder(new Label("No Tags for photo."));
		
		albumName.setText(app.getCurrentAlbum().getName());
		albumName.setOnAction((e) -> {
			app.changeToAlbumsPhotoPage(app.getCurrentAlbum());
		});

		Photo currentPic = app.getCurrentPic();
		Album currentAlbum = app.getCurrentAlbum();
		int index = database.getPicIndexInAlbum(currentAlbum, currentPic);

		if (index < currentAlbum.getNumPhotos() - 1) {
			nextPic.setOnAction((e) -> {
				app.changeToPhotoAreaView(app.getCurrentAlbum().getPhotos().get(index + 1));
			});
		} else {
			nextPic.setDisable(true);
		}

		if (index > 0) {
			prevPic.setOnAction((e) -> {
				app.changeToPhotoAreaView(app.getCurrentAlbum().getPhotos().get(index - 1));
			});
		} else {
			prevPic.setDisable(true);
		}

		setPhoto(currentPic);
	}

}
