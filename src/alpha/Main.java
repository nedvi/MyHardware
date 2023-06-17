package alpha;

import alpha.dataModel.LoadAndTemp;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import oshi.SystemInfo;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Hlavni trida s resenim zadani semestralni prace (ZCU-FAV-UUR)
 *
 * @author Dominik Nedved, A22B0109P
 * @version 2023.05.11
 */
public class Main extends Application {

	//================================== Konstanty titulku okna ==================================
	private final String MAIN_TITLE_STR = "MyHardware";

	// reference pro Primary Stage
	private Stage primaryStage;
	private BorderPane rootPaneBP;
	//-------------------------------------------------------------------------------
	private Background background;
	private final BackgroundFill tileBFill = new BackgroundFill(Color.valueOf("#1c1c1b"), null, null);
	private final Background DARKER_BACKGROUND = new Background(tileBFill);

	private Background buttonBackground;

	public SystemInfo si;
	private LoadAndTemp loadAndTemp;

	public Button dashboardBTN = new Button();
	public Button hwTreeBTN;
	public Button settingsBTN;
	public Button aboutBTN;

	private HWTree hwTree;

	private Dashboard dashboard;

	private Label upTimeLabel = new Label("00:00:00");
	private Timer upTimeTimer = new Timer();
	int sec = 0;
	int min = 0;
	int hour = 0;

	//==========================================================================
	/**
	 * Spousteci metoda
	 *
	 * @param args parametry prikazove radky
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 *Inicializace.
	 */
	@Override
	public void init() {
		System.out.println("Initialization...");

		si = new SystemInfo();

		loadAndTemp = new LoadAndTemp();
		loadAndTemp.run();

		dashboard = new Dashboard(si, loadAndTemp);

		hwTree = new HWTree();

		background = Config.BACKGROUND;
		buttonBackground = Config.BTN_BACKGROUND;;
		dashboardBTN.setDisable(true);

		startUpTime();
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle(MAIN_TITLE_STR);
		this.primaryStage.setScene(getScene());

		// minimalni velikost okna
		this.primaryStage.setMinWidth(1000);
		this.primaryStage.setMinHeight(700);

		this.primaryStage.setMaxWidth(1000);
		this.primaryStage.setMaxHeight(700);

		this.primaryStage.show();

		// zavreni vsech vlaken po zavreni aplikace
		this.primaryStage.setOnCloseRequest(e -> {
			Platform.exit();
			System.exit(0);
		});
	}

	/**
	 * nastaveni sceny
	 * @return scene
	 */
	private Scene getScene() {
		return new Scene(getRoot(), 1000, 700);
	}

	/**
	 * korenovy panel, umistujeme na nej jednotlive komponenty
	 * @return rootPaneBP
	 */
	private Parent getRoot() {
		rootPaneBP = new BorderPane();
		rootPaneBP.setTop(getTopBar());
		rootPaneBP.setCenter(dashboard);
		return rootPaneBP;
	}

