package me.jtrenaud1s.phas.overlay.controller;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import me.jtrenaud1s.phas.overlay.model.Keybind;
import me.jtrenaud1s.phas.overlay.util.WindowUtil;
import me.jtrenaud1s.phas.overlay.view.SettingsView;
import me.jtrenaud1s.phas.overlay.keybind.KeybindListener;
import me.jtrenaud1s.phas.overlay.keybind.KeybindRecorder;
import me.jtrenaud1s.phas.overlay.model.SettingsModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

@Slf4j
public final class SettingsController {
    private final SettingsModel model;
    private final SettingsView view;
    private final KeybindRecorder keybindRecorder;
    private final KeybindListener keybindListener;
    private final OverlayController overlayController;

    private final Path settingsFilePath;

    public SettingsController(
            SettingsModel model,
            SettingsView view,
            OverlayController overlayController,
            KeybindRecorder keybindRecorder,
            KeybindListener keybindListener
    ) {
        this.model = model;
        this.view = view;
        this.overlayController = overlayController;
        this.keybindRecorder = keybindRecorder;
        this.keybindListener = keybindListener;

        // The path to our user settings file
        this.settingsFilePath = getUserAppDirectory().resolve("settings.json");
        createUserAppDirectoryIfNotExists();

        loadSettings();
        view.getKeybindTable().setItems(model.getKeybinds());
        view.getMapListView().setItems(model.getMaps());
        initializeViewBindings();
        setupKeybindEditor();
        setupMapButtons();
        registerKeybindsToListener();

        overlayController.getOverlayModel().getOverlayVisible().set(model.isShowOverlayByDefault());
        overlayController.getOverlayModel().getCrosshairEnabled().set(model.isCrosshairEnabled());
        overlayController.getOverlayModel().getSmudgeVolume().set(model.getSmudgeVolume());
        overlayController.getOverlayModel().getMapSelectionVisible().set(model.isShowMapSelection());

        view.getStage().initOwner(overlayController.getOverlayView().getStage());

        if (!model.getSettingsHidden().get()) {
            view.showStage();
        }
        setupResetOverlayButton();
    }

    private void setupResetOverlayButton() {
        view.getResetOverlayButton().setOnAction(evt ->
                WindowUtil.resizeOverlayToPhasmophobia(overlayController.getOverlayView().getStage()));
    }

    private void initializeViewBindings() {
        view.getCountUpCheckBox().selectedProperty()
                .bindBidirectional(model.getCountUpTimer());
        view.getShowOverlayCheckBox().selectedProperty()
                .bindBidirectional(model.getShowOverlayByDefault());
        view.getCrosshairCheckBox().selectedProperty()
                .bindBidirectional(model.getCrosshairEnabled());
        view.getSettingsHiddenCheckBox().selectedProperty()
                .bindBidirectional(model.getSettingsHidden());
        view.getSmudgeVolumeSlider().valueProperty()
                .bindBidirectional(model.smudgeVolumeProperty());

        model.getCrosshairEnabled().addListener((obs, oldVal, newVal) -> {
            overlayController.getOverlayModel().getCrosshairEnabled().set(newVal);
            saveSettings();
        });

        model.getShowOverlayByDefault().addListener((obs, oldVal, newVal) -> {
            overlayController.getOverlayModel().getOverlayVisible().set(newVal);
            saveSettings();
        });

        model.smudgeVolumeProperty().addListener((obs, oldVal, newVal) -> {
            overlayController.getOverlayModel().getSmudgeVolume().set(newVal.doubleValue());
            saveSettings();
        });

        model.getSettingsHidden().addListener((obs, oldVal, newVal) -> saveSettings());
    }

    private void setupKeybindEditor() {
        view.getKeybindTable().setOnMouseClicked(evt -> {
            if (evt.getClickCount() == 2 && !view.getKeybindTable().getSelectionModel().isEmpty()) {
                int rowIndex = view.getKeybindTable().getSelectionModel().getSelectedIndex();
                startKeybindRecording(rowIndex);
            }
        });
    }

    private void startKeybindRecording(int rowIndex) {
        keybindListener.pause();

        Keybind keybind = model.getKeybinds().get(rowIndex);
        keybind.setKeys(List.of("Recording..."));
        view.getKeybindTable().refresh();

        keybindRecorder.startRecording(recordedChord -> {
            String[] keys = recordedChord.split(" \\+ ");
            keybind.setKeys(List.of(keys));

            saveSettings();
            registerKeybindsToListener();
            view.getKeybindTable().refresh();
            keybindListener.resume();
        });
    }

    private void setupMapButtons() {
        // Set up actions for map selection buttons
        view.getSelectAllMapsButton().setOnAction(evt -> {
            model.getMaps().forEach(map -> map.setEnabled(true));
            view.getMapListView().refresh();
            saveSettings();
        });

        view.getDeselectAllMapsButton().setOnAction(evt -> {
            model.getMaps().forEach(map -> map.setEnabled(false));
            view.getMapListView().refresh();
            saveSettings();
        });

        view.getSelectRandomMapButton().setOnAction(evt -> {
            String selectedMap = model.selectRandomMap();
            if (selectedMap != null) {
                overlayController.getOverlayModel().getSelectedMap().set(selectedMap);
            }
        });

        // Connect the show map selection checkbox to the overlay model
        view.getShowMapSelectionCheckBox().selectedProperty()
                .bindBidirectional(model.getShowMapSelection());

        model.getShowMapSelection().addListener((obs, oldVal, newVal) -> {
            overlayController.getOverlayModel().getMapSelectionVisible().set(newVal);
            overlayController.getOverlayView().setMapSelectorVisible(newVal);
            saveSettings();
        });
    }

    private void registerKeybindsToListener() {
        keybindListener.clearKeybinds();
        for (Keybind keybind : model.getKeybinds()) {
            log.info("Associating keybind: {} -> {}", keybind.getKeys(), keybind.getName());
            Set<String> keybindSet = Set.copyOf(keybind.getKeys());
            keybindListener.associateKeybind(keybindSet, keybind.getName());
        }
    }

    private void loadSettings() {
        try {
            log.info("Loading settings from file: {}", settingsFilePath);
            model.loadFromFile(settingsFilePath.toString());
        } catch (IOException e) {
            log.warn("No existing settings file found. Using defaults.", e);
            saveSettings();
        }
    }

    private void saveSettings() {
        try {
            model.saveToFile(settingsFilePath.toString());
        } catch (IOException e) {
            log.error("Failed to save settings: {}", e.getMessage());
        }
    }

    private Path getUserAppDirectory() {
        String userHome = System.getProperty("user.home");
        String appDir;

        if (isWindows()) {
            appDir = System.getenv("APPDATA"); // e.g., C:\Users\<User>\AppData\Roaming
        } else if (isMac()) {
            appDir = userHome + "/Library/Application Support";
        } else {
            appDir = userHome + "/.config";
        }
        return Path.of(appDir, "PhasOverlay");
    }

    private void createUserAppDirectoryIfNotExists() {
        try {
            Files.createDirectories(settingsFilePath.getParent());
        } catch (IOException e) {
            log.error("Failed to create user app directory: {}", e.getMessage());
        }
    }

    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    private boolean isMac() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }
}
