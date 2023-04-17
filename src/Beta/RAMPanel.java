package Beta;

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

public class RAMPanel extends BorderPane {
	//-----------------------------------------------------------------------
	private double ramUsageNowNum;

	Tile ramTile;

	private Background background;
	private BackgroundFill bFill;
	private BackgroundFill tileBFill;
	private Background tileBackground;
	//-----------------------------------------------------------------------
	private static final Label RAM_LABEL = new Label("RAM Status");
	private static final double TILE_SIZE = 250;
	//-----------------------------------------------------------------------

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
	 *Inicializace.
	 */
	public void init(GlobalMemory memoryGM) throws Exception {
		System.out.println("RAM Panel initialization...");
		bFill = new BackgroundFill(Color.valueOf("0x2a2a2aff"), null, null);
		background = new Background(bFill);

		tileBFill = new BackgroundFill(Color.valueOf("#1c1c1b"), new CornerRadii(5), null);
		tileBackground = new Background(tileBFill);

		RAM_LABEL.setFont(Font.font(30));
		RAM_LABEL.setTextFill(Color.WHITE);

		VBox ramPane = new VBox();

		double bilion = 1000000000.0;

		ramTile = new Tile(Tile.SkinType.CIRCULAR_PROGRESS);
		//ramTile.setMaxSize(TILE_SIZE, TILE_SIZE);
		ramTile.setPrefSize(TILE_SIZE, TILE_SIZE);
		/*
		ramTile.setTitle("RAM Usage (%)");
		ramTile.setTitleAlignment(TextAlignment.LEFT);
		*/
		ramTile.setMaxValue(memoryGM.getTotal()/bilion);
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
	                	ramUsageNowNum = getRamUsed(memoryGM)/bilion;
	                	ramTile.setValue(ramUsageNowNum);
	                	}
	            });
	        }
	    }, 0, 1000);

		ramPane.getChildren().addAll(RAM_LABEL, ramTile);
		ramPane.setPadding(new Insets(5));
		ramPane.setSpacing(5);
		ramPane.setAlignment(Pos.CENTER);
		ramPane.setBackground(tileBackground);

		setCenter(ramPane);
	}

	private static double getRamUsed(GlobalMemory memory) {
		return ( (memory.getTotal() - (memory.getAvailable())));
	}
}