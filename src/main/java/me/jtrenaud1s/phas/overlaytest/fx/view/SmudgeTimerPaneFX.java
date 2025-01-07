package me.jtrenaud1s.phas.overlaytest.fx.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SmudgeTimerPaneFX extends VBox {
    private static final int TIMER_SPIRIT = 180; // seconds
    private static final int TIMER_DEMON  = 60;
    private static final int TIMER_OTHER  = 90;

    private final CustomProgressBarFX progressBar;
    private final Label timeRemainingLabel;
    private final Rectangle statusIndicator;
    private final Label ghostLabel;

    private Timeline timeline;
    private int remainingTime;
    private boolean isRunning;
    private boolean countUp;

    public SmudgeTimerPaneFX() {
        setAlignment(Pos.CENTER);
        setSpacing(5.0);
        setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);");
        setPadding(new Insets(10));

        progressBar = new CustomProgressBarFX(0, TIMER_SPIRIT);
        progressBar.setMouseTransparent(true);

        timeRemainingLabel = new Label("3:00");
        timeRemainingLabel.setTextFill(Color.WHITE);
        timeRemainingLabel.setFont(Font.font(18));

        statusIndicator = new Rectangle(10, 10);
        statusIndicator.setFill(Color.RED);
        statusIndicator.setStyle("-fx-background-color: red;");

        StackPane timeStack = new StackPane();
        timeStack.setAlignment(Pos.CENTER);
        timeStack.getChildren().addAll(timeRemainingLabel, statusIndicator);
        statusIndicator.setTranslateX(-25);

        ghostLabel = new Label("None");
        ghostLabel.setTextFill(Color.WHITE);
        ghostLabel.setFont(Font.font(18));

        getChildren().addAll(progressBar, timeStack, ghostLabel);
    }

    public void startOrResetTimer(boolean countUpTimer) {
        this.countUp = countUpTimer;
        resetTimer(TIMER_SPIRIT);
    }

    public void resetTimer(int timerDuration) {
        if (timeline != null) {
            timeline.stop();
        }
        remainingTime = timerDuration;
        isRunning = true;
        statusIndicator.setFill(Color.GREEN);
        updateGhostLabel();
        updateTimeDisplay();
        progressBar.setMaxValue(timerDuration);
        progressBar.setValue(timerDuration);

        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), evt -> {
                    if (remainingTime > 0) {
                        remainingTime--;
                        progressBar.setValue(remainingTime);
                        updateTimeDisplay();
                        updateGhostLabel();
                    } else {
                        stopTimer();
                    }
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void stopTimer() {
        if (timeline != null) {
            timeline.stop();
        }
        isRunning = false;
        statusIndicator.setFill(Color.RED);
        remainingTime = TIMER_SPIRIT;
        updateGhostLabel();
        updateTimeDisplay();
        progressBar.setMaxValue(TIMER_SPIRIT);
        progressBar.setValue(TIMER_SPIRIT);
    }

    private void updateTimeDisplay() {
        int time = countUp ? TIMER_SPIRIT - remainingTime : remainingTime;
        int minutes = time / 60;
        int seconds = time % 60;
        timeRemainingLabel.setText(String.format("%d:%02d", minutes, seconds));
    }

    private void updateGhostLabel() {
        if (remainingTime > TIMER_OTHER) {
            ghostLabel.setText("Spirit");
        } else if (remainingTime > TIMER_DEMON) {
            ghostLabel.setText("All Others");
        } else {
            ghostLabel.setText("Demon");
        }
    }

    public boolean isTimerRunning() {
        return isRunning;
    }
}
