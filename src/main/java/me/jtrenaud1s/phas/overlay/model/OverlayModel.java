package me.jtrenaud1s.phas.overlay.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;

/**
 * OverlayModel holds data about the overlay's state.
 */
@Getter
public class OverlayModel {
    private final BooleanProperty overlayVisible = new SimpleBooleanProperty(false);
    private final BooleanProperty crosshairEnabled = new SimpleBooleanProperty(false);

    public OverlayModel() {
        overlayVisible.set(false);
        crosshairEnabled.set(false);
    }
}