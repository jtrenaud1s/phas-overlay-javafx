package me.jtrenaud1s.phas.overlay.component;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SmudgeProgressBar extends Region {
    private final Canvas canvas;
    private int value;
    private int maxValue;

    public SmudgeProgressBar(int min, int max) {
        this.value = min;
        this.maxValue = max;

        // We can make a canvas or just override Region’s layoutChildren().
        canvas = new Canvas(300, 20);
        getChildren().add(canvas);
        setPrefSize(300, 20);

        // Whenever we resize, repaint
        widthProperty().addListener((obs, oldVal, newVal) -> draw());
        heightProperty().addListener((obs, oldVal, newVal) -> draw());
    }

    public void setValue(int value) {
        this.value = Math.min(value, maxValue);
        draw();
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        draw();
    }

    private void draw() {
        double w = getWidth();
        double h = getHeight();
        canvas.setWidth(w);
        canvas.setHeight(h);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setGlobalAlpha(0.5);
        gc.clearRect(0, 0, w, h);

        gc.setFill(Color.GRAY);
        gc.fillRect(0, 0, w, h);

        double fraction = (double) value / (double) maxValue;
        double progressWidth = fraction * w;
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, progressWidth, h);

        double demonMark = ((120.0) / (double) maxValue) * w;
        double otherMark = (90.0 / (double) maxValue) * w;
        gc.setGlobalAlpha(1);
        gc.setStroke(Color.BLACK);
        gc.strokeLine(demonMark, 0, demonMark, h);
        gc.strokeLine(otherMark, 0, otherMark, h);
    }
}
