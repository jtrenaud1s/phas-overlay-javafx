module PhasOverlay {
    // ------------------------------------------------------------
    // Required modules
    // ------------------------------------------------------------
    requires ch.qos.logback.classic;
    requires com.fasterxml.jackson.databind;
    requires com.sun.jna;
    requires com.sun.jna.platform;
    requires java.desktop;     // still needed by JNativeHook
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires org.slf4j;
    requires static lombok;
    requires com.github.kwhat.jnativehook;

    // ------------------------------------------------------------
    // Public exports
    // ------------------------------------------------------------

    exports me.jtrenaud1s.phas.overlay.keybind;
    exports me.jtrenaud1s.phas.overlay.model;
    exports me.jtrenaud1s.phas.overlay.util to com.github.kwhat.jnativehook;
    exports me.jtrenaud1s.phas.overlay to com.github.kwhat.jnativehook, javafx.graphics;
    // ------------------------------------------------------------
    // Open packages for reflection / FXML / serialization
    // ------------------------------------------------------------
    opens me.jtrenaud1s.phas.overlay.model to com.fasterxml.jackson.databind;
    opens me.jtrenaud1s.phas.overlay.view to javafx.fxml;
    opens me.jtrenaud1s.phas.overlay.component to javafx.fxml;
}