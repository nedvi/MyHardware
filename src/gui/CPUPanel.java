package gui;

import dataModel.LoadAndTemp;
import dataModel.TableValues;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.TickType;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Trida reprezentujci CPU panel v GUI.
 * Vizualizace zateze a teploty.
 *
 * @author Dominik Nedved
 * @version 2023.06.26
 */
public class CPUPanel extends BorderPane {

	//===================== Data =====================

	private final LoadAndTemp loadAndTemp;
    private long[] oldTicks;
	private final DoubleProperty cpuLoad = new SimpleDoubleProperty();

	//===================== Tiles =====================

	private Tile cpuTempTile;
	private Gauge cpuTempGauge;
	private Tile cpuLoadTile;

	//===================== Nodes =====================

	private VBox cpuPaneVB;
	private HBox cpuLine;

	//===================== CPU Usage table =====================
	private TableView<TableValues> cpuUsageTable;
	private ObservableList<TableValues> cpuUsageData;
	public static List<Double> cpuUsages = new ArrayList<>();
	public static double[] minCpuUsage = {100};
	public static double[] maxCpuUsage = {0};

	//===================== CPU Temp table =====================
	private TableView<TableValues> cpuTempTable;
	private ObservableList<TableValues> cpuTempData;
	public static List<Double> cpuTemps = new ArrayList<>();
	public static double[] minCpuTemp = {100};
	public static double[] maxCpuTemp = {0};

	private Background tileBackground;
	//===============================================================
	private static final Label CPU_LABEL = new Label("CPU Status");
	private static final double TILE_SIZE = 250;

	//===============================================================

	/**
	 * Konstruktor
	 * @param si predana instance SystemInfa
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

		tileBackground = Config.TILE_BACKGROUND;

		CPU_LABEL.setFont(Font.font(30));
		CPU_LABEL.setTextFill(Color.WHITE);

		cpuPaneVB = new VBox();
		cpuLine = new HBox();

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
					cpuLoad.set(cpuData(processorCP));
					if (cpuLoad.get() > 0) {
						cpuUsages.add(cpuLoad.get()*100);
					}
					double averageCpuUsage;
					double cpuUsagesSum = 0;
					for (double d : cpuUsages) {
						cpuUsagesSum += d;
					}
					averageCpuUsage = cpuUsagesSum / cpuUsages.size();
					cpuUsageData.get(0).setValue((double) Math.round(averageCpuUsage * 100) / 100);


					if (minCpuUsage[0] > cpuLoad.get()*100 || minCpuUsage[0] < 0.1) {	// to 0.1 je tam kvuli tomu, ze nez se nacte prvni realna zatez CPU, stoji tam nula, ktera tam pak i logicky zustane
						minCpuUsage[0] = cpuLoad.get()*100;
						cpuUsageData.get(1).setValue((double) Math.round(minCpuUsage[0] * 100) /100);
					}
					if (maxCpuUsage[0] < cpuLoad.get()*100) {
						maxCpuUsage[0] = cpuLoad.get()*100;
						cpuUsageData.get(2).setValue((double) Math.round(maxCpuUsage[0] * 100) /100);
					}
					cpuLoadTile.setValue(cpuLoad.get()*100);
				});
	        }
	    }, 0, 1000);

		cpuTempGauge = createGauge();
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

		DoubleProperty cpuTemp = new SimpleDoubleProperty(0);
		cpuTemp.bind(loadAndTemp.cpuTempProperty());
		loadAndTemp.cpuTempProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					if (cpuTemp.get() > 0) {
						cpuTemps.add(cpuTemp.get());
					}
					double averageCpuTemp;
					double cpuTempsSum = 0;
					for (double d : cpuTemps) {
						cpuTempsSum += d;
					}
					averageCpuTemp = cpuTempsSum / cpuTemps.size();
					cpuTempData.get(0).setValue((double) Math.round(averageCpuTemp * 100) / 100);

					if (minCpuTemp[0] > cpuTemp.get() || minCpuTemp[0] < 0.1) {	// to 0.1 je tam kvuli tomu, ze nez se nacte prvni realna teplota CPU, stoji tam nula, ktera tam pak i logicky zustane
						minCpuTemp[0] = cpuTemp.get();
						cpuTempData.get(1).setValue((double) Math.round(minCpuTemp[0] * 100) / 100);
					}
					if (maxCpuTemp[0] < cpuTemp.get()) {
						maxCpuTemp[0] = cpuTemp.get();
						cpuTempData.get(2).setValue((double) Math.round(maxCpuTemp[0] * 100) / 100);
					}

					cpuTempGauge.setValue(cpuTemp.get());
				}
			});

		cpuLine.getChildren().addAll(cpuLoadTile, cpuTempTile);
		cpuLine.setPadding(new Insets(5));
		cpuLine.setSpacing(5);
		cpuLine.setAlignment(Pos.CENTER);
		cpuLine.setBackground(tileBackground);

		cpuPaneVB.getChildren().addAll(CPU_LABEL, cpuLine, getTables());
		cpuPaneVB.setPadding(new Insets(5));
		cpuPaneVB.setSpacing(5);
		cpuPaneVB.setAlignment(Pos.CENTER);
		cpuPaneVB.setBackground(tileBackground);

		setCenter(cpuPaneVB);
	}

	/**
	 * @param proc procesor
	 * @return aktualni data procesoru pro vypocet zateze
	 */
    private double cpuData(CentralProcessor proc) {
        double d = proc.getSystemCpuLoadBetweenTicks(oldTicks);
        oldTicks = proc.getSystemCpuLoadTicks();
        return d;
    }

