package me.jtrenaud1s.phas.overlay.view;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import me.jtrenaud1s.phas.overlay.model.Keybind;
import me.jtrenaud1s.phas.overlay.util.WindowUtil;

import java.util.Objects;

@Getter
public class SettingsView {
    private final Stage stage;
    private final TableView<Keybind> keybindTable;

    private final CheckBox countUpCheckBox;
    private final CheckBox showOverlayCheckBox;
    private final CheckBox crosshairCheckBox;
    private final CheckBox settingsHiddenCheckBox;

    private final Slider smudgeVolumeSlider;

    public SettingsView() {
        this.stage = new Stage();
        stage.setTitle("Settings");
        stage.setWidth(600);
        stage.setHeight(400);

        stage.getIcons().add(new Image(Objects.requireNonNull(
                SettingsView.class.getResourceAsStream("/images/PhasOverlay.png"))));

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
        // Constrain columns so no extra filler column appears
        keybindTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        root.setCenter(tabPane);

        countUpCheckBox = new CheckBox("Reverse Timer");
        showOverlayCheckBox = new CheckBox("Show Overlay by Default");
        crosshairCheckBox = new CheckBox("Enable Crosshair");
        settingsHiddenCheckBox = new CheckBox("Hide Settings By Default");

        countUpCheckBox.setTooltip(new Tooltip("Reverse the timer (count up) instead of down."));
        showOverlayCheckBox.setTooltip(new Tooltip("Show the overlay by default at startup."));
        crosshairCheckBox.setTooltip(new Tooltip("Show or hide a crosshair in the center of the screen."));
        settingsHiddenCheckBox.setTooltip(new Tooltip("Hide the settings window by default at startup."));

        smudgeVolumeSlider = new Slider(0.0, 1.0, 1.0);
        smudgeVolumeSlider.setShowTickMarks(true);
        smudgeVolumeSlider.setShowTickLabels(true);
        smudgeVolumeSlider.setBlockIncrement(0.1);

        Label volumeLabel = new Label("Smudge Timer Volume");

        VBox timerSettingsBox = new VBox(5, countUpCheckBox, volumeLabel, smudgeVolumeSlider);
        TitledPane timerPane = new TitledPane("Timer Settings", timerSettingsBox);
        timerPane.setCollapsible(false);

        VBox overlaySettingsBox = new VBox(5, showOverlayCheckBox, crosshairCheckBox);
        TitledPane overlayPane = new TitledPane("Overlay Settings", overlaySettingsBox);
        overlayPane.setCollapsible(false);

        VBox appSettingsBox = new VBox(5, settingsHiddenCheckBox);
        TitledPane appPane = new TitledPane("Application Settings", appSettingsBox);
        appPane.setCollapsible(false);

        VBox generalContent = new VBox(10, timerPane, overlayPane, appPane);
        generalContent.setPadding(new javafx.geometry.Insets(10));
        generalTab.setContent(generalContent);

        Scene scene = new Scene(root);
        stage.setScene(scene);

        Platform.setImplicitExit(false);

        // Hide rather than exit if user closes the settings
        stage.setOnCloseRequest(event -> {
            event.consume();
            stage.hide();
        });

        // Hide if minimized
        stage.iconifiedProperty().addListener((obs, wasIconified, isIconified) -> {
            if (isIconified) {
                stage.hide();
            }
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
        stage.toFront();
    }

    public void toggleStage() {
        if (stage.isShowing()) {
            stage.hide();
        } else {
            showStage();
        }
    }
}
