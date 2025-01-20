package me.jtrenaud1s.phas.overlay.keybind;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class KeybindListener implements NativeKeyListener, NativeMouseInputListener {
    private static final long COOLDOWN_PERIOD_MS = 500;

    private final Map<String, Runnable> actions = new HashMap<>();
    private final Map<Set<String>, String> keybindToAction = new HashMap<>();
    private final Map<Set<String>, Long> lastActivationTime = new HashMap<>();
    private final Set<String> currentKeys = new LinkedHashSet<>();
    private boolean paused = false;

    public KeybindListener() {
        try {
            GlobalScreen.addNativeKeyListener(this);
            GlobalScreen.addNativeMouseListener(this);
        } catch (Exception e) {
            log.error("Failed to register native hook", e);
        }
    }

    public void registerAction(String actionName, Runnable action) {
        actions.put(actionName, action);
    }

    public void associateKeybind(Set<String> keybind, String actionName) {
        keybindToAction.put(keybind, actionName);
    }

    public void clearKeybinds() {
        keybindToAction.clear();
        lastActivationTime.clear();
    }

    public void pause() {
        paused = true;
        currentKeys.clear();
    }

    public void resume() {
        paused = false;
    }

    private void checkKeybinds() {
        if (paused) return;

        Set<String> bestMatch = null;
        String bestAction = null;

        // Find the closest match by length of the keybind set
        for (Map.Entry<Set<String>, String> entry : keybindToAction.entrySet()) {
            Set<String> keybind = entry.getKey();
            String actionName = entry.getValue();

            if (currentKeys.containsAll(keybind)) {
                if (bestMatch == null || keybind.size() > bestMatch.size()) {
                    bestMatch = keybind;
                    bestAction = actionName;
                }
            }
        }

        if (bestMatch != null) {
            long currentTime = System.currentTimeMillis();
            Long lastTime = lastActivationTime.get(bestMatch);

            if (lastTime == null || currentTime - lastTime >= COOLDOWN_PERIOD_MS) {
                lastActivationTime.put(bestMatch, currentTime);

                Runnable action = actions.get(bestAction);
                if (action != null) {
                    log.info("Action triggered: {}", bestAction);
                    action.run();
                }
            } else {
                log.debug("Keybind {} is on cooldown", bestMatch);
            }
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if (paused) return;

        String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
        currentKeys.add(keyText);
        checkKeybinds();
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        if (paused) return;

        String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
        currentKeys.remove(keyText);
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent e) {
        if (paused) return;

        String mouseButton = "Mouse " + e.getButton();
        currentKeys.add(mouseButton);
        checkKeybinds();
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent e) {
        if (paused) return;

        String mouseButton = "Mouse " + e.getButton();
        currentKeys.remove(mouseButton);
    }
}
