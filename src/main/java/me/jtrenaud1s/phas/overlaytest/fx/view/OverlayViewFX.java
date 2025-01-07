package me.jtrenaud1s.phas.overlaytest.fx.view;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class OverlayViewFX {
    private final Stage stage;
    @Getter
    private final SmudgeTimerPaneFX smudgeTimerPane;
    private final static String WINDOW_TITLE = "Phasmophobia Overlay";
    double timerScale = 1.2D;

    public OverlayViewFX() {
        this.stage = new Stage(StageStyle.TRANSPARENT);
        stage.setAlwaysOnTop(true);
        stage.setTitle(WINDOW_TITLE);


        // Fullscreen overlay bounds:
        double width = Screen.getPrimary().getBounds().getWidth();
        double height = Screen.getPrimary().getBounds().getHeight();

        stage.setX(0);
        stage.setY(0);
        stage.setWidth(width);
        stage.setHeight(height);

        // Container Pane
        Pane root = new Pane();
        root.setPrefSize(width, height);
        root.setBackground(javafx.scene.layout.Background.EMPTY);
        Scene scene = new Scene(root, width, height, Color.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);

        smudgeTimerPane = new SmudgeTimerPaneFX();

        root.getChildren().add(smudgeTimerPane);
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        root.setMouseTransparent(true);
    }

    // The overlay is initially hidden.
    public void showOverlay() {
        stage.show();
        makeMouseTransparent(stage);
        double timerWidth = smudgeTimerPane.getLayoutBounds().getWidth();
        double timerHeight = smudgeTimerPane.getLayoutBounds().getHeight();
        double screenWidth = Screen.getPrimary().getBounds().getWidth();

        double scaledWidth = timerWidth * timerScale;
        double scaledHeight = timerHeight * timerScale;

        double extraWidth = scaledWidth - timerWidth;
        double extraHeight = scaledHeight - timerHeight;
        log.info("Timer Width: {}", timerWidth);

        smudgeTimerPane.setScaleX(timerScale);
        smudgeTimerPane.setScaleY(timerScale);
        // Some margin from the right edge (so it's not flush against the screen border).
        double marginRight = 10.0;
        // Optional margin from top
        double marginTop = 10.0;

        // Original code used a width of ~360 for the smudge timer’s layout.
        // Now we scale it:

        log.info("Smudge Timer Pane width: {}", smudgeTimerPane.getWidth());
        log.info("Window width: {}", screenWidth);

        smudgeTimerPane.setLayoutX(screenWidth - scaledWidth - marginRight + (extraWidth / 2));
        smudgeTimerPane.setLayoutY(marginTop + (extraHeight / 2));
    }

    public void hideOverlay() {
        stage.hide();
    }

    public boolean isShowing() {
        return stage.isShowing();
    }

    private static void makeMouseTransparent(Stage stage) {
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, stage.getTitle());
        log.info("HWND: {}", hwnd);
        int wl = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE);
        log.info("Window Long: {}", wl);
        wl = wl | WinUser.WS_EX_LAYERED | WinUser.WS_EX_TRANSPARENT;
        User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, wl);
    }
}