package me.jtrenaud1s.phas.overlay.util;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import javafx.event.Event;
import javafx.event.EventDispatcher;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WindowUtil {
    public static void makeMouseTransparent(Stage stage) {
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, stage.getTitle());
        if (hwnd != null) {
            int wl = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE);
            wl = wl | WinUser.WS_EX_LAYERED | WinUser.WS_EX_TRANSPARENT;
            User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, wl);
        }
    }

    /**
     * Attempts to find the "Phasmophobia" window by title and resize the given Stage to match it.
     * If the window is not found, logs a warning and does nothing.
     */
    public static void resizeOverlayToPhasmophobia(Stage overlayStage) {
        // Attempt to find the game window by its exact title
        WinDef.HWND phasmoHwnd = User32.INSTANCE.FindWindow(null, "Phasmophobia");

        if (phasmoHwnd == null) {
            log.warn("Could not find a window with title 'Phasmophobia'.");
            return;
        }

        // Get the bounding rectangle of the window
        WinDef.RECT rect = new WinDef.RECT();
        User32.INSTANCE.GetWindowRect(phasmoHwnd, rect);

        int windowLeft   = rect.left;
        int windowTop    = rect.top;
        int windowRight  = rect.right;
        int windowBottom = rect.bottom;

        int windowWidth  = windowRight - windowLeft;
        int windowHeight = windowBottom - windowTop;

        // If for some reason the width/height are invalid, bail out
        if (windowWidth <= 0 || windowHeight <= 0) {
            log.warn("Phasmophobia window rect has invalid width/height: width={}, height={}",
                    windowWidth, windowHeight);
            return;
        }

        // Reposition and resize the overlay stage
        overlayStage.setX(windowLeft);
        overlayStage.setY(windowTop);
        overlayStage.setWidth(windowWidth);
        overlayStage.setHeight(windowHeight);

        log.info("Overlay resized to match Phasmophobia window: ({}x{} at {},{})",
                windowWidth, windowHeight, windowLeft, windowTop);
    }

    public static void timingDispatcher(Stage stage) {
        final EventDispatcher eventDispatcher = stage.getEventDispatcher();  // the original dispatcher

        stage.setEventDispatcher((event, tail) -> {
            long millis = System.currentTimeMillis();

            Event returnedEvent = eventDispatcher.dispatchEvent(event, tail);  // let original one handle it as usual

            millis = System.currentTimeMillis() - millis;

            if(millis >= 100) {  // check if it was slow
                log.info("[WARN] Slow Event Handling: {} ms for event: {}", millis, event);
            }

            return returnedEvent;
        });
    }

    public static void forceFocus(Stage stage) {
        // Attempt to find the window handle by the stage's title
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, stage.getTitle());
        if (hwnd == null) {
            log.warn("Could not find HWND for stage titled '{}'; cannot force focus.", stage.getTitle());
            return;
        }

        // Bring the window up (show if minimized, etc.)
        User32.INSTANCE.ShowWindow(hwnd, WinUser.SW_SHOWNORMAL);

        // Attempt to set this window to foreground
        boolean result = User32.INSTANCE.SetForegroundWindow(hwnd);
        if (!result) {
            log.warn("SetForegroundWindow failed (foreground lock may be active).");
        } else {
            log.info("Window '{}' was brought to foreground.", stage.getTitle());
        }
    }
}
