package me.jtrenaud1s.phas.overlay.controller;

import lombok.extern.slf4j.Slf4j;
import me.jtrenaud1s.phas.overlay.model.Keybind;
import me.jtrenaud1s.phas.overlay.view.SettingsView;
import me.jtrenaud1s.phas.overlay.keybind.KeybindListener;
import me.jtrenaud1s.phas.overlay.keybind.KeybindRecorder;
import me.jtrenaud1s.phas.overlay.model.SettingsModel;
import me.jtrenaud1s.phas.overlay.model.OverlayModel;

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

    private final OverlayModel overlayModel;
    // Instead of referencing the overlay view or controller directly,
    // we store only the model for minimal coupling. The main code can pass this in.

    private final Path settingsFilePath;

    public SettingsController(
            SettingsModel model,
            SettingsView view,
            OverlayModel overlayModel,
            KeybindRecorder keybindRecorder,
            KeybindListener keybindListener
    ) {
        this.model = model;
        this.view = view;
        this.overlayModel = overlayModel;
        this.keybindRecorder = keybindRecorder;
        this.keybindListener = keybindListener;

        this.settingsFilePath = getUserAppDirectory().resolve("settings.json");
        createUserAppDirectoryIfNotExists();

        loadSettings();

        initializeViewBindings();
        updateKeybindTable();
        setupListeners();

        overlayModel.getOverlayVisible().set(model.isShowOverlayByDefault());
        overlayModel.getCrosshairEnabled().set(model.isCrosshairEnabled());
    }

    private void initializeViewBindings() {
        view.getCountUpProperty().bindBidirectional(model.getCountUpTimer());
        view.getShowOverlayProperty().bindBidirectional(model.getShowOverlayByDefault());

        model.getShowOverlayByDefault().addListener((obs, oldVal, newVal) -> {
            overlayModel.getOverlayVisible().set(newVal);
        });

        view.getCrosshairProperty().set(model.isCrosshairEnabled());
        view.getCrosshairProperty().addListener((obs, oldVal, newVal) -> {
            model.setCrosshairEnabled(newVal);
            overlayModel.getCrosshairEnabled().set(newVal);
            saveSettings();
        });
    }

    private void setupListeners() {
        view.getKeybindTable().setOnMouseClicked(evt -> {
            if (evt.getClickCount() == 2 && !view.getKeybindTable().getSelectionModel().isEmpty()) {
                int rowIndex = view.getKeybindTable().getSelectionModel().getSelectedIndex();
                startKeybindRecording(rowIndex);
            }
        });

        registerKeybindsToListener();
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

    private void registerKeybindsToListener() {
        keybindListener.clearKeybinds();

        for (Keybind keybind : model.getKeybinds()) {
            log.info("Associating keybind: {} -> {}", keybind.getKeys(), keybind.getName());
            Set<String> keybindSet = Set.copyOf(keybind.getKeys());
            keybindListener.associateKeybind(keybindSet, keybind.getName());
        }
    }

    private void updateKeybindTable() {
        view.getKeybindTable().setItems(model.getKeybinds());
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
            appDir = userHome + "/Library/Application Support"; // e.g., /Users/<User>/Library/Application Support
        } else {
            appDir = userHome + "/.config"; // e.g., /home/<User>/.config
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
