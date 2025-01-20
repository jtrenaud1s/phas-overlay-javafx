package me.jtrenaud1s.phas.overlay.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Slf4j
public class SettingsModel {
    @JsonIgnore
    private final BooleanProperty countUpTimer = new SimpleBooleanProperty(false);
    @JsonIgnore
    private final BooleanProperty showOverlayByDefault = new SimpleBooleanProperty(false);
    @JsonIgnore
    private final BooleanProperty crosshairEnabled = new SimpleBooleanProperty(false);

    private final ObservableList<Keybind> keybinds = FXCollections.observableArrayList();

    public SettingsModel() {
        this.countUpTimer.set(false);
        this.showOverlayByDefault.set(true);
        this.crosshairEnabled.set(true);

        this.keybinds.setAll(
                new Keybind("Toggle Overlay", List.of("Ctrl", "Shift", "A")),
                new Keybind("Start/Reset Smudge Timer", List.of("Space", "Mouse 2")),
                new Keybind("Stop Smudge Timer", List.of("Ctrl", "Space", "Mouse 2")),
                new Keybind("Toggle Crosshair", List.of("Ctrl", "Shift", "C")),
                new Keybind("Quit", List.of("Ctrl", "Shift", "Q"))
        );
    }

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

    @JsonProperty("keybinds")
    public void setKeybinds(List<Keybind> newKeybinds) {
        keybinds.setAll(newKeybinds);
    }

    @JsonProperty("crosshairEnabled")
    public boolean isCrosshairEnabled() {
        return crosshairEnabled.get();
    }

    @JsonProperty("crosshairEnabled")
    public void setCrosshairEnabled(boolean value) {
        crosshairEnabled.set(value);
    }

    /**
     * Load from JSON file.
     */
    public void loadFromFile(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        SettingsModel loaded = mapper.readValue(new File(filePath), SettingsModel.class);

        setCountUpTimer(loaded.isCountUpTimer());
        setShowOverlayByDefault(loaded.isShowOverlayByDefault());
        setCrosshairEnabled(loaded.isCrosshairEnabled());

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

    /**
     * Save to JSON file.
     */
    public void saveToFile(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), this);
    }
}