package me.jtrenaud1s.phas.overlay;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import me.jtrenaud1s.phas.overlay.controller.OverlayController;
import me.jtrenaud1s.phas.overlay.controller.SettingsController;
import me.jtrenaud1s.phas.overlay.keybind.KeybindListener;
import me.jtrenaud1s.phas.overlay.keybind.KeybindRecorder;
import me.jtrenaud1s.phas.overlay.model.OverlayModel;
import me.jtrenaud1s.phas.overlay.model.SettingsModel;
import me.jtrenaud1s.phas.overlay.view.OverlayView;   // FXML-based controller for the overlay
import me.jtrenaud1s.phas.overlay.view.SettingsView; // FXML-based controller for settings

@Slf4j
public class Main extends Application {

    // UI Controllers
    private OverlayView overlayView;
    private SettingsView settingsView;

    // Domain controllers
    private OverlayController overlayController;

    // Models
    private SettingsModel settingsModel;
    private OverlayModel overlayModel;

    private KeybindListener keybindListener;

    @Override
    public void start(Stage primaryStage) {
        // 1) Attempt to register native hook for global keybinds
        if (!initNativeHook()) {
            return; // If it fails, just exit
        }

        // 2) Create Models
        settingsModel = new SettingsModel();
        overlayModel  = new OverlayModel();

        // 3) Create Keybind system
        // Keybind system
        KeybindRecorder keybindRecorder = new KeybindRecorder();
        keybindListener = new KeybindListener();

        // 4) Initialize FXML UIs (Settings + Overlay)
        if (!initSettingsView()) {
            return;
        }
        if (!initOverlayView()) {
            return;
        }

        // 5) Instantiate domain-level controllers
        overlayController = new OverlayController(overlayModel, overlayView);
        SettingsController settingsController = new SettingsController(
                settingsModel,
                settingsView,
                overlayController,
                keybindRecorder,
                keybindListener
        );

        // 6) Register global keybind actions
        registerKeybindActions();

        // This app doesn't need to show the "primaryStage," so we can hide it.
        primaryStage.hide();
    }

    /**
     * Cleanly stop the application, un-register native hook,
     * and shut down the overlay scheduler.
     */
    @Override
    public void stop() {
        log.info("Shutting down...");
        try {
            GlobalScreen.unregisterNativeHook();
            // If the overlay is loaded, stop its scheduler
            if (overlayView != null && overlayView.getSmudgeTimerPane() != null) {
                overlayView.getSmudgeTimerPane().shutdownScheduler();
            }
        } catch (NativeHookException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    // ==================================================================================
    //                         Private Helper Methods
    // ==================================================================================

    /**
     * Try to register the global hook for keybinds.
     */
    private boolean initNativeHook() {
        try {
            GlobalScreen.registerNativeHook();
            return true;
        } catch (NativeHookException e) {
            log.error("Failed to register native hook", e);
            return false;
        }
    }

    /**
     * Load and initialize SettingsView from FXML.
     * Creates a Stage for it and calls setStage(...) on the controller.
     */
    private boolean initSettingsView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SettingsView.fxml"));
            Parent root = loader.load();

            settingsView = loader.getController();

            Stage settingsStage = new Stage();
            settingsStage.setScene(new Scene(root));
            settingsView.setStage(settingsStage);

            return true;
        } catch (Exception e) {
            log.error("Failed to load SettingsView.fxml", e);
            return false;
        }
    }

    /**
     * Load and initialize OverlayView from FXML.
     * Creates a Stage for it and calls setStage(...) on the controller.
     */
    private boolean initOverlayView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/OverlayView.fxml"));
            loader.load();

            overlayView = loader.getController();


            return true;
        } catch (Exception e) {
            log.error("Failed to load OverlayView.fxml", e);
            return false;
        }
    }

    /**
     * Register all the keybind actions (Toggle Overlay, Stop Timer, etc.).
     */
    private void registerKeybindActions() {
        // Toggle Overlay
        keybindListener.registerAction("Toggle Overlay", () -> {
            boolean currentlyVisible = overlayModel.getOverlayVisible().get();
            Platform.runLater(() -> overlayModel.getOverlayVisible().set(!currentlyVisible));
        });

        // Start/Reset Smudge Timer
        keybindListener.registerAction("Start/Reset Smudge Timer", () ->
                Platform.runLater(() -> overlayController.startOrResetTimer(settingsModel.isCountUpTimer()))
        );

        // Stop Smudge Timer
        keybindListener.registerAction("Stop Smudge Timer", () ->
                Platform.runLater(overlayController::stopTimer)
        );

        // Toggle Crosshair
        keybindListener.registerAction("Toggle Crosshair", () -> {
            boolean currentlyEnabled = overlayModel.getCrosshairEnabled().get();
            Platform.runLater(() -> overlayModel.getCrosshairEnabled().set(!currentlyEnabled));
        });

        // Toggle Settings
        keybindListener.registerAction("Toggle Settings", () ->
                Platform.runLater(settingsView::toggleStage)
        );

        // Quit
        keybindListener.registerAction("Quit", Platform::exit);
    }
}
