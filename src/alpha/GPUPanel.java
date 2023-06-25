package alpha;

import alpha.dataModel.LoadAndTemp;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Trida reprezentujci GPU panel v GUI.
 * Vizualizace zateze a teploty.
 *
 * @author Dominik Nedved, A22B0109P
 * @version 2023.05.21
 */
public class GPUPanel extends BorderPane {

	private final LoadAndTemp loadAndTemp;

	private static final Label GPU_LABEL = new Label("GPU Status");
	private static final double TILE_SIZE = 250;
	private Background tileBackground;
	private Tile gpuLoadTile;

	private Gauge gpuTempGauge;
	private Tile gpuTempTile;

	private VBox gpuPaneVB;
	private HBox gpuStatsHB;

	//=====================================================================

	/**
	 * Konstruktor
	 *
	 * @param loadAndTemp refernce na LoadANdTemp
	 */
	GPUPanel(LoadAndTemp loadAndTemp) {
		super();
		this.loadAndTemp = loadAndTemp;
		try {
			init();
		} catch (Exception e) {
			System.out.println("GPU Panel init failed!");
			e.printStackTrace();
		}
	}

	/**
	 * Inicializace GPU panelu
	 */
	public void init() {
		System.out.println("GPU Panel initialization...");

		tileBackground = new Background(Config.tileBFill);

		GPU_LABEL.setFont(Font.font(30));
		GPU_LABEL.setTextFill(Color.WHITE);

		gpuPaneVB = new VBox();
		gpuStatsHB = new HBox();


		gpuLoadTile = TileBuilder.create()
				.skinType(Tile.SkinType.GAUGE)
				.title("GPU Usage (%)")
				.needleColor(Color.valueOf("0x37b3fcff"))
		 		.thresholdColor(Color.RED)
				.threshold(90)
				.build();

		gpuTempGauge = createGpuTempGauge();

		gpuTempTile = TileBuilder.create()
				.prefSize(TILE_SIZE, TILE_SIZE)
				.skinType(Tile.SkinType.CUSTOM)
				.title("GPU Temperature")
				.unit("\u00b0C")
				.text("")
				.graphic(gpuTempGauge)
				.build();


		DoubleProperty gpuLoad = new SimpleDoubleProperty(0);
		gpuLoad.bind(loadAndTemp.gpuLoadProperty());
		loadAndTemp.gpuLoadProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				gpuLoadTile.setValue(gpuLoad.get());
			}
		});

		DoubleProperty gpuTemp = new SimpleDoubleProperty(0);
		gpuTemp.bind(loadAndTemp.gpuTempProperty());
		loadAndTemp.gpuTempProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				gpuTempGauge.setValue(gpuTemp.get());
			}
		});

		gpuStatsHB.getChildren().addAll(gpuLoadTile, gpuTempTile);
		gpuStatsHB.setPadding(new Insets(5));
		gpuStatsHB.setSpacing(5);
		gpuStatsHB.setAlignment(Pos.CENTER);
		gpuStatsHB.setBackground(tileBackground);

		gpuPaneVB.getChildren().addAll(GPU_LABEL, gpuStatsHB);
		gpuPaneVB.setPadding(new Insets(5));
		gpuPaneVB.setSpacing(5);
		gpuPaneVB.setAlignment(Pos.CENTER);
		gpuPaneVB.setBackground(tileBackground);

		setCenter(gpuPaneVB);
	}

	/**
	 * Vytvori vizualni stranku GPU panelu s teplotou
	 *
	 * @return vizualni stranka GPU panelu s teplotou
	 */
	private Gauge createGpuTempGauge() {
        return GaugeBuilder.create()
                           .skinType(Gauge.SkinType.SPACE_X)
                           .prefSize(TILE_SIZE, TILE_SIZE)
                           .animated(true)
                           .unit("\u00B0C")		// stupen Celsia
                           .valueColor(Tile.FOREGROUND)
                           .titleColor(Tile.FOREGROUND)
                           .unitColor(Tile.FOREGROUND)
                           .barColor(Tile.BLUE)
                           .needleColor(Tile.FOREGROUND)
                           .barColor(Tile.BLUE)
                           .barBackgroundColor(Tile.BACKGROUND.darker())
                           .tickLabelColor(Tile.FOREGROUND)
                           .majorTickMarkColor(Tile.FOREGROUND)
                           .minorTickMarkColor(Tile.FOREGROUND)
                           .mediumTickMarkColor(Tile.FOREGROUND)
				           .thresholdColor(Color.RED)
						   .threshold(90)
				           .barColor(Color.RED)
                           .build();
    }

	public void refreshStyleSheet() {

		tileBackground = Config.TILE_BACKGROUND;
		gpuStatsHB.setBackground(tileBackground);
		gpuPaneVB.setBackground(tileBackground);
		GPU_LABEL.setTextFill(Config.TEXT_COLOR);

		gpuLoadTile.setBackgroundColor(Config.TILE_SECONDARY_COLOR);
		gpuLoadTile.setValueColor(Config.TEXT_COLOR);
		gpuLoadTile.setTitleColor(Config.TEXT_COLOR);
		gpuTempTile.setBackgroundColor(Config.TILE_SECONDARY_COLOR);
		gpuTempTile.setTitleColor(Config.TEXT_COLOR);
		gpuTempGauge.setValueColor(Config.TEXT_COLOR);
		gpuTempGauge.setUnitColor(Config.TEXT_COLOR);
		gpuTempGauge.setBarBackgroundColor(Config.TILE_COLOR);
	}
}