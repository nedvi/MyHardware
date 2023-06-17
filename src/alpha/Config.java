package alpha;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

/**
 * Trida reprezentujici vizualni konfiguraci (CSS a barvy).
 * Casem bude sjednoceno pouze do CSS
 *
 * @author Dominik Nedved, A22B0109P
 * @version 2023.05.11
 */
public class Config {
    //========================== Konfigurace CSS =============================
    public static final String DARK_STYLE_SHEET = "styles.css";
    public static String ACTIVE_STYLE_SHEET = DARK_STYLE_SHEET;

    //========================= Konfigurace pozadi ==========================
    public static final BackgroundFill BACKGROUND_FILL = new BackgroundFill(Color.valueOf("0x2a2a2aff"), null, null);
    public static final Background BACKGROUND = new Background(BACKGROUND_FILL);

    //===================== Konfigurace pozadi tlacitek =====================
    public static final BackgroundFill BTN_FILL = new BackgroundFill(Color.valueOf("#525252"), new CornerRadii(2), new Insets(1));
    public static final Background BTN_BACKGROUND = new Background(BTN_FILL);
}
