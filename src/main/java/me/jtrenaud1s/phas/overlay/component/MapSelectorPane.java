package me.jtrenaud1s.phas.overlay.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import lombok.Getter;

/**
 * A component that displays the currently selected map
 * in the overlay.
 */
@Getter
public class MapSelectorPane extends VBox {

    private final Label mapNameLabel;

    public MapSelectorPane() {
        setAlignment(Pos.CENTER);
        setSpacing(5.0);
        setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);");
        setPadding(new Insets(10));

        // Create map name label
        mapNameLabel = new Label("No Map Selected");
        mapNameLabel.setTextFill(Color.WHITE);
        mapNameLabel.setFont(Font.font("Arial", 16));

        getChildren().add(mapNameLabel);
    }

    /**
     * Updates the displayed map name.
     *
     * @param mapName The name of the selected map
     */
    public void setSelectedMap(String mapName) {
        if (mapName == null || mapName.isEmpty()) {
            mapNameLabel.setText("No Map Selected");
        } else {
            mapNameLabel.setText(mapName);
        }
    }
}
