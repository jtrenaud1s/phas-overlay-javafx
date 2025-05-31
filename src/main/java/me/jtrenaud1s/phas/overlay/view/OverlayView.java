package me.jtrenaud1s.phas.overlay.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import me.jtrenaud1s.phas.overlay.component.MapSelectorPane;
import me.jtrenaud1s.phas.overlay.component.SmudgeTimerPane;
import me.jtrenaud1s.phas.overlay.util.WindowUtil;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
@Getter
public class OverlayView implements Initializable {

    @FXML private Pane root;                // The FXML root Pane
    @FXML private SmudgeTimerPane smudgeTimerPane;
    @FXML private MapSelectorPane mapSelectorPane;
    @FXML private Circle crosshairDot;

    private Stage stage;                   // The overlay stage
    private Stage ownerStage;              // The invisible owner stage

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initOverlay();
    }

    /**
     * Call this after loading FXML to set up the stage with your overlay logic.
     */
    public void initOverlay() {
        this.stage = new Stage();

        ownerStage = new Stage(StageStyle.UTILITY);
        ownerStage.setOpacity(0);
        ownerStage.setWidth(1);
        ownerStage.setHeight(1);
        ownerStage.setX(-10000);
        ownerStage.setY(-10000);
        ownerStage.show();

        this.stage.initOwner(ownerStage);
        this.stage.initStyle(StageStyle.TRANSPARENT);
        this.stage.initModality(Modality.NONE);
        this.stage.setTitle("Phasmophobia Overlay");
        this.stage.setAlwaysOnTop(true);

        double width = Screen.getPrimary().getBounds().getWidth();
        double height = Screen.getPrimary().getBounds().getHeight();

        Scene scene = new Scene(root, width, height, Color.TRANSPARENT);
        this.stage.setScene(scene);
        root.setPrefSize(width, height);

        crosshairDot.setCenterX(width / 2.0);
        crosshairDot.setCenterY(height / 2.0);

        // Initialize layout for map selector
        updateOverlayLayout(1.0);

        WindowUtil.resizeOverlayToPhasmophobia(this.stage);
        this.stage.hide();
        WindowUtil.makeMouseTransparent(this.stage);
    }

    public void showOverlay() {
        if (stage != null) {
            stage.show();
            WindowUtil.makeMouseTransparent(stage);
        }
    }

    public void hideOverlay() {
        if (stage != null) {
            stage.hide();
        }
    }

    public void setCrosshairVisible(boolean visible) {
        if (crosshairDot != null) {
            crosshairDot.setVisible(visible);
        }
    }

    public void setSmudgeVolume(double volume) {
        if (smudgeTimerPane != null) {
            smudgeTimerPane.setVolume(volume);
        }
    }

    /**
     * Updates the selected map displayed in the map selector.
     */
    public void setSelectedMap(String mapName) {
        if (mapSelectorPane != null) {
            mapSelectorPane.setSelectedMap(mapName);
        }
    }

    /**
     * Shows or hides the map selector based on settings.
     */
    public void setMapSelectorVisible(boolean visible) {
        if (mapSelectorPane != null) {
            mapSelectorPane.setVisible(visible);
        }
    }

    /**
     * Reposition/rescale the timer pane.
     */
    public void updateOverlayLayout(double scale) {
        if (smudgeTimerPane == null) return;
        if (mapSelectorPane == null) return;


        double timerWidth = smudgeTimerPane.getLayoutBounds().getWidth();
        double timerHeight = smudgeTimerPane.getLayoutBounds().getHeight();

        double screenWidth = Screen.getPrimary().getBounds().getWidth();

        double scaledWidth = timerWidth * scale;
        double scaledHeight = timerHeight * scale;
        double extraWidth = scaledWidth - timerWidth;
        double extraHeight = scaledHeight - timerHeight;

        double marginRight = 10.0;
        double marginTop = 10.0;

        mapSelectorPane.setMinWidth(timerWidth);

        smudgeTimerPane.setScaleX(scale);
        smudgeTimerPane.setScaleY(scale);
        mapSelectorPane.setScaleX(scale);
        mapSelectorPane.setScaleY(scale);
        smudgeTimerPane.setLayoutX(screenWidth - scaledWidth - marginRight + (extraWidth / 2));
        smudgeTimerPane.setLayoutY(marginTop + (extraHeight / 2));
        mapSelectorPane.setLayoutX(screenWidth - scaledWidth - marginRight + (extraWidth / 2));
        mapSelectorPane.setLayoutY(smudgeTimerPane.getLayoutY() + timerHeight * scale + marginTop);
    }
}
