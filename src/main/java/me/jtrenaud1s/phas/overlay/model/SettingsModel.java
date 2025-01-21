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

    private final ObservableList<Keybind> keybinds = FXCollections.observableArrayList();

    public SettingsModel() {
        this.countUpTimer.set(false);
        this.showOverlayByDefault.set(true);
        this.crosshairEnabled.set(true);
        this.settingsHidden.set(false);
        this.smudgeVolume.set(1.0);

        this.keybinds.setAll(
                new Keybind("Toggle Overlay", List.of("Ctrl", "Shift", "A")),
                new Keybind("Start/Reset Smudge Timer", List.of("Space", "Mouse 2")),
                new Keybind("Stop Smudge Timer", List.of("Ctrl", "Space", "Mouse 2")),
                new Keybind("Toggle Crosshair", List.of("Ctrl", "Shift", "C")),
                new Keybind("Toggle Settings", List.of("Ctrl", "Shift", "S")),
                new Keybind("Quit", List.of("Ctrl", "Shift", "Q"))
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

    // --- JSON file I/O ---

    public void loadFromFile(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        SettingsModel loaded = mapper.readValue(new File(filePath), SettingsModel.class);

        setCountUpTimer(loaded.isCountUpTimer());
        setShowOverlayByDefault(loaded.isShowOverlayByDefault());
        setCrosshairEnabled(loaded.isCrosshairEnabled());
        setSettingsHidden(loaded.isSettingsHidden());
        setSmudgeVolume(loaded.getSmudgeVolume());

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
    }

    public void saveToFile(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), this);
    }
}
