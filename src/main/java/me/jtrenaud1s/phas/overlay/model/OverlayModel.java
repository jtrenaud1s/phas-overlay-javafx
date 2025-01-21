package me.jtrenaud1s.phas.overlay.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import lombok.Getter;

/**
 * OverlayModel holds data about the overlay's state.
 */
@Getter
public class OverlayModel {
    private final BooleanProperty overlayVisible = new SimpleBooleanProperty(false);
    private final BooleanProperty crosshairEnabled = new SimpleBooleanProperty(false);
    private final DoubleProperty smudgeVolume = new SimpleDoubleProperty(1.0D);
}