package alpha;

import alpha.dataModel.NetworkSpeed;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.Tile.SkinType;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

/**
 * Panel se zobrazenim sitoveho pripojeni
 *
 * @author Dominik Nedved, A22B0109P
 * @version 2023.04.17
 */
public class NetworkPanel extends BorderPane {

	private Tile networkTile;

	private final SystemInfo si;


	//-----------------------------------------------------------------------
	private static final Label NETWORK_LABEL = new Label("Network Status");
	//-----------------------------------------------------------------------

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

		BackgroundFill tileBFill = new BackgroundFill(Color.valueOf("#1c1c1b"), new CornerRadii(5), null);
		Background tileBackground = new Background(tileBFill);

		NETWORK_LABEL.setFont(Font.font(30));
		NETWORK_LABEL.setTextFill(Color.WHITE);

		VBox networkPane = new VBox();

		networkTile = new Tile(SkinType.SPARK_LINE);
		networkTile.setMaxValue(10000.0);
		networkTile.setUnit(" MB/s");
		networkTile.setTitle("Download");
		networkTile.setPrefSize(500, 250);
		networkTile.setDecimals(3);				// desetinna mista
		networkTile.setAveragingPeriod(31);		// rozsah grafu

		NetworkSpeed networkSpeed = new NetworkSpeed(si);
		networkSpeed.run();
//		System.out.println("Thread: " + Thread.currentThread());

		DoubleProperty download = new SimpleDoubleProperty(0);
		download.bind(networkSpeed.downloadProperty());
		networkSpeed.downloadProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				networkTile.setValue(download.get() / 1024);	// v MB
			}
		});

		networkPane.getChildren().addAll(NETWORK_LABEL, networkTile);
		networkPane.setPadding(new Insets(5));
		networkPane.setSpacing(5);
		networkPane.setAlignment(Pos.CENTER);
		networkPane.setBackground(tileBackground);

		setCenter(networkPane);
	}
}