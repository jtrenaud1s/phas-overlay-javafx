package me.jtrenaud1s.phas.overlaytest.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.TableRow;
import lombok.extern.slf4j.Slf4j;
import me.jtrenaud1s.phas.overlaytest.fx.view.OverlayViewFX;
import me.jtrenaud1s.phas.overlaytest.keybind.KeybindListener;
import me.jtrenaud1s.phas.overlaytest.keybind.KeybindRecorder;
import me.jtrenaud1s.phas.overlaytest.model.SettingsModel;
import me.jtrenaud1s.phas.overlaytest.fx.view.SettingsViewFX;

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

    public SettingsController(SettingsModel model, SettingsViewFX view,
                              OverlayViewFX overlayView, KeybindRecorder keybindRecorder,
                              KeybindListener keybindListener) {
        this.model = model;
        this.view = view;
        this.keybindRecorder = keybindRecorder;
        this.keybindListener = keybindListener;
        this.overlayView = overlayView;

        loadSettings();
        setupView();
        setupListeners();

        view.setCountUpSelected(model.isCountUpTimer());
        view.setShowOverlaySelected(model.isShowOverlayByDefault());
        view.getCountUpProperty().addListener((obs, oldVal, newVal) -> {
            model.setCountUpTimer(newVal);
            saveSettings();
        });
        view.getShowOverlayProperty().addListener((obs, oldVal, newVal) -> {
            model.setShowOverlayByDefault(newVal);
            saveSettings();
        });

        if (model.isShowOverlayByDefault()) {
            overlayView.showOverlay();
        }
    }

    private void setupView() {
        model.addPropertyChangeListener(evt -> {
            if ("keybinds".equals(evt.getPropertyName())) {
                Platform.runLater(this::updateKeybindTable);
            }
        });

        updateKeybindTable();
    }

    private void setupListeners() {
        view.getKeybindTable().setRowFactory(tv -> {
            TableRow<SettingsModel.Keybind> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    int rowIndex = row.getIndex();
                    startKeybindRecording(rowIndex);
                }
            });
            return row;
        });

        registerKeybindsToListener();
    }

    private void startKeybindRecording(int rowIndex) {
        keybindListener.pause();

        SettingsModel.Keybind keybind = model.getKeybinds().get(rowIndex);
        keybind.setKeys(List.of("Recording"));
        updateKeybindTable();

        keybindRecorder.startRecording(recordedChord -> {
            String[] keys = recordedChord.split(" \\+ ");
            List<String> newKeybind = List.of(keys);

            keybind.setKeys(newKeybind);
            updateKeybindTable();
            saveSettings();

            registerKeybindsToListener();
            keybindListener.resume();
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
        view.setKeybindData(FXCollections.observableArrayList(model.getKeybinds()));
    }

    private void loadSettings() {
        try {
            model.loadFromFile(settingsFilePath);
            updateKeybindTable();
        } catch (IOException e) {
            log.warn("No existing settings file found. Starting with defaults.", e);
            initializeDefaults();
            saveSettings();
        }
    }

    private void initializeDefaults() {
        model.getKeybinds().clear();
        model.addKeybind(new SettingsModel.Keybind("Open Inventory", List.of("Ctrl", "I")));
        model.addKeybind(new SettingsModel.Keybind("Jump", List.of("Space")));
        model.addKeybind(new SettingsModel.Keybind("Run", List.of("Shift", "W")));
    }

    private void saveSettings() {
        try {
            model.saveToFile(settingsFilePath);
        } catch (IOException e) {
            System.err.println("Failed to save settings: " + e.getMessage());
        }
    }
}
