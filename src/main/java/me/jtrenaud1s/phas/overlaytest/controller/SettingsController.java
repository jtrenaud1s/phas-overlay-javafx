package me.jtrenaud1s.phas.overlaytest.controller;

import lombok.extern.slf4j.Slf4j;
import me.jtrenaud1s.phas.overlaytest.keybind.KeybindListener;
import me.jtrenaud1s.phas.overlaytest.keybind.KeybindRecorder;
import me.jtrenaud1s.phas.overlaytest.model.SettingsModel;
import me.jtrenaud1s.phas.overlaytest.view.SettingsView;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@Slf4j
public class SettingsController {
    private final SettingsModel model;
    private final SettingsView view;
    private final KeybindRecorder keybindRecorder;
    private final KeybindListener keybindListener;
    private final String settingsFilePath = "settings.json";

    public SettingsController(SettingsModel model, SettingsView view, KeybindRecorder keybindRecorder, KeybindListener keybindListener) {
        this.model = model;
        this.view = view;
        this.keybindRecorder = keybindRecorder;
        this.keybindListener = keybindListener;

        loadSettings();
        setupView();
        setupListeners();
    }

    private void setupView() {
        // Observe model changes
        model.addPropertyChangeListener(evt -> {
            SwingUtilities.invokeLater(() -> {
                switch (evt.getPropertyName()) {
                    case "keybinds" -> updateKeybindTable();
                    case "feature1Enabled" -> log.info("Feature 1 enabled: {}", model.isFeature1Enabled());
                    default -> {}
                }
            });
        });

        updateKeybindTable();
    }

    private void setupListeners() {
        view.getKeybindTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = view.getKeybindTable().getSelectedRow();
                    if (row >= 0) {
                        startKeybindRecording(row);
                    }
                }
            }
        });

        // Register keybinds from the model to the listener
        registerKeybindsToListener();
    }

    private void startKeybindRecording(int rowIndex) {
        keybindListener.pause(); // Pause the listener during recording

        // Temporarily set the keybind chord to "Recording"
        SettingsModel.Keybind keybind = model.getKeybinds().get(rowIndex);
        keybind.setKeys(List.of("Recording"));
        updateKeybindTable(); // Refresh table to show "Recording"

        // Start recording
        keybindRecorder.startRecording(recordedChord -> {
            String[] keys = recordedChord.split(" \\+ ");
            List<String> newKeybind = List.of(keys);

            // Update with the new keybind and stop recording
            keybind.setKeys(newKeybind);
            updateKeybindTable(); // Refresh table
            saveSettings(); // Persist the updated settings

            // Re-register keybinds and resume the listener
            registerKeybindsToListener();
            keybindListener.resume();
        });
    }

    private void registerKeybindsToListener() {
        keybindListener.clearKeybinds(); // Clear existing keybinds

        for (SettingsModel.Keybind keybind : model.getKeybinds()) {
            log.info("Associating keybind: {} -> {}", keybind.getKeys(), keybind.getName());
            Set<String> keybindSet = Set.copyOf(keybind.getKeys());
            keybindListener.associateKeybind(keybindSet, keybind.getName());
        }
    }

    private void updateKeybindTable() {
        String[] columnNames = {"Keybind Name", "Keybind Chord"};
        Object[][] data = model.getKeybinds().stream()
                .map(keybind -> new Object[]{keybind.getName(), keybind.toString()})
                .toArray(Object[][]::new);

        view.updateKeybindTable(data, columnNames);
    }

    private void loadSettings() {
        try {
            model.loadFromFile(settingsFilePath);
            updateKeybindTable(); // Refresh the UI with the loaded data
        } catch (IOException e) {
            log.warn("No existing settings file found. Starting with defaults.", e);
            initializeDefaults();
            saveSettings(); // Save defaults to create the settings file
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