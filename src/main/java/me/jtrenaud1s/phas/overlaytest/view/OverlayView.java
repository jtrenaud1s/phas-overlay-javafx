package me.jtrenaud1s.phas.overlaytest.view;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

@Getter
public class OverlayView extends JFrame {
    private SmudgeTimerPanel smudgeTimerPanel;
    public OverlayView() {
        setAlwaysOnTop(true);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        setBounds(0, 0, gd.getDisplayMode().getWidth(), gd.getDisplayMode().getHeight());

        setLayout(null);

        this.smudgeTimerPanel = new SmudgeTimerPanel();
        smudgeTimerPanel.setBounds(getWidth() - 360, 10, 350, 200); // Position at the top-right
        add(new AlphaContainer(smudgeTimerPanel));
    }
}