package me.jtrenaud1s.phas.overlay.keybind;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

@Slf4j
public class KeybindRecorder implements NativeKeyListener, NativeMouseInputListener {
    private final Set<String> currentKeys = new LinkedHashSet<>(); // Use LinkedHashSet to preserve order
    private boolean isRecording = false;
    private Consumer<String> onRecordingComplete;

    public KeybindRecorder() {
        try {
            GlobalScreen.addNativeKeyListener(this);
            GlobalScreen.addNativeMouseListener(this);
        } catch (Exception e) {
            log.error("Failed to register native hook", e);
        }
    }

    public void startRecording(Consumer<String> onRecordingComplete) {
        isRecording = true;
        currentKeys.clear();
        this.onRecordingComplete = onRecordingComplete;
    }

    public void stop() {
        isRecording = false;
    }

    private void stopRecording(String lastKey) {
        if (isRecording) {
            // Include the last released key in the chord
            currentKeys.add(lastKey);

            // Build the chord from the keys currently pressed
            String recordedChord = String.join(" + ", currentKeys);

            // Trigger the recording completion callback
            if (onRecordingComplete != null) {
                onRecordingComplete.accept(recordedChord);
            }

            stop(); // Explicitly stop recording
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if (!isRecording) return;

        int keyCode = e.getKeyCode();
        currentKeys.add(NativeKeyEvent.getKeyText(keyCode)); // Track pressed keys as readable text
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        if (!isRecording) return;

        int keyCode = e.getKeyCode();
        String keyText = NativeKeyEvent.getKeyText(keyCode);

        // Stop recording immediately upon key release and include the released key
        stopRecording(keyText);
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent e) {
        if (!isRecording) return;

        String mouseButton = "Mouse " + e.getButton(); // Use descriptive text for mouse buttons
        currentKeys.add(mouseButton);
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent e) {
        if (!isRecording) return;

        String mouseButton = "Mouse " + e.getButton();

        // Stop recording immediately upon mouse button release and include the button
        stopRecording(mouseButton);
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        // Not needed
    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent e) {
        // Not needed
    }
}