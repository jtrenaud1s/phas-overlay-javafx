package me.jtrenaud1s.phas.overlay.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.jtrenaud1s.phas.overlay.view.OverlayView;
import me.jtrenaud1s.phas.overlay.model.OverlayModel;

import java.util.Objects;

@Slf4j
@Getter
public final class OverlayController {
    public static final double SCALE = 1.2D;
    private final OverlayModel overlayModel;
    private final OverlayView overlayView;

    public OverlayController(OverlayModel overlayModel, OverlayView overlayView) {
        this.overlayModel = overlayModel;
        this.overlayView = overlayView;

        overlayModel.getOverlayVisible().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                overlayView.showOverlay();
                overlayView.updateTimerPaneLayout(SCALE);
            } else {
                overlayView.hideOverlay();
            }
        });

        // NEW: crosshair listener
        overlayModel.getCrosshairEnabled().addListener((obs, oldVal, newVal) -> {
            overlayView.setCrosshairVisible(newVal);
            log.info("Crosshair is now {}", newVal ? "ON" : "OFF");
        });

        overlayView.updateTimerPaneLayout(SCALE);
        overlayView.setCrosshairVisible(overlayModel.getCrosshairEnabled().get());
        log.info("Crosshair enabled: {}", overlayModel.getCrosshairEnabled().get());
    }

    /**
     * Start or reset the timer in the smudge pane, using the user's
     * "countUpTimer" setting from the SettingsModel if needed.
     */
    public void startOrResetTimer(boolean countUp) {
        overlayView.getSmudgeTimerPane().startOrResetTimer(countUp);
    }

    public void stopTimer() {
        overlayView.getSmudgeTimerPane().stopTimer();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (OverlayController) obj;
        return Objects.equals(this.overlayModel, that.overlayModel) &&
                Objects.equals(this.overlayView, that.overlayView);
    }

    @Override
    public int hashCode() {
        return Objects.hash(overlayModel, overlayView);
    }

    @Override
    public String toString() {
        return "OverlayController[" +
                "overlayModel=" + overlayModel + ", " +
                "overlayView=" + overlayView + ']';
    }
}
