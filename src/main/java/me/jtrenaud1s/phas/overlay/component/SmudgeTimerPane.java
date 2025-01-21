package me.jtrenaud1s.phas.overlay.component;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SmudgeTimerPane extends VBox {

    private static final int TIMER_SPIRIT = 180;
    public static final int TIMER_DEMON = 120;
    public static final int TIMER_STANDARD = 90;

    private final SmudgeProgressBar progressBar;
    private final Label timeRemainingLabel;
    private final Rectangle statusIndicator;
    private final Label ghostLabel;
    private final AudioClip cueSound;

    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> scheduledFuture;

    private int remainingTime;
    private boolean isRunning;
    private boolean countUp;

    public SmudgeTimerPane() {
        setAlignment(Pos.CENTER);
        setSpacing(5.0);
        setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);");
        setPadding(new Insets(10));

        // Prepare the audio clip
        URL audioUrl = Objects.requireNonNull(getClass().getResource("/audio/countdown.mp3"));
        cueSound = new AudioClip(audioUrl.toExternalForm());
        cueSound.setVolume(1.0);
        cueSound.setCycleCount(1);

        // Create progress bar
        progressBar = new SmudgeProgressBar(0, TIMER_SPIRIT);
        progressBar.setMouseTransparent(true);

        // Create label for time
        timeRemainingLabel = new Label("3:00");
        timeRemainingLabel.setTextFill(Color.WHITE);
        timeRemainingLabel.setFont(Font.font("Arial", 16));

        // Create status indicator
        statusIndicator = new Rectangle(10, 10);
        statusIndicator.setFill(Color.RED);
        statusIndicator.setStyle("-fx-background-color: red;");

        // Stack for time label + small status indicator
        StackPane timeStack = new StackPane();
        timeStack.setAlignment(Pos.CENTER);
        timeStack.getChildren().addAll(timeRemainingLabel, statusIndicator);
        statusIndicator.setTranslateX(-25);

        // Create label for ghost type
        ghostLabel = new Label("None");
        ghostLabel.setTextFill(Color.WHITE);
        ghostLabel.setFont(Font.font("Arial", 16));

        getChildren().addAll(progressBar, timeStack, ghostLabel);

        // Create a single-thread scheduler for timer tasks
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Start the timer. If {@code countUpTimer} is true, the displayed time counts up
     * (although 'remainingTime' logic remains the same internally).
     */
    public void startOrResetTimer(boolean countUpTimer) {
        this.countUp = countUpTimer;
        resetTimer(TIMER_SPIRIT);
    }

    public void setVolume(double volume) {
        cueSound.setVolume(volume);  // Range 0.0 - 1.0
    }

    /**
     * Reset the timer to a certain duration and start counting down immediately.
     */
    public void resetTimer(int timerDuration) {
        // Cancel any existing scheduled task
        cancelScheduledTask();

        remainingTime = timerDuration;
        isRunning = true;
        statusIndicator.setFill(Color.GREEN);
        updateGhostLabel();
        updateTimeDisplay();

        progressBar.setMaxValue(timerDuration);
        progressBar.setValue(timerDuration);

        cueSound.stop();

        // Schedule a task to run every second
        scheduledFuture = scheduler.scheduleAtFixedRate(() -> {
            if (remainingTime > 0) {
                remainingTime--;
                Platform.runLater(() -> {
                    progressBar.setValue(remainingTime);
                    updateTimeDisplay();
                    updateGhostLabel();
                    checkAudioCue();
                });

            } else {
                stopTimer();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * Plays the countdown cue sound at specific times.
     */
    private void checkAudioCue() {
        if (remainingTime == 5 || remainingTime == 95 || remainingTime == 125) {
            cueSound.play();
        }
    }

    /**
     * Stops the timer and resets everything.
     */
    public void stopTimer() {
        cancelScheduledTask();

        isRunning = false;
        statusIndicator.setFill(Color.RED);
        remainingTime = TIMER_SPIRIT;
        cueSound.stop();

        Platform.runLater(() -> {
            updateGhostLabel();
            updateTimeDisplay();
            progressBar.setMaxValue(TIMER_SPIRIT);
            progressBar.setValue(TIMER_SPIRIT);
        });
    }

    /**
     * Cleanly cancels any existing scheduled timer task.
     */
    private void cancelScheduledTask() {
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(false);
        }
    }

    /**
     * Update the time label based on whether we're counting up or down.
     */
    private void updateTimeDisplay() {
        int time = countUp ? TIMER_SPIRIT - remainingTime : remainingTime;
        int minutes = time / 60;
        int seconds = time % 60;
        timeRemainingLabel.setText(String.format("%d:%02d", minutes, seconds));
    }

    /**
     * Update the ghost-label text based on how much time remains.
     */
    private void updateGhostLabel() {
        if (remainingTime > TIMER_DEMON) {
            ghostLabel.setText("None");
        } else if (remainingTime > TIMER_STANDARD) {
            ghostLabel.setText("Demon");
        } else if (remainingTime > 0) {
            ghostLabel.setText("Standard");
        } else {
            ghostLabel.setText("Spirit");
        }
    }

    public boolean isTimerRunning() {
        return isRunning;
    }

    /**
     * Optionally call this when the pane is no longer needed.
     */
    public void shutdownScheduler() {
        cancelScheduledTask();
        scheduler.shutdownNow();
    }
}
