package alpha;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Settings extends BorderPane {

    public Settings() {
        super();
        this.setPadding(new Insets(5));
        this.getStylesheets().add(Config.ACTIVE_STYLE_SHEET.get());
	    this.getStyleClass().add("settings");


        setCenter(settingsPane());
    }

    private Node settingsPane() {
        VBox settingsVB = new VBox();

        settingsVB.getStylesheets().add(Config.ACTIVE_STYLE_SHEET.get());
        settingsVB.getStyleClass().add("settings");

        HBox darkModeHB = new HBox();

        Label darkModeLabel = new Label("\nBright mode ");
        darkModeLabel.setFont(new Font("Helvetica", 20));
        darkModeLabel.setTextFill(Color.WHITE);

        Tile styleSwitchTile = TileBuilder.create()
                .maxSize(93, 93)
                .backgroundColor(Config.TILE_SECONDARY_COLOR)
                .skinType(Tile.SkinType.SWITCH)
                .build();


        styleSwitchTile.setOnSwitchPressed(e -> {
            if (Config.ACTIVE_STYLE_SHEET.get().equals(Config.DARK_STYLE_SHEET)) {
                Config.ACTIVE_STYLE_SHEET.set(Config.LIGHT_STYLE_SHEET);
                Config.setBrightStyle();
            } else {
                Config.ACTIVE_STYLE_SHEET.set(Config.DARK_STYLE_SHEET);
                Config.setDarkStyle();
            }
            this.getStylesheets().clear();
            this.getStylesheets().add(Config.ACTIVE_STYLE_SHEET.get());
            this.getStyleClass().add("settings");

            styleSwitchTile.setBackgroundColor(Config.TILE_SECONDARY_COLOR);

            settingsVB.getStylesheets().clear();
            settingsVB.getStylesheets().add(Config.ACTIVE_STYLE_SHEET.get());
            settingsVB.getStyleClass().add("settings");
        });

        darkModeHB.getChildren().addAll(darkModeLabel, styleSwitchTile);
        darkModeHB.setPadding(new Insets(5));

        darkModeHB.setAlignment(Pos.TOP_CENTER);

        Label uiLabel = new Label("UI\n__________________________________________________");
        uiLabel.setFont(new Font("Helvetica", 25));

        Label uptimeAndMeasuresLabel = new Label("Up-Time and measured values\n__________________________________________________");
        uptimeAndMeasuresLabel.setFont(new Font("Helvetica", 25));

        settingsVB.getChildren().addAll(uiLabel, darkModeHB, uptimeAndMeasuresLabel);

        settingsVB.setAlignment(Pos.TOP_CENTER);

        return settingsVB;
    }


}
