package gui;

import java.util.Timer;
import java.util.TimerTask;

import eu.hansolo.tilesfx.Tile;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;

/**
 * Trida reprezentujci RAM panel v GUI.
 * Vizualizace zateze.
 *
 * @author Dominik Nedved
 * @version 2023.06.26
 */
public class RAMPanel extends BorderPane {

	//===================== Data =====================

	private double ramUsageNowNum;
	final double BILION = 1073741824.0;

	//===================== Tiles =====================

	private Tile ramTile;
	private BackgroundFill tileBFill;
	private Background tileBackground;
	private final double TILE_SIZE = 250;

	//===================== Nodes =====================

	private VBox ramPane;
	private final Label RAM_LABEL = new Label("RAM Status");

	//===============================================================

	/**
	 * Konstruktor
	 *
	 * @param si predana instance SystemInfa
	 */
	public RAMPanel(SystemInfo si) {
		super();
		try {
			init(si.getHardware().getMemory());
		} catch (Exception e) {
			System.out.println("RAM Panel init failed!");
			e.printStackTrace();
		}
	}

	/**
	 * Inicializace
	 */
	public void init(GlobalMemory memoryGM) {
		System.out.println("RAM Panel initialization...");

		tileBFill = new BackgroundFill(Color.valueOf("#1c1c1b"), new CornerRadii(5), null);
		tileBackground = new Background(tileBFill);

		RAM_LABEL.setFont(Font.font(30));
		RAM_LABEL.setTextFill(Color.WHITE);

		ramPane = new VBox();

		ramTile = new Tile(Tile.SkinType.CIRCULAR_PROGRESS);
		ramTile.setPrefSize(500, TILE_SIZE);
		ramTile.setMaxValue(memoryGM.getTotal()/ BILION);
		ramTile.setThresholdColor(Color.RED);
		ramTile.setThresholdVisible(true);
		ramTile.setThreshold(10);
		ramTile.setLowerThresholdColor(Color.RED);
		ramTile.setLowerThresholdVisible(true);
		ramTile.setLowerThreshold(10);
		ramTile.setUnit(" GB");
		ramTile.setUnitColor(Color.WHITE);

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				javafx.application.Platform.runLater(new Runnable() {
					@Override
					public void run() {
						ramUsageNowNum = getRamUsed(memoryGM)/ BILION;
						ramTile.setValue(ramUsageNowNum);
					}
				});
			}
		}, 0, 1000);

		ramPane.getChildren().addAll(RAM_LABEL, ramTile);
		ramPane.setPadding(new Insets(10));
		ramPane.setSpacing(5);
		ramPane.setAlignment(Pos.CENTER);
		ramPane.setBackground(tileBackground);

		setCenter(ramPane);
	}

	/**
	 * @param memory
	 * @return aktualni zatez ram
	 */
	private static double getRamUsed(GlobalMemory memory) {
		return ( (memory.getTotal() - (memory.getAvailable())));
	}

	/**
	 * Provede refresh stylu.
	 * Vola se pokazde pri prepnuti konkretniho panelu.
	 */
	public void refreshStyleSheet() {

		tileBackground = Config.TILE_BACKGROUND;
		ramPane.setBackground(tileBackground);
		ramPane.setBackground(tileBackground);
		RAM_LABEL.setTextFill(Config.TEXT_COLOR);

		ramTile.setBackgroundColor(Config.TILE_SECONDARY_COLOR);
		ramTile.setValueColor(Config.TEXT_COLOR);
		ramTile.setTitleColor(Config.TEXT_COLOR);
		ramTile.setBarBackgroundColor(Config.TILE_COLOR);
		ramTile.setUnitColor(Config.TEXT_COLOR);
	}
}