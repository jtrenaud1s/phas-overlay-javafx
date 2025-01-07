package me.jtrenaud1s.phas.overlaytest.fx.view;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import me.jtrenaud1s.phas.overlaytest.model.SettingsModel;

@Getter
public class SettingsViewFX {
    private final TableView<SettingsModel.Keybind> keybindTable;
    @Getter
    private final SimpleBooleanProperty countUpProperty = new SimpleBooleanProperty(false);
    @Getter
    private final SimpleBooleanProperty showOverlayProperty = new SimpleBooleanProperty(false);
    private final CheckBox countUpCheckBox;
    private final CheckBox showOverlayCheckBox;
    private final Stage stage;

    public SettingsViewFX() {
        this.stage = new Stage();
        stage.setTitle("Settings");
        stage.setWidth(600);
        stage.setHeight(400);

        // Layout root
        BorderPane root = new BorderPane();
        TabPane tabPane = new TabPane();
        Tab keybindsTab = new Tab("Keybinds");
        keybindsTab.setClosable(false);

        Tab generalTab = new Tab("General");
        generalTab.setClosable(false);

        tabPane.getTabs().addAll(generalTab, keybindsTab);
        keybindTable = new TableView<>();
        setupKeybindTable();
        keybindsTab.setContent(keybindTable);
        root.setCenter(tabPane);

        countUpCheckBox = new CheckBox("Reverse Timer");
        countUpCheckBox.setTooltip(new Tooltip("Reverse the timer to count up instead of down."));
        // Bind the checkbox to the property
        countUpCheckBox.selectedProperty().bindBidirectional(countUpProperty);

        showOverlayCheckBox = new CheckBox("Show Overlay by Default");
        showOverlayCheckBox.setTooltip(new Tooltip("Show the overlay by default when the application starts."));
        // Bind the checkbox to the property
        showOverlayCheckBox.selectedProperty().bindBidirectional(showOverlayProperty);

        // For now, just put the checkbox in a VBox
        VBox generalContent = new VBox(10, countUpCheckBox, showOverlayCheckBox);
        generalContent.setPadding(new javafx.geometry.Insets(10));
        generalTab.setContent(generalContent);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        keybindTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        stage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });
    }

    private void setupKeybindTable() {
        TableColumn<SettingsModel.Keybind, String> nameCol = new TableColumn<>("Action");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<SettingsModel.Keybind, String> chordCol = new TableColumn<>("Keybind");
        chordCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().toString())
        );
        keybindTable.getColumns().clear();
        keybindTable.getColumns().add(nameCol);
        keybindTable.getColumns().add(chordCol);
    }

    public void setKeybindData(ObservableList<SettingsModel.Keybind> data) {
        keybindTable.setItems(data);
    }

    public void showStage() {
        stage.show();
    }

    // Or a direct getter for the checkbox
    public boolean isCountUpSelected() {
        return countUpProperty.get();
    }

    public void setCountUpSelected(boolean value) {
        countUpProperty.set(value);
    }

    public boolean isShowOverlaySelected() {
        return showOverlayProperty.get();
    }

    public void setShowOverlaySelected(boolean value) {
        showOverlayProperty.set(value);
    }

}
