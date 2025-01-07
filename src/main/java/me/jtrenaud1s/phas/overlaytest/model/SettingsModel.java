package me.jtrenaud1s.phas.overlaytest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SettingsModel {
    private final BooleanProperty countUpTimer = new SimpleBooleanProperty(false);
    private final BooleanProperty showOverlayByDefault = new SimpleBooleanProperty(false);
    // Return the observable list directly
    @Getter
    private final ObservableList<Keybind> keybinds = FXCollections.observableArrayList();

    public SettingsModel() {
        this.countUpTimer.set(true);
        this.showOverlayByDefault.set(true);

        this.keybinds.setAll(
                new Keybind("Toggle Overlay", List.of("Ctrl", "Shift", "A")),
                new Keybind("Start/Reset Smudge Timer", List.of("Space", "Mouse 2")),
                new Keybind("Stop Smudge Timer", List.of("Ctrl", "Space", "Mouse 2"))
        );
    }

    public boolean isCountUpTimer() {
        return countUpTimer.get();
    }

    public void setCountUpTimer(boolean value) {
        countUpTimer.set(value);
    }

    public BooleanProperty countUpTimerProperty() {
        return countUpTimer;
    }

    public boolean isShowOverlayByDefault() {
        return showOverlayByDefault.get();
    }

    public void setShowOverlayByDefault(boolean value) {
        showOverlayByDefault.set(value);
    }

    public BooleanProperty showOverlayByDefaultProperty() {
        return showOverlayByDefault;
    }

    public void setKeybinds(List<Keybind> newKeybinds) {
        keybinds.setAll(newKeybinds);
    }

    /**
     * Load from JSON file.
     */
    public void loadFromFile(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        SettingsModel loaded = mapper.readValue(new File(filePath), SettingsModel.class);
        setCountUpTimer(loaded.isCountUpTimer());
        setShowOverlayByDefault(loaded.isShowOverlayByDefault());
        setKeybinds(loaded.getKeybinds());
    }

    /**
     * Save to JSON file.
     */
    public void saveToFile(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), this);
    }

    @Setter
    @Getter
    public static class Keybind {
        private String name;
        private List<String> keys;

        public Keybind() {}

        public Keybind(String name, List<String> keys) {
            this.name = name;
            this.keys = keys;
        }

        @Override
        public String toString() {
            return String.join(" + ", keys);
        }
    }
}