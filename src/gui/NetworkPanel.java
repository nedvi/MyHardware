package gui;

import dataModel.NetworkSpeed;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.Tile.SkinType;
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
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import oshi.SystemInfo;

/**
 * Panel se zobrazenim sitoveho pripojeni
 *
 * @author Dominik Nedved, A22B0109P
 * @version 2023.06.26
 */
public class NetworkPanel extends BorderPane {

	//===================== Data =====================

	private final SystemInfo si;

	//===================== Nodes =====================

	private VBox networkPane;
	private HBox tilesHB;
	private static final Label NETWORK_LABEL = new Label("Network Status");

	//===================== Tiles =====================

	private Tile downloadTile;
	private Tile uploadTile;
	private Background tileBackground;

	//===============================================================

	/**
	 * Konstruktror network panelu
	 *
	 * @param si pristup k informacim o systemu
	 */
	public NetworkPanel(SystemInfo si) {
		super();
		this.si = si;
		try {
			init();
		} catch (Exception e) {
			System.out.println("Network Panel init failed!");
			e.printStackTrace();
		}
	}

	/**
	 * Inicializace
	 */
	public void init() {
		System.out.println("Network Panel initialization...");

		tileBackground = new Background(Config.tileBFill);

		NETWORK_LABEL.setFont(Font.font(30));
		NETWORK_LABEL.setTextFill(Color.WHITE);

		networkPane = new VBox();

		downloadTile = TileBuilder.create()
				.prefSize(250, 250)
				.skinType(SkinType.GAUGE2)
				.title("Download \u2193")
				.unit(" MB/s")
				.gradientStops(new Stop(0, Tile.BLUE),
						new Stop(0.5, Tile.BLUE),
						new Stop(1.0, Tile.BLUE))
				.strokeWithGradient(true)
				.decimals(3)
				.build();

		uploadTile = TileBuilder.create()
				.prefSize(250, 250)
				.skinType(SkinType.GAUGE2)
				.title("Upload \u2191")
				.unit(" MB/s")
				.gradientStops(new Stop(0, Tile.BLUE),
						new Stop(0.5, Tile.BLUE),
						new Stop(1.0, Tile.BLUE))
				.strokeWithGradient(true)
				.decimals(3)
				.build();

		NetworkSpeed networkSpeed = new NetworkSpeed(si);
		Thread thread = new Thread(networkSpeed);
		thread.start();
		System.out.println("Network " + thread.getName());

		DoubleProperty download = new SimpleDoubleProperty(0);
		download.bind(networkSpeed.downloadProperty());

		networkSpeed.downloadProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				downloadTile.setValue(download.get() / 1024);	// v MB
			}
		});

		DoubleProperty upload = new SimpleDoubleProperty(0);
		upload.bind(networkSpeed.uploadProperty());

		networkSpeed.uploadProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				uploadTile.setValue(upload.get() / 1024);	// v MB
			}
		});

		tilesHB = new HBox();
		tilesHB.getChildren().addAll(downloadTile, uploadTile);
		tilesHB.setPadding(new Insets(5));
		tilesHB.setSpacing(5);
		tilesHB.setAlignment(Pos.CENTER);
		tilesHB.setBackground(tileBackground);

		networkPane.getChildren().addAll(NETWORK_LABEL, tilesHB);
		networkPane.setPadding(new Insets(5));
		networkPane.setSpacing(5);
		networkPane.setAlignment(Pos.CENTER);
		networkPane.setBackground(tileBackground);

		setCenter(networkPane);
	}

	/**
	 * Provede refresh stylu.
	 * Vola se pokazde pri prepnuti konkretniho panelu.
	 */
	public void refreshStyleSheet() {

		tileBackground = Config.TILE_BACKGROUND;
		networkPane.setBackground(tileBackground);
		tilesHB.setBackground(tileBackground);
		NETWORK_LABEL.setTextFill(Config.TEXT_COLOR);

		downloadTile.setBackgroundColor(Config.TILE_SECONDARY_COLOR);
		downloadTile.setValueColor(Config.TEXT_COLOR);
		downloadTile.setTitleColor(Config.TEXT_COLOR);
		downloadTile.setUnitColor(Config.TEXT_COLOR);
		downloadTile.setBarBackgroundColor(Config.TILE_COLOR);

		uploadTile.setBackgroundColor(Config.TILE_SECONDARY_COLOR);
		uploadTile.setValueColor(Config.TEXT_COLOR);
		uploadTile.setTitleColor(Config.TEXT_COLOR);
		uploadTile.setUnitColor(Config.TEXT_COLOR);
		uploadTile.setBarBackgroundColor(Config.TILE_COLOR);
	}
}