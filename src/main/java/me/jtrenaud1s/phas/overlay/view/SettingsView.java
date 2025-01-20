package me.jtrenaud1s.phas.overlay.view;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import me.jtrenaud1s.phas.overlay.model.Keybind;
import me.jtrenaud1s.phas.overlay.util.WindowUtil;

@Getter
public class SettingsView {
    private final Stage stage;
    private final TableView<Keybind> keybindTable;
    private final SimpleBooleanProperty countUpProperty = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty showOverlayProperty = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty crosshairProperty = new SimpleBooleanProperty(false);

    public SettingsView() {
        this.stage = new Stage();
        stage.setTitle("Settings");
        stage.setWidth(600);
        stage.setHeight(400);
        WindowUtil.timingDispatcher(stage);

        BorderPane root = new BorderPane();
        TabPane tabPane = new TabPane();

        Tab generalTab = new Tab("General");
        generalTab.setClosable(false);

        Tab keybindsTab = new Tab("Keybinds");
        keybindsTab.setClosable(false);

        tabPane.getTabs().addAll(generalTab, keybindsTab);

        keybindTable = new TableView<>();
        setupKeybindTable();
        keybindsTab.setContent(keybindTable);
        root.setCenter(tabPane);

        CheckBox countUpCheckBox = new CheckBox("Reverse Timer");
        countUpCheckBox.setTooltip(new Tooltip("Reverse the timer (count up) instead of down."));
        countUpCheckBox.selectedProperty().bindBidirectional(countUpProperty);

        CheckBox showOverlayCheckBox = new CheckBox("Show Overlay by Default");
        showOverlayCheckBox.setTooltip(new Tooltip("Show the overlay by default at startup."));
        showOverlayCheckBox.selectedProperty().bindBidirectional(showOverlayProperty);

        CheckBox crosshairCheckBox = new CheckBox("Enable Crosshair");
        crosshairCheckBox.setTooltip(new Tooltip("Show or hide a crosshair in the center of the screen."));
        crosshairCheckBox.selectedProperty().bindBidirectional(crosshairProperty);

        VBox generalContent = new VBox(10, countUpCheckBox, showOverlayCheckBox, crosshairCheckBox);
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
        TableColumn<Keybind, String> nameCol = new TableColumn<>("Action");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Keybind, String> chordCol = new TableColumn<>("Keybind");
        chordCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().toString())
        );

        keybindTable.getColumns().clear();
        keybindTable.getColumns().add(nameCol);
        keybindTable.getColumns().add(chordCol);
    }

    public void showStage() {
        stage.show();
    }
}