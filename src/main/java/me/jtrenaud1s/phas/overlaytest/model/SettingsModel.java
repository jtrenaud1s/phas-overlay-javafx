package me.jtrenaud1s.phas.overlaytest.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SettingsModel {
    @Getter
    private boolean countUpTimer;
    @Getter
    private boolean showOverlayByDefault;
    private final List<Keybind> keybinds;

    @JsonIgnore // Exclude from serialization
    private transient PropertyChangeSupport propertyChangeSupport;

    public SettingsModel() {
        this.countUpTimer = false;
        this.showOverlayByDefault = false;
        this.keybinds = new ArrayList<>();
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    private void initializePropertyChangeSupport() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void setCountUpTimer(boolean feature1Enabled) {
        boolean oldValue = this.countUpTimer;
        this.countUpTimer = feature1Enabled;
        propertyChangeSupport.firePropertyChange("countUpTimer", oldValue, feature1Enabled);
    }

    public void setShowOverlayByDefault(boolean showOverlayByDefault) {
        boolean oldValue = this.showOverlayByDefault;
        this.showOverlayByDefault = showOverlayByDefault;
        propertyChangeSupport.firePropertyChange("showOverlayByDefault", oldValue, showOverlayByDefault);
    }

    public List<Keybind> getKeybinds() {
        return new ArrayList<>(keybinds); // Return a copy for immutability
    }

    public void addKeybind(Keybind keybind) {
        keybinds.add(keybind);
        propertyChangeSupport.firePropertyChange("keybinds", null, keybinds);
    }

    public void updateKeybind(int index, Keybind keybind) {
        if (index >= 0 && index < keybinds.size()) {
            Keybind oldKeybind = keybinds.set(index, keybind);
            propertyChangeSupport.firePropertyChange("keybinds", oldKeybind, keybinds);
        }
    }

    public void loadFromFile(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        SettingsModel loadedSettings = mapper.readValue(new File(filePath), SettingsModel.class);

        setCountUpTimer(loadedSettings.countUpTimer);
        setShowOverlayByDefault(loadedSettings.showOverlayByDefault);
        keybinds.clear();
        keybinds.addAll(loadedSettings.keybinds);

        initializePropertyChangeSupport(); // Reinitialize PropertyChangeSupport
        propertyChangeSupport.firePropertyChange("keybinds", null, keybinds);
    }

    public void saveToFile(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
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
