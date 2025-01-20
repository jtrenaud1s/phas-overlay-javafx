package me.jtrenaud1s.phas.overlay.view;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.jtrenaud1s.phas.overlay.component.SmudgeTimerPane;
import me.jtrenaud1s.phas.overlay.util.WindowUtil;

@Slf4j
@Getter
public class OverlayView {
    private final Stage stage;
    @Getter
    private final SmudgeTimerPane smudgeTimerPane;
    private final Circle crosshairDot;

    public OverlayView() {
        this.stage = new Stage(StageStyle.TRANSPARENT);
        stage.setTitle("Phasmophobia Overlay");
        stage.setAlwaysOnTop(true);
        WindowUtil.timingDispatcher(stage);

        double width = Screen.getPrimary().getBounds().getWidth();
        double height = Screen.getPrimary().getBounds().getHeight();

        stage.setX(0);
        stage.setY(0);
        stage.setWidth(width);
        stage.setHeight(height);

        Pane root = new Pane();
        root.setPrefSize(width, height);
        root.setBackground(null); // fully transparent
        Scene scene = new Scene(root, width, height, Color.TRANSPARENT);
        stage.setScene(scene);

        crosshairDot = new Circle(3);  // radius 5
        crosshairDot.setFill(Color.LIMEGREEN);
        // Position center of the screen = (width/2, height/2) minus the radius offset
        crosshairDot.setLayoutX(width / 2.0);
        crosshairDot.setLayoutY(height / 2.0);

        // By default, hide it for now; we'll show/hide based on model
        crosshairDot.setVisible(false);

        smudgeTimerPane = new SmudgeTimerPane();
        root.getChildren().add(smudgeTimerPane);
        root.getChildren().add(crosshairDot);

        stage.hide();
    }

    public void showOverlay() {
        stage.show();
        WindowUtil.makeMouseTransparent(stage);
    }

    public void hideOverlay() {
        stage.hide();
    }

    public void setCrosshairVisible(boolean visible) {
        crosshairDot.setVisible(visible);
    }

    /**
     * Reposition/rescale the timer pane.
     */
    public void updateTimerPaneLayout(double scale) {
        double timerWidth = smudgeTimerPane.getLayoutBounds().getWidth();
        double timerHeight = smudgeTimerPane.getLayoutBounds().getHeight();
        double screenWidth = Screen.getPrimary().getBounds().getWidth();

        double scaledWidth = timerWidth * scale;
        double scaledHeight = timerHeight * scale;
        double extraWidth = scaledWidth - timerWidth;
        double extraHeight = scaledHeight - timerHeight;

        double marginRight = 10.0;
        double marginTop = 10.0;

        smudgeTimerPane.setScaleX(scale);
        smudgeTimerPane.setScaleY(scale);
        smudgeTimerPane.setLayoutX(screenWidth - scaledWidth - marginRight + (extraWidth / 2));
        smudgeTimerPane.setLayoutY(marginTop + (extraHeight / 2));
    }
}
