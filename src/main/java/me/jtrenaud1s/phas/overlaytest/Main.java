package me.jtrenaud1s.phas.overlaytest;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import lombok.extern.slf4j.Slf4j;
import me.jtrenaud1s.phas.overlaytest.controller.SettingsController;
import me.jtrenaud1s.phas.overlaytest.keybind.KeybindListener;
import me.jtrenaud1s.phas.overlaytest.keybind.KeybindRecorder;
import me.jtrenaud1s.phas.overlaytest.model.SettingsModel;
import me.jtrenaud1s.phas.overlaytest.view.OverlayView;
import me.jtrenaud1s.phas.overlaytest.view.SettingsView;

import javax.swing.*;
import java.util.Set;

@Slf4j
public class Main {
    public static final String ACTION_TOGGLE_OVERLAY = "Toggle Overlay";
    public static final String ACTION_START_RESET_TIMER = "Start/Reset Smudge Timer";
    public static final String ACTION_STOP_TIMER = "Stop Smudge Timer";

    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SwingUtilities.invokeLater(() -> {
            SettingsModel model = new SettingsModel();
            SettingsView settingsView = new SettingsView();
            OverlayView overlayView = new OverlayView();
            overlayView.setVisible(false);

            try {
                GlobalScreen.registerNativeHook();
            } catch (NativeHookException e) {
                log.error("Failed to register native hook", e);
                return;
            }

            KeybindRecorder keybindRecorder = new KeybindRecorder();
            KeybindListener keybindListener = new KeybindListener();

            // Register actions
            keybindListener.registerAction(ACTION_TOGGLE_OVERLAY, () -> {
                SwingUtilities.invokeLater(() -> {
                    boolean isVisible = overlayView.isVisible();
                    overlayView.setVisible(!isVisible);
                    log.info("Overlay visibility toggled: {}", !isVisible);
                });
            });

            keybindListener.registerAction(ACTION_START_RESET_TIMER, () -> {
                SwingUtilities.invokeLater(() -> {
                    overlayView.getSmudgeTimerPanel().startOrResetTimer("None");
                    log.info("Smudge timer started/reset.");
                });
            });

            keybindListener.registerAction(ACTION_STOP_TIMER, () -> {
                SwingUtilities.invokeLater(() -> {
                    overlayView.getSmudgeTimerPanel().stopTimer();
                    log.info("Smudge timer stopped.");
                });
            });

            new SettingsController(model, settingsView, keybindRecorder, keybindListener);

            settingsView.setVisible(true);
        });
    }
}