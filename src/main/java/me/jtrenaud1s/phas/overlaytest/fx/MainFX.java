package me.jtrenaud1s.phas.overlaytest.fx;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import me.jtrenaud1s.phas.overlaytest.controller.SettingsController;
import me.jtrenaud1s.phas.overlaytest.keybind.KeybindListener;
import me.jtrenaud1s.phas.overlaytest.keybind.KeybindRecorder;
import me.jtrenaud1s.phas.overlaytest.model.SettingsModel;
import me.jtrenaud1s.phas.overlaytest.fx.view.OverlayViewFX;
import me.jtrenaud1s.phas.overlaytest.fx.view.SettingsViewFX;

@Slf4j
public class MainFX extends Application {

    public static final String ACTION_TOGGLE_OVERLAY = "Toggle Overlay";
    public static final String ACTION_START_RESET_TIMER = "Start/Reset Smudge Timer";
    public static final String ACTION_STOP_TIMER = "Stop Smudge Timer";

    @Override
    public void start(Stage primaryStage) {
        SettingsModel model = new SettingsModel();

        OverlayViewFX overlayView = new OverlayViewFX();
        SettingsViewFX settingsView = new SettingsViewFX();

        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            log.error("Failed to register native hook", e);
            return;
        }

        KeybindRecorder keybindRecorder = new KeybindRecorder();
        KeybindListener keybindListener = getKeybindListener(overlayView, model);

        new SettingsController(model, settingsView, overlayView, keybindRecorder, keybindListener);

        settingsView.showStage();
    }

    private static KeybindListener getKeybindListener(OverlayViewFX overlayView, SettingsModel model) {
        KeybindListener keybindListener = new KeybindListener();

        // Register actions (called from the listener)
        keybindListener.registerAction(ACTION_TOGGLE_OVERLAY, () -> {
            Platform.runLater(() -> {
                boolean showing = overlayView.isShowing();
                if (showing) {
                    overlayView.hideOverlay();
                } else {
                    overlayView.showOverlay();
                }
                log.info("Overlay visibility toggled: {}", !showing);
            });
        });

        keybindListener.registerAction(ACTION_START_RESET_TIMER, () -> {
            Platform.runLater(() -> {
                overlayView.getSmudgeTimerPane().startOrResetTimer(model.isCountUpTimer());
                log.info("Smudge timer started/reset.");
            });
        });

        keybindListener.registerAction(ACTION_STOP_TIMER, () -> {
            Platform.runLater(() -> {
                overlayView.getSmudgeTimerPane().stopTimer();
                log.info("Smudge timer stopped.");
            });
        });
        return keybindListener;
    }

    public static void main(String[] args) {
        launch(args);
    }
}