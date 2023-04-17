package Beta;

import java.util.Timer;
import java.util.TimerTask;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.TickType;

/**
 *
 * @author Nedvi
 *
 */
public class CPUPanel extends BorderPane {
	//-----------------------------------------------------------------------

	private SystemInfo si = new SystemInfo();
    private long[] oldTicks;
	private double cpuLoadNowNum;
	private double cpuTempNow;

	private Gauge cpuTempGauge;
	private Tile cpuTempTile;

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
	public CPUPanel(SystemInfo si) {
		super();
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
	public void init(CentralProcessor processorCP) throws Exception {
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
	        	javafx.application.Platform.runLater(new Runnable() {
	                @Override
	                public void run() {
	                	cpuLoadNowNum = cpuData(processorCP);
	            		cpuLoadTile.setValue(cpuLoadNowNum*100);
						//System.out.println(" - CPU Load: " + cpuLoadNowNum*100);

	                }
	            });
	        }
	    }, 0, 1000);

		cpuTempGauge = createGauge(Gauge.SkinType.SPACE_X);
		cpuTempGauge.setThresholdColor(Color.RED);
		cpuTempGauge.setThreshold(90);
		cpuTempGauge.setBarColor(Color.RED);
	    cpuTempTile = TileBuilder.create()
	                              .prefSize(TILE_SIZE, TILE_SIZE)
	                              .skinType(Tile.SkinType.CUSTOM)
	                              .title("CPU Temperature")
	                              .unit("\u00b0C")
	                              .text("")
	                              .graphic(cpuTempGauge)
	                              .build();

		CPUTemperature cpuTemp = new CPUTemperature();
		Thread cpuTempThread = new Thread(cpuTemp);
		cpuTempThread.start();

	    Timer cpuTempTimer = new Timer();
	    cpuTempTimer.scheduleAtFixedRate(new TimerTask() {
	        @Override
	        public void run() {
	        	javafx.application.Platform.runLater(new Runnable() {
	                @Override
	                public void run() {
						cpuTempNow = cpuTemp.getCpuTempNow();
	                	cpuTempGauge.setValue(cpuTempNow);
						System.out.println(" - CPU Temperature: " + cpuTempNow);
					}
	            });
	        }
	    }, 0, 5000);

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
                           .unit("\u00B0C")
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
