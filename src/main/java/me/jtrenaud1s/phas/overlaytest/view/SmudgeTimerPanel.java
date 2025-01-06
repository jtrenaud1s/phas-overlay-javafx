package me.jtrenaud1s.phas.overlaytest.view;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Slf4j
public class SmudgeTimerPanel extends JPanel {

    private static final int TIMER_SPIRIT = 180; // seconds
    private static final int TIMER_DEMON = 60; // seconds
    private static final int TIMER_OTHER = 90; // seconds

    private final CustomProgressBar progressBar;
    private final JLabel timeRemainingLabel;
    private final JLabel statusLabel;
    private final JLabel ghostLabel;

    private javax.swing.Timer timer;
    private int remainingTime;
    private boolean isRunning;

    public SmudgeTimerPanel() {
        setLayout(new GridBagLayout());
        //setBackground(new Color(50, 50, 50, 150)); // Transparent gray background

        progressBar = new CustomProgressBar(0, TIMER_SPIRIT);

        timeRemainingLabel = new JLabel("3:00");
        timeRemainingLabel.setForeground(Color.WHITE);
        timeRemainingLabel.setBackground(new Color(0, 0, 0, 0));
        timeRemainingLabel.setFont(timeRemainingLabel.getFont().deriveFont(18f));

        statusLabel = new JLabel();
        statusLabel.setOpaque(true);
        statusLabel.setBackground(Color.RED);
        statusLabel.setPreferredSize(new Dimension(10, 10));

        ghostLabel = new JLabel("None");
        ghostLabel.setForeground(Color.WHITE);
        ghostLabel.setFont(ghostLabel.getFont().deriveFont(18f));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(progressBar, gbc);

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statusPanel.setOpaque(false);
        statusPanel.add(statusLabel);
        statusPanel.add(timeRemainingLabel);
        add(statusPanel, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(ghostLabel, gbc);

        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
    }

    public void resetTimer( int timerDuration ) {
        if( timer != null ) {
            timer.stop();
        }
        remainingTime = timerDuration;
        isRunning = true;
        updateGhostLabel();
        updateTimeDisplay();
        progressBar.setMaximum( timerDuration );
        progressBar.setValue( timerDuration );
        statusLabel.setBackground( Color.GREEN );

        timer = new javax.swing.Timer( 1000, e -> {

            if( remainingTime > 0 ) {
                remainingTime --;
                progressBar.setValue( remainingTime );
                updateTimeDisplay();
                updateGhostLabel();
            }
            else {
                stopTimer();
            }
        } );
        timer.start();
    }

    public void stopTimer() {
        if( timer != null ) {
            timer.stop();
        }
        isRunning = false;
        statusLabel.setBackground( Color.RED );
    }

    public void startOrResetTimer(String ghostType) {
        resetTimer(TIMER_SPIRIT);
    }

    private void updateTimeDisplay() {
        int minutes = remainingTime / 60;
        int seconds = remainingTime % 60;
        timeRemainingLabel.setText(String.format("%d:%02d", minutes, seconds));
        timeRemainingLabel.setBackground(new Color(0, 0, 0, 0));
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

    @Slf4j
    private static class CustomProgressBar extends JComponent {
        private int value;
        private int maximum;

        public CustomProgressBar(int min, int max) {
            this.value = min;
            this.maximum = max;
            setPreferredSize(new Dimension(300, 20));
            setMinimumSize(new Dimension(300, 20));
        }

        public void setValue(int value) {
            this.value = Math.min(value, maximum);
            repaint();
        }

        public void setMaximum(int maximum) {
            this.maximum = maximum;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            log.info("Width: {}, Height: {}", width, height);

            // Draw background
            g2d.setColor(Color.GRAY);
            g2d.fillRect(0, 0, width, height);

            // Draw progress
            int progressWidth = (int) ((value / (double) maximum) * width);
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, progressWidth, height);

            // Draw tick marks
            g2d.setColor(Color.BLACK);
            int demonMark = (int) (((TIMER_SPIRIT - TIMER_DEMON) / (double) maximum) * width);
            int otherMark = (int) ((TIMER_OTHER / (double) maximum) * width);

            g2d.drawLine(demonMark, 0, demonMark, height);
            g2d.drawLine(otherMark, 0, otherMark, height);
        }
    }

    // Test the SmudgeTimerPanel in its own JFrame
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Smudge Timer Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(350, 200);

            SmudgeTimerPanel timerPanel = new SmudgeTimerPanel();
            frame.add(new AlphaContainer(timerPanel));
            frame.setVisible(true);
        });
    }
}
