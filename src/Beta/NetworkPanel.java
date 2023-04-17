package Beta;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.Tile.SkinType;
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
import oshi.hardware.NetworkIF;

public class NetworkPanel extends BorderPane {
	Tile networkTile;

	double networkNowNum;

	//-----------------------------------------------------------------------
	private static final Label NETWORK_LABEL = new Label("Network Status");
	//-----------------------------------------------------------------------

	public NetworkPanel(SystemInfo si) {
		super();

		List<NetworkIF> networkIFlist;
		networkIFlist = si.getHardware().getNetworkIFs(false);

		try {
			init(networkIFlist);
		} catch (Exception e) {
			System.out.println("Network Panel init failed!");
			e.printStackTrace();
		}
	}

	/**
	 * Metoda pro aktualizaci aktivniho sitoveho adapteru
	 *
	 * @param networkIFlist list rozhrani sitovych adapteru
	 * @return aktivni rozhrani
	 */
	public NetworkIF updatingNetworkIF(List<NetworkIF> networkIFlist) {
		NetworkIF activeNetworkIF = null;
		long highestDownload = 0;
		long temporaryDownload;
		for (NetworkIF networkIF : networkIFlist) {
			temporaryDownload = networkIF.getBytesRecv();
			if (temporaryDownload > highestDownload) {
				highestDownload = temporaryDownload;
				activeNetworkIF = networkIF;
			}
		}
		return activeNetworkIF;
	}

	/**
	 * Inicializace
	 */
	public void init(List<NetworkIF> networkIFlist) {
		System.out.println("Network Panel initialization...");

		BackgroundFill tileBFill = new BackgroundFill(Color.valueOf("#1c1c1b"), new CornerRadii(5), null);
		Background tileBackground = new Background(tileBFill);

		NETWORK_LABEL.setFont(Font.font(30));
		NETWORK_LABEL.setTextFill(Color.WHITE);

		VBox networkPane = new VBox();

		networkTile = new Tile(SkinType.SPARK_LINE);
		networkTile.setMaxValue(10000);
		networkTile.setUnit(" kB/s");
		networkTile.setTitle("Download");
		networkTile.setPrefSize(500, 250);


		System.out.println("Thread: " + Thread.currentThread());
	    Timer timer = new Timer();
	    timer.scheduleAtFixedRate(new TimerTask() {
	        @Override
	        public void run() {
	        	javafx.application.Platform.runLater(new Runnable() {
	                @Override
	                public void run() {

						NetworkIF networkIF = updatingNetworkIF(networkIFlist);

	            		long download1 = networkIF.getBytesRecv();
	            		long timestamp1 = networkIF.getTimeStamp();

	            		networkIF.updateAttributes(); //Updating network stats

	            		long download2 = networkIF.getBytesRecv();
	            		long timestamp2 = networkIF.getTimeStamp();

	                	//networkNowNum = getNetworkUsed(networkMainIF);
		            	networkNowNum = ( (download2 - download1)/(timestamp2 - timestamp1)*10.0 );

						if (networkNowNum >= 1000) {
							networkTile.setUnit(" MB/s");
							networkNowNum /= 1000.00;
							networkTile.setValue(networkNowNum);
		            	}
		            	else {
							networkTile.setUnit(" kB/s");
							networkTile.setValue(networkNowNum);
		            	}
					}
	            });
	        }
	    }, 0, 1000);

		networkPane.getChildren().addAll(NETWORK_LABEL, networkTile);
		networkPane.setPadding(new Insets(5));
		networkPane.setSpacing(5);
		networkPane.setAlignment(Pos.CENTER);
		networkPane.setBackground(tileBackground);

		setCenter(networkPane);
	}
}