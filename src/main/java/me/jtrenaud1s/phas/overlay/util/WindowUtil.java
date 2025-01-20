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
}
