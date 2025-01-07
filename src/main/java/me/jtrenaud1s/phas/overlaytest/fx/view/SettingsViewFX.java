package me.jtrenaud1s.phas.overlaytest.fx.view;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import me.jtrenaud1s.phas.overlaytest.model.SettingsModel;

public class SettingsViewFX {
    private final Stage stage;

    @Getter
    private final TableView<SettingsModel.Keybind> keybindTable;

    // These are JavaFX properties for the two booleans the user can toggle.
    @Getter
    private final SimpleBooleanProperty countUpProperty = new SimpleBooleanProperty(false);

    @Getter
    private final SimpleBooleanProperty showOverlayProperty = new SimpleBooleanProperty(false);

    private final CheckBox countUpCheckBox;
    private final CheckBox showOverlayCheckBox;

    public SettingsViewFX() {
        this.stage = new Stage();
        stage.setTitle("Settings");
        stage.setWidth(600);
        stage.setHeight(400);

        BorderPane root = new BorderPane();
        TabPane tabPane = new TabPane();

        Tab generalTab = new Tab("General");
        generalTab.setClosable(false);

        Tab keybindsTab = new Tab("Keybinds");
        keybindsTab.setClosable(false);

        tabPane.getTabs().addAll(generalTab, keybindsTab);

        // Table of keybinds
        keybindTable = new TableView<>();
        setupKeybindTable();
        keybindsTab.setContent(keybindTable);
        root.setCenter(tabPane);

        // Checkboxes for general settings
        countUpCheckBox = new CheckBox("Reverse Timer");
        countUpCheckBox.setTooltip(new Tooltip("Reverse the timer (count up) instead of down."));
        // Bind the checkbox to the property so changes auto-update the property
        countUpCheckBox.selectedProperty().bindBidirectional(countUpProperty);

        showOverlayCheckBox = new CheckBox("Show Overlay by Default");
        showOverlayCheckBox.setTooltip(new Tooltip("Show the overlay by default at startup."));
        showOverlayCheckBox.selectedProperty().bindBidirectional(showOverlayProperty);

        VBox generalContent = new VBox(10, countUpCheckBox, showOverlayCheckBox);
        generalContent.setPadding(new javafx.geometry.Insets(10));
        generalTab.setContent(generalContent);

        Scene scene = new Scene(root);
        stage.setScene(scene);

        // By default, close the entire app if user closes settings
        stage.setOnCloseRequest(evt -> {
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
        keybindTable.getColumns().addAll(nameCol, chordCol);

        // We typically set the items from the Controller: keybindTable.setItems(model.getKeybinds());
    }

    public void setKeybindData(ObservableList<SettingsModel.Keybind> data) {
        keybindTable.setItems(data);
    }

    public void showStage() {
        stage.show();
    }

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