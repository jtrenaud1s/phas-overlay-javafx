package me.jtrenaud1s.phas.overlay;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import me.jtrenaud1s.phas.overlay.controller.OverlayController;
import me.jtrenaud1s.phas.overlay.controller.SettingsController;
import me.jtrenaud1s.phas.overlay.view.OverlayView;
import me.jtrenaud1s.phas.overlay.keybind.KeybindListener;
import me.jtrenaud1s.phas.overlay.keybind.KeybindRecorder;
import me.jtrenaud1s.phas.overlay.model.OverlayModel;
import me.jtrenaud1s.phas.overlay.model.SettingsModel;
import me.jtrenaud1s.phas.overlay.view.SettingsView;


@Slf4j
public class Main extends Application {

    private OverlayView overlayView;

    @Override
    public void start(Stage primaryStage) {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            log.error("Failed to register native hook", e);
            return;
        }

        SettingsModel settingsModel = new SettingsModel();
        SettingsView settingsView = new SettingsView();

        KeybindRecorder keybindRecorder = new KeybindRecorder();
        KeybindListener keybindListener = new KeybindListener();

        OverlayModel overlayModel = new OverlayModel();
        overlayView = new OverlayView();
        OverlayController overlayController = new OverlayController(overlayModel, overlayView);

        new SettingsController(
                settingsModel,
                settingsView,
                overlayModel,
                keybindRecorder,
                keybindListener
        );

        keybindListener.registerAction("Toggle Overlay", () -> {
            boolean currentlyVisible = overlayModel.getOverlayVisible().get();
            Platform.runLater(() -> overlayModel.getOverlayVisible().set(!currentlyVisible));
        });

        keybindListener.registerAction("Start/Reset Smudge Timer", () -> Platform.runLater(
                () -> overlayController.startOrResetTimer(settingsModel.isCountUpTimer())));

        keybindListener.registerAction("Stop Smudge Timer",
                () -> Platform.runLater(overlayController::stopTimer));

        keybindListener.registerAction("Toggle Crosshair", () -> {
            boolean currentlyEnabled = overlayModel.getCrosshairEnabled().get();
            Platform.runLater(() -> overlayModel.getCrosshairEnabled().set(!currentlyEnabled));
        });

        keybindListener.registerAction("Quit", Platform::exit);
        settingsView.showStage();

    }

    //shutdown hook
    @Override
    public void stop() {
        log.info("Shutting down...");
        try {
            GlobalScreen.unregisterNativeHook();
            overlayView.getSmudgeTimerPane().shutdownScheduler();
        } catch (NativeHookException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
