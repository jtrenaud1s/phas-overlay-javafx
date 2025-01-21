package me.jtrenaud1s.phas.overlay.view;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.Getter;
import me.jtrenaud1s.phas.overlay.model.Keybind;
import me.jtrenaud1s.phas.overlay.util.WindowUtil;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

@Getter
public class SettingsView implements Initializable {

    // -- Fields linked to FXML nodes ----------------------------------
    @FXML private BorderPane root;

    // Tabs and Panes
    @FXML private TabPane tabPane;
    @FXML private Tab generalTab;
    @FXML private Tab keybindsTab;
    @FXML private ScrollPane generalScrollPane;

    // Keybinds Table
    @FXML private TableView<Keybind> keybindTable;
    @FXML private TableColumn<Keybind, String> nameCol;
    @FXML private TableColumn<Keybind, String> chordCol;

    // CheckBoxes
    @FXML private CheckBox countUpCheckBox;
    @FXML private CheckBox showOverlayCheckBox;
    @FXML private CheckBox crosshairCheckBox;
    @FXML private CheckBox settingsHiddenCheckBox;

    // Slider
    @FXML private Slider smudgeVolumeSlider;

    // Button
    @FXML private Button resetOverlayButton;

    // -- Non-FXML fields ---------------------------------------------
    private Stage stage;  // We’ll store a reference to the stage here.

    /**
     * Called by the FXML loader when initialization is complete.
     * We can set up table columns, tooltips, etc. here.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup table columns
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        chordCol.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().toString())
        );

        // Setup tooltips
        countUpCheckBox.setTooltip(new Tooltip("Reverse the timer (count up) instead of down."));
        showOverlayCheckBox.setTooltip(new Tooltip("Show the overlay by default at startup."));
        crosshairCheckBox.setTooltip(new Tooltip("Show or hide a crosshair in the center of the screen."));
        settingsHiddenCheckBox.setTooltip(new Tooltip("Hide the settings window by default at startup."));
        resetOverlayButton.setTooltip(new Tooltip("Resize and move overlay to match Phasmophobia's window."));

        // Prevent close => hide window
        Platform.setImplicitExit(false);
    }

    /**
     * This method should be called once after the FXML is loaded.
     * It sets up the stage with the relevant properties, icons, etc.
     */
    public void setStage(Stage stage) {
        this.stage = stage;

        this.stage.setTitle("Settings");
        this.stage.setWidth(600);
        this.stage.setHeight(500);

        // Load icon
        this.stage.getIcons().add(new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/images/PhasOverlay.png"))));

        // Additional logic
        WindowUtil.timingDispatcher(this.stage);
        this.stage.setAlwaysOnTop(true);

        // Hide rather than exit if user closes the settings
        this.stage.setOnCloseRequest(event -> {
            event.consume();
            this.stage.hide();
        });

        // Hide if minimized
        this.stage.iconifiedProperty().addListener((obs, wasIconified, isIconified) -> {
            if (isIconified) {
                this.stage.hide();
            }
        });
    }

    /**
     * Show the stage on screen.
     */
    public void showStage() {
        if (stage != null) {
            stage.centerOnScreen();
            stage.show();
            WindowUtil.forceFocus(stage);
        }
    }

    /**
     * Toggle stage visibility.
     */
    public void toggleStage() {
        if (stage != null) {
            stage.setWidth(600);
            stage.setHeight(500);
            if (stage.isShowing()) {
                stage.hide();
            } else {
                showStage();
            }
        }
    }
}
