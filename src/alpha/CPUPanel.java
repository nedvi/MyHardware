package alpha;

import alpha.dataModel.LoadAndTemp;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.TickType;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Nedvi
 *
 */
public class CPUPanel extends BorderPane {
	private final LoadAndTemp loadAndTemp;

    private long[] oldTicks;
	private double cpuLoadNowNum;
	private final DoubleProperty cpuTempNow = new SimpleDoubleProperty();

	private Gauge cpuTempGauge;

	private Tile cpuLoadTile;

	private Background background;
	private BackgroundFill bFill;
	private BackgroundFill tileBFill;
	private Background tileBackground;
	//-----------------------------------------------------------------------
	private static final Label CPU_LABEL = new Label("CPU Status");
	private static final double TILE_SIZE = 250;
	//-----------------------------------------------------------------------

	/**
	 *
	 * @param si
	 */
	public CPUPanel(SystemInfo si, LoadAndTemp loadAndTemp) {
		super();
		this.loadAndTemp = loadAndTemp;
		try {
			init(si.getHardware().getProcessor());
		} catch (Exception e) {
			System.out.println("CPU Panel init failed!");
			e.printStackTrace();
		}
	}

	/**
	 * Inicializace
	 */
	public void init(CentralProcessor processorCP) {
		System.out.println("CPU Panel initialization...");
		bFill = new BackgroundFill(Color.valueOf("0x2a2a2aff"), null, null);
		background = new Background(bFill);

		tileBFill = new BackgroundFill(Color.valueOf("#1c1c1b"), new CornerRadii(5), null);
		tileBackground = new Background(tileBFill);

		CPU_LABEL.setFont(Font.font(30));
		CPU_LABEL.setTextFill(Color.WHITE);

		VBox cpuPaneVB = new VBox();
		HBox cpuLine = new HBox();

        oldTicks = new long[TickType.values().length];

		cpuLoadTile = new Tile();
		cpuLoadTile.setSkinType(Tile.SkinType.GAUGE);
        cpuLoadTile.setTitle("CPU Usage (%)");
		cpuLoadTile.setNeedleColor(Color.valueOf("0x37b3fcff"));
		cpuLoadTile.setThresholdColor(Color.RED);
		cpuLoadTile.setThreshold(90);

		System.out.println(Thread.currentThread());
	    Timer cpuLoadTimer = new Timer();
	    cpuLoadTimer.scheduleAtFixedRate(new TimerTask() {
	        @Override
	        public void run() {
	        	javafx.application.Platform.runLater(() -> {
					cpuLoadNowNum = cpuData(processorCP);
					cpuLoadTile.setValue(cpuLoadNowNum*100);
				});
	        }
	    }, 0, 1000);

		cpuTempGauge = createGauge(Gauge.SkinType.SPACE_X);
		cpuTempGauge.setThresholdColor(Color.RED);
		cpuTempGauge.setThreshold(90);
		cpuTempGauge.setBarColor(Color.RED);
		Tile cpuTempTile = TileBuilder.create()
				.prefSize(TILE_SIZE, TILE_SIZE)
				.skinType(Tile.SkinType.CUSTOM)
				.title("CPU Temperature")
				.unit("\u00b0C")
				.text("")
				.graphic(cpuTempGauge)
				.build();

	    Timer cpuTempTimer = new Timer();
	    cpuTempTimer.scheduleAtFixedRate(new TimerTask() {
	        @Override
	        public void run() {
	        	javafx.application.Platform.runLater(() -> {
					cpuTempNow.bindBidirectional(loadAndTemp.cpuTempProperty());
					cpuTempGauge.setValue(cpuTempNow.get());
//					System.out.println(" - CPU Temperature: " + cpuTempNow);
				});
	        }
	    }, 0, 1000);

		cpuLine.getChildren().addAll(cpuLoadTile, cpuTempTile);
		cpuLine.setPadding(new Insets(5));
		cpuLine.setSpacing(5);
		cpuLine.setAlignment(Pos.CENTER);
		cpuLine.setBackground(tileBackground);

		cpuPaneVB.getChildren().addAll(CPU_LABEL, cpuLine);
		cpuPaneVB.setPadding(new Insets(5));
		cpuPaneVB.setSpacing(5);
		cpuPaneVB.setAlignment(Pos.CENTER);
		cpuPaneVB.setBackground(tileBackground);

		setCenter(cpuPaneVB);
	}

    private double cpuData(CentralProcessor proc) {
        double d = proc.getSystemCpuLoadBetweenTicks(oldTicks);
        oldTicks = proc.getSystemCpuLoadTicks();
        return d;
    }

    private Gauge createGauge(final Gauge.SkinType TYPE) {
        return GaugeBuilder.create()
                           .skinType(TYPE)
                           .prefSize(TILE_SIZE, TILE_SIZE)
                           .animated(true)
                           //.title("")
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
                           .build();
    }

}
