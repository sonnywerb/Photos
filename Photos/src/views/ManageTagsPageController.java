package views;

import java.util.Optional;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Pair;
import model.Tag;

/**
 * Class controlling the display of tags on manage Tags page. This page displays
 * the tags for user specific.
 * @author Dev Patel and Eric Chan
 */
public class ManageTagsPageController extends BaseController {

	@FXML
	private TableView<Tag> tagsTable;

	@FXML
	private Button addTag;

	@FXML
	private Button deleteTag;
	
	@FXML
	private Hyperlink backToAlbums;

	@Override
	public void setup() {
		super.setup();

		tagsTable.setPlaceholder(new Label("No Tags available."));
		tagsTable.getColumns().get(0).setSortable(false);
		tagsTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("tagKey"));

		@SuppressWarnings("unchecked")
		TableColumn<Tag, CheckBox> tableColumn1 = (TableColumn<Tag, CheckBox>) tagsTable.getColumns().get(1);
		tableColumn1.setSortable(false);
		tableColumn1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Tag,CheckBox>, ObservableValue<CheckBox>>() {
			
			@Override
			public ObservableValue<CheckBox> call(CellDataFeatures<Tag, CheckBox> arg0) {
				Tag user = arg0.getValue();
                CheckBox checkBox = new CheckBox();
                checkBox.selectedProperty().setValue(user.isSingleValueTag());
                checkBox.setDisable(true);
                return new SimpleObjectProperty<CheckBox>(checkBox);
			}
		});
		
		addTag.setOnAction((e) -> {
			Dialog<Pair<String, Boolean>> dialog = new Dialog<>();
			dialog.setTitle("Manage Tags");
			dialog.setHeaderText("Add Tag");
			DialogPane dialogPane = dialog.getDialogPane();
			dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

			TextField tagField = new TextField();
			tagField.setPromptText("Tag name");

			CheckBox c = new CheckBox("Single Value Tag");
			dialogPane.setContent(new VBox(8, tagField, c));

			dialog.setResultConverter((ButtonType button) -> {
				if (button == ButtonType.OK) {
					return new Pair<String, Boolean>(tagField.getText(), c.isSelected());
				}
				return null;
			});

			Optional<Pair<String, Boolean>> optionalResult = dialog.showAndWait();
			optionalResult.ifPresent((Pair<String, Boolean> results) -> {
				String tagValue = results.getKey().trim();
				if (!tagValue.isBlank()) {
					database.addTagType(tagValue, results.getValue(), app.getCurrentUser());
					refreshData();
				}
			});
		});

		deleteTag.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Tag t = getCurrentlySelectedTag();
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Delete Tag");
				alert.setHeaderText("You are going to delete: " + t.getTagKey());
				alert.setContentText("Are you sure?");

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK) {
					database.removeTag(t);
					refreshData();
				}
			}
		});
		
		backToAlbums.setOnAction((e) -> {
			app.changeToAlbumView(app.getCurrentUser());
		});

		///////////////////////////////////
		refreshData();
	}
	
	private Tag getCurrentlySelectedTag() {
		return tagsTable.getSelectionModel().getSelectedItem();
	}

	// Method which populates the view based on the tags retrieved from the database
	private void refreshData() {
		tagsTable.getItems().clear();
		tagsTable.getItems()
				.addAll(FXCollections.observableArrayList(database.getUserOwnerTags(app.getCurrentUser())));
		tagsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				deleteTag.setDisable(false);
			} else {
				deleteTag.setDisable(true);
			}
		});

		deleteTag.setDisable(true);
		if (!tagsTable.getItems().isEmpty()) {
			tagsTable.getSelectionModel().select(0);
		}
	}

}
