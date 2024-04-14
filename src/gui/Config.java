package gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

/**
 * Trida reprezentujici vizualni konfiguraci (CSS a barvy).
 * Casem bude sjednoceno pouze do CSS
 *
 * @author Dominik Nedved
 * @version 2023.06.26
 */
public class Config {

    //========================== Konfigurace CSS =============================

    public static final String DARK_STYLE_SHEET = "DarkStyle.css";
    public static final String LIGHT_STYLE_SHEET = "LightStyle.css";
    public static SimpleStringProperty ACTIVE_STYLE_SHEET = new SimpleStringProperty(DARK_STYLE_SHEET);

    //========================= Konfigurace pozadi ==========================

    public static BackgroundFill BACKGROUND_FILL = new BackgroundFill(Color.valueOf("0x2a2a2aff"), null, null);
    public static Background BACKGROUND = new Background(BACKGROUND_FILL);
    public static Background DASHBOARD_BACKGROUND = new Background(BACKGROUND_FILL);

    //===================== Konfigurace pozadi tlacitek =====================

    public static BackgroundFill BTN_FILL = new BackgroundFill(Color.valueOf("#525252"), new CornerRadii(2), new Insets(1));
    public static Background BTN_BACKGROUND = new Background(BTN_FILL);

    //========================= Konfigurace tiles ==========================

    public static Color TILE_COLOR = Color.valueOf("#1c1c1b");
    public static BackgroundFill tileBFill = new BackgroundFill(TILE_COLOR, new CornerRadii(5), null);
    public static Background TILE_BACKGROUND = new Background(tileBFill);
    public static Color TILE_SECONDARY_COLOR = Color.valueOf("0x2a2a2aff");
    public static Color TEXT_COLOR = Color.WHITE;

    /**
     * Nastavi konfiguraci na tmavy motiv
     */
    public static void setDarkStyle() {
        // Konfigurace pozadi
        BACKGROUND_FILL = new BackgroundFill(Color.valueOf("0x2a2a2aff"), null, null);
        BACKGROUND = new Background(BACKGROUND_FILL);
        DASHBOARD_BACKGROUND = new Background(BACKGROUND_FILL);

        // Konfigurace pozadi tlacitek
        BTN_FILL = new BackgroundFill(Color.valueOf("#525252"), new CornerRadii(2), new Insets(1));
        BTN_BACKGROUND = new Background(BTN_FILL);

        // Konfigurace tiles
        TILE_COLOR = Color.valueOf("#1c1c1b");
        tileBFill = new BackgroundFill(TILE_COLOR, new CornerRadii(5), null);
        TILE_BACKGROUND = new Background(tileBFill);

        TILE_SECONDARY_COLOR = Color.valueOf("0x2a2a2aff");

        TEXT_COLOR = Color.WHITE;
    }

    /**
     * Nastavi konfiguraci na svetly motiv
     */
    public static void setBrightStyle() {
        // Konfigurace pozadi
        BACKGROUND_FILL = new BackgroundFill(Color.LIGHTGRAY, null, null);
        BACKGROUND = new Background(BACKGROUND_FILL);
        DASHBOARD_BACKGROUND = new Background(new BackgroundFill(Color.WHITE, null, null));

        // Konfigurace pozadi tlacitek
        BTN_FILL = new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(2), new Insets(1));
        BTN_BACKGROUND = new Background(BTN_FILL);

        // Konfigurace tiles
        TILE_COLOR = Color.LIGHTGRAY;
        tileBFill = new BackgroundFill(TILE_COLOR, new CornerRadii(5), null);
        TILE_BACKGROUND = new Background(tileBFill);

        TILE_SECONDARY_COLOR = Color.WHITE;

        TEXT_COLOR = Color.BLACK;
    }

}
