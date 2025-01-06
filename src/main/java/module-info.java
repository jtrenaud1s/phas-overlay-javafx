module OverlayTest {
    requires java.datatransfer;
    requires java.desktop;
    requires com.github.kwhat.jnativehook;
    requires com.sun.jna.platform;
    requires static lombok;
    requires org.slf4j;
    requires com.fasterxml.jackson.databind;

    exports me.jtrenaud1s.phas.overlaytest.model;
    opens me.jtrenaud1s.phas.overlaytest.model to com.fasterxml.jackson.databind;
}