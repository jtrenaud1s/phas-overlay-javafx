module PhasOverlay {
    requires java.desktop; // still needed by JNativeHook
    requires com.github.kwhat.jnativehook;
    requires com.sun.jna.platform;
    requires static lombok;
    requires org.slf4j;
    requires com.fasterxml.jackson.databind;

    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.media;
    requires javafx.fxml;
    requires com.sun.jna;
    requires ch.qos.logback.classic;

    exports me.jtrenaud1s.phas.overlay.model;
    opens me.jtrenaud1s.phas.overlay.model to com.fasterxml.jackson.databind;

    exports me.jtrenaud1s.phas.overlay;
    opens me.jtrenaud1s.phas.overlay.view to javafx.fxml;
    opens me.jtrenaud1s.phas.overlay.component to javafx.fxml;

    exports me.jtrenaud1s.phas.overlay.util to com.github.kwhat.jnativehook;
}