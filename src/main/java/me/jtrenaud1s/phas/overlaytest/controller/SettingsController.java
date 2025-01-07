package me.jtrenaud1s.phas.overlaytest.controller;

import lombok.extern.slf4j.Slf4j;
import me.jtrenaud1s.phas.overlaytest.fx.view.OverlayViewFX;
import me.jtrenaud1s.phas.overlaytest.fx.view.SettingsViewFX;
import me.jtrenaud1s.phas.overlaytest.keybind.KeybindListener;
import me.jtrenaud1s.phas.overlaytest.keybind.KeybindRecorder;
import me.jtrenaud1s.phas.overlaytest.model.SettingsModel;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Slf4j
public class SettingsController {
    private final SettingsModel model;
    private final SettingsViewFX view;
    private final OverlayViewFX overlayView;
    private final KeybindRecorder keybindRecorder;
    private final KeybindListener keybindListener;

    private final String settingsFilePath = "settings.json";

    public SettingsController(
            SettingsModel model,
            SettingsViewFX view,
            OverlayViewFX overlayView,
            KeybindRecorder keybindRecorder,
            KeybindListener keybindListener
    ) {
        this.model = model;
        this.view = view;
        this.overlayView = overlayView;
        this.keybindRecorder = keybindRecorder;
        this.keybindListener = keybindListener;

        // 1) Load data from file
        loadSettings();

        // 2) Initialize the UI from the model
        initializeViewBindings();
        updateKeybindTable(); // Fill the table with the model’s list

        // 3) Setup user actions
        setupListeners();

        // 4) If the user’s setting says “show overlay,” show it
        if (model.isShowOverlayByDefault()) {
            overlayView.showOverlay();
        }
    }

    private void initializeViewBindings() {
        // The view has SimpleBooleanProperties: countUpProperty, showOverlayProperty
        // Bind them to the model’s properties (bidirectional if you want live updates both ways).
        view.getCountUpProperty().bindBidirectional(model.countUpTimerProperty());
        view.getShowOverlayProperty().bindBidirectional(model.showOverlayByDefaultProperty());

        // Alternatively, you could do the binding in the view class.
        // But here in the controller is also common practice.
    }

    private void setupListeners() {
        // Whenever the user toggles the checkboxes in the view,
        // the model automatically updates (and vice versa).
        // So no explicit listener is needed for those booleans
        // unless you want to do additional logic.

        // Double-click on table row => startKeybindRecording
        view.getKeybindTable().setOnMouseClicked(evt -> {
            if (evt.getClickCount() == 2 && !view.getKeybindTable().getSelectionModel().isEmpty()) {
                int rowIndex = view.getKeybindTable().getSelectionModel().getSelectedIndex();
                startKeybindRecording(rowIndex);
            }
        });

        // Register existing keybinds with the KeybindListener
        registerKeybindsToListener();
    }

    private void startKeybindRecording(int rowIndex) {
        keybindListener.pause();

        // Temporarily show that we're "Recording"
        SettingsModel.Keybind keybind = model.getKeybinds().get(rowIndex);
        keybind.setKeys(List.of("Recording..."));
        // The table automatically updates because `model.getKeybinds()` is an ObservableList.

        keybindRecorder.startRecording(recordedChord -> {
            String[] keys = recordedChord.split(" \\+ ");
            keybind.setKeys(List.of(keys));

            saveSettings();             // persist new keybind
            registerKeybindsToListener();
            keybindListener.resume();   // done recording
        });
    }

    private void registerKeybindsToListener() {
        keybindListener.clearKeybinds();

        for (SettingsModel.Keybind keybind : model.getKeybinds()) {
            log.info("Associating keybind: {} -> {}", keybind.getKeys(), keybind.getName());
            Set<String> keybindSet = Set.copyOf(keybind.getKeys());
            keybindListener.associateKeybind(keybindSet, keybind.getName());
        }
    }

    private void updateKeybindTable() {
        // Just set the TableView’s items to the model's ObservableList
        view.getKeybindTable().setItems(model.getKeybinds());
    }

    private void loadSettings() {
        try {
            model.loadFromFile(settingsFilePath);
        } catch (IOException e) {
            log.warn("No existing settings file found. Using defaults.", e);
            initializeDefaults();
            saveSettings();
        }
    }

    private void initializeDefaults() {
        model.getKeybinds().clear();
        model.getKeybinds().add(new SettingsModel.Keybind("Open Inventory", List.of("Ctrl", "I")));
        model.getKeybinds().add(new SettingsModel.Keybind("Jump", List.of("Space")));
        model.getKeybinds().add(new SettingsModel.Keybind("Run", List.of("Shift", "W")));
    }

    private void saveSettings() {
        try {
            model.saveToFile(settingsFilePath);
        } catch (IOException e) {
            log.error("Failed to save settings: {}", e.getMessage());
        }
    }
}
