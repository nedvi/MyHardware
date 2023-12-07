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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

/**
 * Trida reprezentujci GPU panel v GUI.
 * Vizualizace zateze a teploty.
 *
 * @author Dominik Nedved, A22B0109P
 * @version 2023.06.26
 */
public class GPUPanel extends BorderPane {

	/** Datovy model se zatezi a teplotou GPU */
	private final LoadAndTemp loadAndTemp;

	//===================== Tiles =====================

	private static final Label GPU_LABEL = new Label("GPU Status");
	private static final double TILE_SIZE = 250;
	private Background tileBackground;
	private Tile gpuLoadTile;
	private Gauge gpuTempGauge;
	private Tile gpuTempTile;

	//===================== Nodes =====================

	private VBox gpuPaneVB;
	private HBox gpuStatsHB;

	//===================== GPU Usage table =====================
	private TableView<TableValues> gpuUsageTable;
	private ObservableList<TableValues> gpuUsageData;
	public static List<Double> gpuUsages = new ArrayList<>();
	public static double[] minGpuUsage = {100};
	public static double[] maxGpuUsage = {0};

	//===================== GPU Temp table =====================
	private TableView<TableValues> gpuTempTable;
	private ObservableList<TableValues> gpuTempData;
	public static List<Double> gpuTemps = new ArrayList<>();
	public static double[] minGpuTemp = {100};
	public static double[] maxGpuTemp = {0};

	//===============================================================

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
				if (gpuLoad.get() > 0) {
					gpuUsages.add(gpuLoad.get());
				}
				double averageGpuUsage;
				double gpuUsagesSum = 0;
				for (double d : gpuUsages) {
					gpuUsagesSum += d;
				}
				averageGpuUsage = gpuUsagesSum / gpuUsages.size();
				gpuUsageData.get(0).setValue((double) Math.round(averageGpuUsage * 100) / 100);


				if (minGpuUsage[0] > gpuLoad.get() || minGpuUsage[0] < 0.1) {	// to 0.1 je tam kvuli tomu, ze nez se nacte prvni realna zatez GPU, stoji tam nula, ktera tam pak i logicky zustane
					minGpuUsage[0] = gpuLoad.get();
					gpuUsageData.get(1).setValue((double) Math.round(minGpuUsage[0] * 100) /100);
				}
				if (maxGpuUsage[0] < gpuLoad.get()) {
					maxGpuUsage[0] = gpuLoad.get();
					gpuUsageData.get(2).setValue((double) Math.round(maxGpuUsage[0] * 100) /100);
				}
				gpuLoadTile.setValue(gpuLoad.get());
			}
		});

		DoubleProperty gpuTemp = new SimpleDoubleProperty(0);
		gpuTemp.bind(loadAndTemp.gpuTempProperty());
		loadAndTemp.gpuTempProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (gpuTemp.get() > 0) {
					gpuTemps.add(gpuTemp.get());
				}
				double averageGpuTemp;
				double gpuTempsSum = 0;
				for (double d : gpuTemps) {
					gpuTempsSum += d;
				}
				averageGpuTemp = gpuTempsSum / gpuTemps.size();
				gpuTempData.get(0).setValue((double) Math.round(averageGpuTemp * 100) / 100);

				if (minGpuTemp[0] > gpuTemp.get() || minGpuTemp[0] < 0.1) {	// to 0.1 je tam kvuli tomu, ze nez se nacte prvni realna teplota GPU, stoji tam nula, ktera tam pak i logicky zustane
					minGpuTemp[0] = gpuTemp.get();
					gpuTempData.get(1).setValue((double) Math.round(minGpuTemp[0] * 100) / 100);
				}
				if (maxGpuTemp[0] < gpuTemp.get()) {
					maxGpuTemp[0] = gpuTemp.get();
					gpuTempData.get(2).setValue((double) Math.round(maxGpuTemp[0] * 100) / 100);
				}

				gpuTempGauge.setValue(gpuTemp.get());
			}
		});

		gpuStatsHB.getChildren().addAll(gpuLoadTile, gpuTempTile);
		gpuStatsHB.setPadding(new Insets(5));
		gpuStatsHB.setSpacing(5);
		gpuStatsHB.setAlignment(Pos.CENTER);
		gpuStatsHB.setBackground(tileBackground);

		gpuPaneVB.getChildren().addAll(GPU_LABEL, gpuStatsHB, getTables());
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

	/**
	 * @return Node s tabulkami pod tiles
	 */
	private Node getTables() {
		HBox tablesHB = new HBox();

		//===================== CPU Usage table =====================
		gpuUsageTable = new TableView<>();
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

		gpuUsageTable.getColumns().addAll(valueTypeUsageCol, usageCol);

		gpuUsageTable.setMaxWidth(236);
		gpuUsageTable.setMaxHeight(98);
		gpuUsageTable.setMinWidth(236);
		gpuUsageTable.setMinHeight(98);
		gpuUsageTable.setSelectionModel(null);
		gpuUsageTable.setEditable(false);

		gpuUsageData = FXCollections.observableArrayList();
		gpuUsageData.add(new TableValues("Average", 0));
		gpuUsageData.add(new TableValues("Min", 100));
		gpuUsageData.add(new TableValues("Max", 0));

		gpuUsageTable.getItems().addAll(gpuUsageData);

		//===================== CPU Temp table =====================
		gpuTempTable = new TableView<>();
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

		gpuTempTable.getColumns().addAll(valueTypeTempCol, tempCol);

		gpuTempTable.setMaxWidth(236);
		gpuTempTable.setMaxHeight(98);
		gpuTempTable.setMinWidth(236);
		gpuTempTable.setMinHeight(98);
		gpuTempTable.setSelectionModel(null);
		gpuTempTable.setEditable(false);

		gpuTempData = FXCollections.observableArrayList();
		gpuTempData.add(new TableValues("Average", 0));
		gpuTempData.add(new TableValues("Min", 100));
		gpuTempData.add(new TableValues("Max", 0));

		gpuTempTable.getItems().addAll(gpuTempData);

		tablesHB.getChildren().addAll(gpuUsageTable, gpuTempTable);

		return tablesHB;
	}

	/**
	 * Provede refresh stylu.
	 * Vola se pokazde pri prepnuti konkretniho panelu.
	 */
	public void refreshStyleSheet() {

		gpuUsageTable.getStylesheets().clear();
		gpuUsageTable.getStylesheets().add(Config.ACTIVE_STYLE_SHEET.get());

		gpuTempTable.getStylesheets().clear();
		gpuTempTable.getStylesheets().add(Config.ACTIVE_STYLE_SHEET.get());

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