	/**
	 * Metoda pro zobrazeni vrchniho baru s prepinanim jednotlivych sekci
	 *
	 * @return topBar
	 */
	private Node getTopBar() {
		HBox topPaneHB = new HBox();

		Label TITLE = new Label("- My Hardware -");
		TITLE.setFont(Font.font("Helvetica", FontWeight.BOLD , 40));
		TITLE.setBackground(background);
		TITLE.setAlignment(Pos.CENTER);
		TITLE.setTextFill(Color.valueOf("0x37b3fcff"));

		Label settingsLabel = new Label("Settings");
		settingsLabel.setStyle("-fx-text-fill: WHITE;");
		settingsLabel.setFont(Font.font("Helvetica", 15));
		Label aboutLabel = new Label("About");
		aboutLabel.setStyle("-fx-text-fill: WHITE;");
		aboutLabel.setFont(Font.font("Helvetica", 15));

		HBox topBarHB = new HBox();

		dashboardBTN.setText("Dashboard");
		dashboardBTN.setFont(Font.font("Helvetica", FontWeight.BOLD, 15));
		dashboardBTN.setBackground(buttonBackground);
		dashboardBTN.setTextFill(Color.valueOf("0x37b3fcff"));

		dashboardBTN.setOnAction(e -> {
			if (rootPaneBP.getCenter()!=dashboard) {
				rootPaneBP.setCenter(dashboard);
				dashboardBTN.setDisable(true);
				hwTreeBTN.setDisable(false);
			}
		});

		hwTreeBTN = new Button();
		hwTreeBTN.setText("HW Tree");
		hwTreeBTN.setFont(Font.font("Helvetica", FontWeight.BOLD, 15));
		hwTreeBTN.setBackground(buttonBackground);
		hwTreeBTN.setTextFill(Color.valueOf("0x37b3fcff"));

		hwTreeBTN.setOnAction(e -> {
			if (rootPaneBP.getCenter()!= hwTree) {
				rootPaneBP.setCenter(hwTree);
				hwTreeBTN.setDisable(true);
				dashboardBTN.setDisable(false);
			}
		});

		settingsBTN = new Button();
		settingsBTN.setText("Settings");
		settingsBTN.setFont(Font.font("Helvetica", FontWeight.BOLD, 15));
		settingsBTN.setBackground(buttonBackground);
		settingsBTN.setTextFill(Color.valueOf("0x37b3fcff"));

		aboutBTN = new Button();
		aboutBTN.setText("About");
		aboutBTN.setFont(Font.font("Helvetica", FontWeight.BOLD, 15));
		aboutBTN.setBackground(buttonBackground);
		aboutBTN.setTextFill(Color.valueOf("0x37b3fcff"));

		aboutBTN.setOnAction(e -> about());

		AnchorPane apLeft = new AnchorPane();
		HBox.setHgrow(apLeft, Priority.ALWAYS);	// pro posunuti Up-Timu doprava
		topBarHB.getChildren().addAll(dashboardBTN, hwTreeBTN, settingsBTN, aboutBTN);

		topBarHB.setPadding(new Insets(5));
		topBarHB.setBackground(DARKER_BACKGROUND);

		AnchorPane apRight = new AnchorPane();

		upTimeLabel = new Label();
		upTimeLabel.setAlignment(Pos.CENTER_RIGHT);
		upTimeLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));
		upTimeLabel.setTextFill(Color.valueOf("0x37b3fcff"));
		upTimeLabel.setPadding(new Insets(5));

		HBox upTimeHB = new HBox(upTimeLabel);
		upTimeHB.setAlignment(Pos.CENTER_RIGHT);
		upTimeHB.setBackground(DARKER_BACKGROUND);

		apRight.getChildren().addAll(upTimeHB);
		topPaneHB.getChildren().addAll(topBarHB, apLeft, apRight);
		topPaneHB.setSpacing(5);
		topPaneHB.setBackground(DARKER_BACKGROUND);
		topPaneHB.setAlignment(Pos.CENTER);

		return topPaneHB;
	}

	/**
	 * Zobrazi informacni okno o autorovi
	 */
	private void about() {
		String aboutAuthorSB =
				"Author:\t\t\tDominik Nedved\n" +
				"Study number:\t\tA22B0109P\n" +
				"Discord:\t\t\tNedvi#5232\n" +
				"\nFaculty of Applied Sciences\n" +
				"University of West Bohemia";

		Alert aboutAlert = new Alert(Alert.AlertType.INFORMATION);
		aboutAlert.setTitle("About");
		aboutAlert.setHeaderText("About");
		aboutAlert.setContentText(aboutAuthorSB);
		DialogPane alertDP = aboutAlert.getDialogPane();
		alertDP.getStylesheets().add(Config.ACTIVE_STYLE_SHEET);
		alertDP.getStyleClass().add("alertDP");

		aboutAlert.show();
		aboutBTN.setDisable(true);
		aboutAlert.setOnCloseRequest(e -> aboutBTN.setDisable(false));
	}

	/**
	 * Spusti a formatuje Up-Time
	 */
	private void startUpTime() {
		upTimeTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				javafx.application.Platform.runLater(new Runnable() {
					@Override
					public void run() {
						upTimeLabel.setText(String.format("Up-Time: %02d:%02d:%02d", hour, min, sec));
						if (sec<59 && min<59) {
							sec++;
						} else if (sec==59 && min<59) {
							min++;
							sec = 0;
						} else {
							hour++;
							min = 0;
							sec = 0;
						}
					}
				});
			}
		}, 0, 1000);
	}

}