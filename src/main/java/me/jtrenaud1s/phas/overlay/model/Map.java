package me.jtrenaud1s.phas.overlay.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a Phasmophobia map that can be selected randomly.
 */
@Getter
@NoArgsConstructor
public class Map {
    @Setter
    private String name;
    private final BooleanProperty enabled = new SimpleBooleanProperty(true);

    public Map(String name, boolean enabled) {
        this.name = name;
        this.enabled.set(enabled);
    }

    @JsonProperty("enabled")
    public boolean isEnabled() {
        return enabled.get();
    }

    @JsonProperty("enabled")
    public void setEnabled(boolean enabled) {
        this.enabled.set(enabled);
    }

    public BooleanProperty enabledProperty() {
        return enabled;
    }

    @Override
    public String toString() {
        return name;
    }
}
