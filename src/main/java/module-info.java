module OverlayTest {
    requires java.desktop; // still needed by JNativeHook
    requires com.github.kwhat.jnativehook;
    requires com.sun.jna.platform;
    requires static lombok;
    requires org.slf4j;
    requires com.fasterxml.jackson.databind;

    // JavaFX modules
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires com.sun.jna;

    exports me.jtrenaud1s.phas.overlaytest.model;
    opens me.jtrenaud1s.phas.overlaytest.model to com.fasterxml.jackson.databind;

    exports me.jtrenaud1s.phas.overlaytest.fx;
    opens me.jtrenaud1s.phas.overlaytest.fx to javafx.fxml;
}