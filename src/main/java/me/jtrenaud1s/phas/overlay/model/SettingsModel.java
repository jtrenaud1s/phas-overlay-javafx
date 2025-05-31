package me.jtrenaud1s.phas.overlay.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@Slf4j
public class SettingsModel {
    @JsonIgnore
    private final BooleanProperty countUpTimer = new SimpleBooleanProperty(false);
    @JsonIgnore
    private final BooleanProperty showOverlayByDefault = new SimpleBooleanProperty(false);
    @JsonIgnore
    private final BooleanProperty crosshairEnabled = new SimpleBooleanProperty(false);
    @JsonIgnore
    private final BooleanProperty settingsHidden = new SimpleBooleanProperty(false);
    @JsonIgnore
    private final DoubleProperty smudgeVolume = new SimpleDoubleProperty(1.0);
    @JsonIgnore
    private final BooleanProperty showMapSelection = new SimpleBooleanProperty(true);

    private final ObservableList<Keybind> keybinds = FXCollections.observableArrayList();
    private final ObservableList<Map> maps = FXCollections.observableArrayList();

    private static final SecureRandom random = new SecureRandom();

    public SettingsModel() {
        this.countUpTimer.set(false);
        this.showOverlayByDefault.set(true);
        this.crosshairEnabled.set(true);
        this.settingsHidden.set(false);
        this.smudgeVolume.set(1.0);
        this.showMapSelection.set(true);

        this.keybinds.setAll(
                new Keybind("Toggle Overlay", List.of("Ctrl", "Shift", "A")),
                new Keybind("Start/Reset Smudge Timer", List.of("Space", "Mouse 2")),
                new Keybind("Stop Smudge Timer", List.of("Ctrl", "Space", "Mouse 2")),
                new Keybind("Toggle Crosshair", List.of("Ctrl", "Shift", "C")),
                new Keybind("Toggle Settings", List.of("Ctrl", "Shift", "S")),
                new Keybind("Quit", List.of("Ctrl", "Shift", "Q")),
                new Keybind("Select Random Map", List.of("Ctrl", "Shift", "M"))
        );

        // Add default maps - all Phasmophobia maps as of May 2025
        this.maps.setAll(
                new Map("Bleasdale", true),
                new Map("Brownstone", true),
                new Map("Camp Woodwind", true),
                new Map("Edgefield", true),
                new Map("Grafton", true),
                new Map("Maple Lodge", true),
                new Map("Point Hope", true),
                new Map("Prison", true),
                new Map("Ridgeview", true),
                new Map("Sunny Meadows (Restricted)", true),
                new Map("Sunny Meadows (Full)", true),
                new Map("Tanglewood", true),
                new Map("Willow", true)
        );
    }

    // --- JSON-annotated getters/setters ---

    @JsonProperty("countUpTimer")
    public boolean isCountUpTimer() {
        return countUpTimer.get();
    }

    @JsonProperty("countUpTimer")
    public void setCountUpTimer(boolean value) {
        countUpTimer.set(value);
    }

    @JsonProperty("showOverlayByDefault")
    public boolean isShowOverlayByDefault() {
        return showOverlayByDefault.get();
    }

    @JsonProperty("showOverlayByDefault")
    public void setShowOverlayByDefault(boolean value) {
        showOverlayByDefault.set(value);
    }

    @JsonProperty("crosshairEnabled")
    public boolean isCrosshairEnabled() {
        return crosshairEnabled.get();
    }

    @JsonProperty("crosshairEnabled")
    public void setCrosshairEnabled(boolean value) {
        crosshairEnabled.set(value);
    }

    @JsonProperty("settingsHidden")
    public boolean isSettingsHidden() {
        return settingsHidden.get();
    }

    @JsonProperty("settingsHidden")
    public void setSettingsHidden(boolean value) {
        settingsHidden.set(value);
    }

    @JsonProperty("keybinds")
    public void setKeybinds(List<Keybind> newKeybinds) {
        keybinds.setAll(newKeybinds);
    }

    @JsonProperty("smudgeVolume")
    public double getSmudgeVolume() {
        return smudgeVolume.get();
    }

    @JsonProperty("smudgeVolume")
    public void setSmudgeVolume(double value) {
        smudgeVolume.set(value);
    }

    public DoubleProperty smudgeVolumeProperty() {
        return smudgeVolume;
    }

    @JsonProperty("showMapSelection")
    public boolean isShowMapSelection() {
        return showMapSelection.get();
    }

    @JsonProperty("showMapSelection")
    public void setShowMapSelection(boolean value) {
        showMapSelection.set(value);
    }

    @JsonProperty("maps")
    public void setMaps(List<Map> newMaps) {
        maps.setAll(newMaps);
    }

    // --- JSON file I/O ---

    public void loadFromFile(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        SettingsModel loaded = mapper.readValue(new File(filePath), SettingsModel.class);

        setCountUpTimer(loaded.isCountUpTimer());
        setShowOverlayByDefault(loaded.isShowOverlayByDefault());
        setCrosshairEnabled(loaded.isCrosshairEnabled());
        setSettingsHidden(loaded.isSettingsHidden());
        setSmudgeVolume(loaded.getSmudgeVolume());
        setShowMapSelection(loaded.isShowMapSelection());

        // Keep or merge keybinds
        for (Keybind defaultKeybind : keybinds) {
            loaded.getKeybinds().stream()
                    .filter(k -> k.getName().equals(defaultKeybind.getName()))
                    .findFirst()
                    .ifPresentOrElse(
                            loadedKeybind -> defaultKeybind.setKeys(loadedKeybind.getKeys()),
                            () -> log.info("Keeping default keybind: {}", defaultKeybind.getName())
                    );
        }
        loaded.getKeybinds().stream()
                .filter(loadedKeybind -> keybinds.stream()
                        .noneMatch(defaultKeybind -> defaultKeybind.getName().equals(loadedKeybind.getName())))
                .forEach(keybinds::add);

        // Keep or merge maps
        for (Map defaultMap : maps) {
            loaded.getMaps().stream()
                    .filter(m -> m.getName().equals(defaultMap.getName()))
                    .findFirst()
                    .ifPresentOrElse(
                            loadedMap -> defaultMap.setEnabled(loadedMap.isEnabled()),
                            () -> log.info("Keeping default map: {}", defaultMap.getName())
                    );
        }
        loaded.getMaps().stream()
                .filter(loadedMap -> maps.stream()
                        .noneMatch(defaultMap -> defaultMap.getName().equals(loadedMap.getName())))
                .forEach(maps::add);
    }

    public void saveToFile(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), this);
    }

    /**
     * Selects a random enabled map and returns its name.
     * Updates the selected map in the overlay model.
     */
    public String selectRandomMap() {
        var enabledMaps = maps.filtered(Map::isEnabled);
        if (!enabledMaps.isEmpty()) {
            int randomIndex = random.nextInt(enabledMaps.size());
            return enabledMaps.get(randomIndex).getName();
        }
        return null; // No enabled maps
    }
}
