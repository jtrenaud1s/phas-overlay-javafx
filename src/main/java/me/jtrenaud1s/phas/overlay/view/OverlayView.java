package me.jtrenaud1s.phas.overlay.view;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
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
        // 1) Create an invisible "owner" stage so the overlay won't have a taskbar icon.
        Stage ownerStage = new Stage(StageStyle.UTILITY);
        ownerStage.setOpacity(0);
        ownerStage.setWidth(1);
        ownerStage.setHeight(1);
        // Move it off-screen (optional, but keeps it completely hidden from view)
        ownerStage.setX(-10000);
        ownerStage.setY(-10000);
        ownerStage.show();

        // 2) Create the transparent overlay stage, owned by the invisible stage.
        this.stage = new Stage(StageStyle.TRANSPARENT);
        this.stage.initOwner(ownerStage);
        this.stage.initModality(Modality.NONE);
        stage.setTitle("Phasmophobia Overlay");
        stage.setAlwaysOnTop(true);

        // Custom WindowUtil logic (unchanged)
        WindowUtil.timingDispatcher(stage);

        // 3) Match the screen size so it covers the entire monitor.
        double width = Screen.getPrimary().getBounds().getWidth();
        double height = Screen.getPrimary().getBounds().getHeight();

        stage.setX(0);
        stage.setY(0);
        stage.setWidth(width);
        stage.setHeight(height);

        // 4) Create a root pane/scene with transparent fill.
        Pane root = new Pane();
        root.setPrefSize(width, height);
        root.setBackground(null);
        Scene scene = new Scene(root, width, height, Color.TRANSPARENT);
        stage.setScene(scene);

        // 5) Add a small crosshair dot in the center (invisible by default).
        crosshairDot = new Circle(3);
        crosshairDot.setFill(Color.LIMEGREEN);
        crosshairDot.setLayoutX(width / 2.0);
        crosshairDot.setLayoutY(height / 2.0);
        crosshairDot.setVisible(false);

        // 6) Your custom SmudgeTimerPane.
        smudgeTimerPane = new SmudgeTimerPane();

        // 7) Add both to the root.
        root.getChildren().add(smudgeTimerPane);
        root.getChildren().add(crosshairDot);

        // Initially hidden; showOverlay() below will reveal it.
        stage.hide();
    }

    public void showOverlay() {
        stage.show();
        // Makes the overlay non-interactive with mouse by default (if desired).
        WindowUtil.makeMouseTransparent(stage);
    }

    public void hideOverlay() {
        stage.hide();
    }

    public void setCrosshairVisible(boolean visible) {
        crosshairDot.setVisible(visible);
    }

    public void setSmudgeVolume(double volume) {
        smudgeTimerPane.setVolume(volume);
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
        smudgeTimerPane.setLayoutX(
                screenWidth - scaledWidth - marginRight + (extraWidth / 2)
        );
        smudgeTimerPane.setLayoutY(marginTop + (extraHeight / 2));
    }
}
