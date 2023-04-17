package Beta;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.model.components.Gpu;
import com.profesorfalken.jsensors.model.sensors.Load;
import com.profesorfalken.jsensors.model.sensors.Temperature;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import javafx.application.Platform;
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

/**
 * GPU Panel
 *
 * @author Dominik Nedved, A21B0222P
 * @version
 */
public class GPUPanel extends BorderPane {
	//-----------------------------------------------------------------------
	private Tile gpuLoadTile;

	private Gauge gpuTempGauge;
	private Tile gpuTempTile;

	private Background background;
	private BackgroundFill bFill;
	private BackgroundFill tileBFill;
	private Background tileBackground;
	//-----------------------------------------------------------------------
	private static final Label GPU_LABEL = new Label("GPU Status");
	private static final double TILE_SIZE = 250;
	//-----------------------------------------------------------------------

	GPUPanel(SystemInfo si) {
		super();
		try {
			init();
		} catch (Exception e) {
			System.out.println("GPU Panel init failed!");
			e.printStackTrace();
		}
	}

	/**
	 *Inicializace.
	 */
	public void init() throws Exception {
		System.out.println("GPU Panel initialization...");
		background = new Background(bFill);
		bFill = new BackgroundFill(Color.valueOf("0x2a2a2aff"), null, null);
		background = new Background(bFill);

		tileBFill = new BackgroundFill(Color.valueOf("#1c1c1b"), new CornerRadii(5), null);
		tileBackground = new Background(tileBFill);

		GPU_LABEL.setFont(Font.font(30));
		GPU_LABEL.setTextFill(Color.WHITE);

		VBox gpuPaneVB = new VBox();
		HBox gpuLineHB = new HBox();

		gpuLoadTile = new Tile();
		gpuLoadTile.setSkinType(Tile.SkinType.GAUGE);
		gpuLoadTile.setTitle("GPU Usage (%)");
		gpuLoadTile.setNeedleColor(Color.valueOf("0x37b3fcff"));
		gpuLoadTile.setThresholdColor(Color.RED);
		gpuLoadTile.setThreshold(90);
	    //gpuLoadTile.setMaxValue(100.00);

		gpuTempGauge = createGauge(Gauge.SkinType.SPACE_X);
		gpuTempGauge.setThresholdColor(Color.RED);
		gpuTempGauge.setThreshold(90);
		gpuTempGauge.setBarColor(Color.RED);
	    gpuTempTile = TileBuilder.create()
	                              .prefSize(TILE_SIZE, TILE_SIZE)
	                              .skinType(Tile.SkinType.CUSTOM)
	                              .title("GPU Temperature")
	                              .unit("\u00b0C")
	                              .text("")
	                              .graphic(gpuTempGauge)
	                              .build();

		//GPULoadAndTemp gpuLoadAndTemp = new GPULoadAndTemp();
		GPULoadAndTemp cpuAndGpuStats = new GPULoadAndTemp();
		Thread gpuLoadAndTempThread = new Thread(cpuAndGpuStats);
		gpuLoadAndTempThread.start();

		Timer gpuLoadAndTempTimer = new Timer();
		gpuLoadAndTempTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				javafx.application.Platform.runLater(new Runnable() {
					//javafx.application.Platform.runLater(new Runnable() {
					@Override
					public void run() {
						gpuLoadTile.setValue(cpuAndGpuStats.getGpuLoad());
						System.out.println(" - GPU Load: " + cpuAndGpuStats.getGpuLoad());

						gpuTempGauge.setValue(cpuAndGpuStats.getGpuTemp());
						System.out.println(" - GPU Temperature: " + cpuAndGpuStats.getGpuTemp());
					}
				});
			}
		}, 0, 5000);

		gpuLineHB.getChildren().addAll(gpuLoadTile, gpuTempTile);
		gpuLineHB.setPadding(new Insets(5));
		gpuLineHB.setSpacing(5);
		gpuLineHB.setAlignment(Pos.CENTER);
		gpuLineHB.setBackground(tileBackground);

		gpuPaneVB.getChildren().addAll(GPU_LABEL, gpuLineHB);
		gpuPaneVB.setPadding(new Insets(5));
		gpuPaneVB.setSpacing(5);
		gpuPaneVB.setAlignment(Pos.CENTER);
		gpuPaneVB.setBackground(tileBackground);

		setCenter(gpuPaneVB);
	}

	private double getGpuLoad(List<Gpu> gpus) {
		if (gpus != null) {
		    for (final Gpu gpu : gpus) {
		        //System.out.println("Found CPU component: " + gpu.name);
		        if (gpu.sensors != null) {
		          //System.out.println("Sensors found.");

		          List<Load> gpuLoads = gpu.sensors.loads;
		          // test ziskani jednotlivych hodnot
		          String wantedGpuLoadValue = "Load GPU Core";
		          int i = 0;
		          while(i < gpuLoads.size()) {
		        	  if(gpuLoads.get(i).name.equals(wantedGpuLoadValue)) {
		        		  //System.out.println("Ziskani pouze zateze - uspesne (" + gpuLoads.get(i).name + ": " + gpuLoads.get(i).value + ")");
		        		  return gpuLoads.get(i).value;
		        	  }
		        	  i++;
		          }
		        }
		    }
		}
		return 0;
	}

	private double getGpuTemp(List<Gpu> gpus) {
		if (gpus != null) {
		    for (final Gpu gpu : gpus) {
		        //System.out.println("Found CPU component: " + gpu.name);
		        if (gpu.sensors != null) {
		          //System.out.println("Sensors found.");

		          List<Temperature> temps = gpu.sensors.temperatures;
		          // test ziskani jednotlivych hodnot
		          String wantedGpuValue = "Temp GPU Core";
		          int i = 0;
		          while(i < temps.size()) {
		        	  if(temps.get(i).name.equals(wantedGpuValue)) {
		        		  //System.out.println("Ziskani pouze teploty - uspesne (" + temps.get(i).name + ": " + temps.get(i).value + ")");
		        		  return temps.get(i).value;
		        	  }
		        	  i++;
		          }
		        }
		    }
		}
		return 0;
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