	/**
	 * @return gauge - grafika pro tile
	 */
    private Gauge createGauge() {
        return GaugeBuilder.create()
                           .skinType(Gauge.SkinType.SPACE_X)
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

	/**
	 * @return Node s tabulkami pod tiles
	 */
	private Node getTables() {
		HBox tablesHB = new HBox();

		//===================== CPU Usage table =====================
		cpuUsageTable = new TableView<>();
		TableColumn<TableValues, String> valueTypeUsageCol = new TableColumn<>("Value type");
		valueTypeUsageCol.setMinWidth(118);
		valueTypeUsageCol.setMaxWidth(118);
		valueTypeUsageCol.setSortable(false);
		valueTypeUsageCol.setCellValueFactory(new PropertyValueFactory<TableValues, String>("valueType"));

		TableColumn<TableValues, Double> usageCol = new TableColumn<>("Usage (%)");
		usageCol.setMinWidth(118);
		usageCol.setMaxWidth(118);
		usageCol.setSortable(false);
		usageCol.setCellValueFactory(new PropertyValueFactory<TableValues, Double>("value"));

		cpuUsageTable.getColumns().addAll(valueTypeUsageCol, usageCol);

		cpuUsageTable.setMaxWidth(236);
		cpuUsageTable.setMaxHeight(98);
		cpuUsageTable.setMinWidth(236);
		cpuUsageTable.setMinHeight(98);
		cpuUsageTable.setSelectionModel(null);
		cpuUsageTable.setEditable(false);

		cpuUsageData = FXCollections.observableArrayList();
		cpuUsageData.add(new TableValues("Average", 0));
		cpuUsageData.add(new TableValues("Min", 100));
		cpuUsageData.add(new TableValues("Max", 0));

		cpuUsageTable.getItems().addAll(cpuUsageData);

		//===================== CPU Temp table =====================
		cpuTempTable = new TableView<>();
		TableColumn<TableValues, String> valueTypeTempCol = new TableColumn<>("Value type");
		valueTypeTempCol.setMinWidth(118);
		valueTypeTempCol.setMaxWidth(118);
		valueTypeTempCol.setSortable(false);
		valueTypeTempCol.setCellValueFactory(new PropertyValueFactory<TableValues, String>("valueType"));

		TableColumn<TableValues, Double> tempCol = new TableColumn<>("Temperature (\u00B0C)");
		tempCol.setMinWidth(118);
		tempCol.setMaxWidth(118);
		tempCol.setSortable(false);
		tempCol.setCellValueFactory(new PropertyValueFactory<TableValues, Double>("value"));

		cpuTempTable.getColumns().addAll(valueTypeTempCol, tempCol);

		cpuTempTable.setMaxWidth(236);
		cpuTempTable.setMaxHeight(98);
		cpuTempTable.setMinWidth(236);
		cpuTempTable.setMinHeight(98);
		cpuTempTable.setSelectionModel(null);
		cpuTempTable.setEditable(false);

		cpuTempData = FXCollections.observableArrayList();
		cpuTempData.add(new TableValues("Average", 0));
		cpuTempData.add(new TableValues("Min", 100));
		cpuTempData.add(new TableValues("Max", 0));

		cpuTempTable.getItems().addAll(cpuTempData);

		tablesHB.getChildren().addAll(cpuUsageTable, cpuTempTable);

		return tablesHB;
	}

	/**
	 * Provede refresh stylu.
	 * Vola se pokazde pri prepnuti konkretniho panelu.
	 */
	public void refreshStyleSheet() {

		cpuUsageTable.getStylesheets().clear();
		cpuUsageTable.getStylesheets().add(Config.ACTIVE_STYLE_SHEET.get());

		cpuTempTable.getStylesheets().clear();
		cpuTempTable.getStylesheets().add(Config.ACTIVE_STYLE_SHEET.get());

		tileBackground = Config.TILE_BACKGROUND;
		cpuLine.setBackground(tileBackground);
		cpuPaneVB.setBackground(tileBackground);
		CPU_LABEL.setTextFill(Config.TEXT_COLOR);

		cpuLoadTile.setBackgroundColor(Config.TILE_SECONDARY_COLOR);
		cpuLoadTile.setValueColor(Config.TEXT_COLOR);
		cpuLoadTile.setTitleColor(Config.TEXT_COLOR);
		cpuTempTile.setBackgroundColor(Config.TILE_SECONDARY_COLOR);
		cpuTempTile.setTitleColor(Config.TEXT_COLOR);
		cpuTempGauge.setValueColor(Config.TEXT_COLOR);
		cpuTempGauge.setUnitColor(Config.TEXT_COLOR);
		cpuTempGauge.setBarBackgroundColor(Config.TILE_COLOR);
	}

}
