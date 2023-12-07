import dataModel.LoadAndTemp;
import dataModel.UpTimeData;
import gui.Config;
import gui.Dashboard;
import gui.HWTree;
import gui.Settings;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import oshi.SystemInfo;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Hlavni trida s resenim zadani semestralni prace (ZCU-FAV-UUR)
 * Ucel programu je monitoring HW pocitace
 *
 * @author Dominik Nedved, A22B0109P
 * @version 2023.06.26
 */
public class MyHardware extends Application {

	//================================== Konstanty titulku okna ==================================
	private final String MAIN_TITLE_STR = "MyHardware - SP UUR (Dominik Nedved, A22B0109P)";
	private final Image TITLE_LOGO = new Image("img/myhw-256x256.png");
	private Stage primaryStage;
	private BorderPane rootPaneBP;

	//=================================== Pristup k datum ===================================

	public SystemInfo si;
	private LoadAndTemp loadAndTemp;

	//=================================== Tlacitka ===================================

	public Button dashboardBTN = new Button();
	public Button hwTreeBTN;
	public Button settingsBTN;
	public Button aboutBTN;

	//=================================== Jednotlive sekce ===================================

	private Dashboard dashboard;
	private HWTree hwTree;
	private Settings settings;

	//=================================== Up-Time ===================================
	private UpTimeData upTimeData;
	private Label upTimeLabel = new Label("00:00:00");
	private final Timer upTimeTimer = new Timer();


	//========================================================================================
	/**
	 * Spousteci metoda
	 *
	 * @param args parametry prikazove radky
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Inicializace.
	 */
	@Override
	public void init() {
		System.out.println("Initialization...");

		upTimeData = new UpTimeData();

		si = new SystemInfo();

		loadAndTemp = new LoadAndTemp();
		Thread loadAndTempThread = new Thread(loadAndTemp);
		System.out.println("Load nad temp " + loadAndTempThread.getName());
		loadAndTempThread.start();

		dashboardBTN.setDisable(true);
		dashboard = new Dashboard(si, loadAndTemp);
		hwTree = new HWTree(si);
		settings = new Settings(upTimeData);
		startUpTime();
	}

	/**
	 * @param primaryStage the primary stage for this application, onto which
	 * the application scene can be set.
	 * Applications may create other stages, if needed, but they will not be
	 * primary stages.
	 */
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle(MAIN_TITLE_STR);
		this.primaryStage.getIcons().add(TITLE_LOGO);
		this.primaryStage.setScene(getScene());

		// minimalni velikost okna
		this.primaryStage.setMinWidth(1010);
		this.primaryStage.setMinHeight(705);

		this.primaryStage.setResizable(false);

		this.primaryStage.show();

		// zavola alert okno s potvrzenim ukonceni programu
		this.primaryStage.setOnCloseRequest(event -> exitBTNAction(event));
	}

	/**
	 * Nastaveni sceny
	 *
	 * @return scene
	 */
	private Scene getScene() {
		return new Scene(getRoot(), 1000, 700);
	}

	/**
	 * Korenovy panel, umistujeme na nej jednotlive komponenty
	 *
	 * @return rootPaneBP
	 */
	private Parent getRoot() {
		rootPaneBP = new BorderPane();
		rootPaneBP.setTop(getTopBar());
		rootPaneBP.setCenter(dashboard);
		dashboard.refreshStyleSheet();
		return rootPaneBP;
	}

	/**
	 * Metoda pro zobrazeni vrchniho baru s prepinanim jednotlivych sekci
	 *
	 * @return topBar
	 */
	private Node getTopBar() {
		HBox topPaneHB = new HBox();
		topPaneHB.getStylesheets().add(Config.ACTIVE_STYLE_SHEET.get());
		topPaneHB.getStyleClass().add("menu");

		// Pridani listeneru pro zmeneni motivu okamzite po nastaveni uzivatelem
		Config.ACTIVE_STYLE_SHEET.addListener(observable -> {
			topPaneHB.getStylesheets().clear();
			topPaneHB.getStylesheets().add(Config.ACTIVE_STYLE_SHEET.get());
			topPaneHB.getStyleClass().add("menu");
		});

		dashboardBTN.setText("Dashboard");
		dashboardBTN.setOnAction(e -> {
			if (rootPaneBP.getCenter() != dashboard) {
				dashboard.refreshStyleSheet();
				rootPaneBP.setCenter(dashboard);
				dashboardBTN.setDisable(true);
				hwTreeBTN.setDisable(false);
				settingsBTN.setDisable(false);
			}
		});

		hwTreeBTN = new Button();
		hwTreeBTN.setText("HW Tree");
		hwTreeBTN.setOnAction(e -> {
			if (rootPaneBP.getCenter() != hwTree) {
				hwTree.refreshStyleSheet();	// zavola update styleSheetu
				rootPaneBP.setCenter(hwTree);
				hwTreeBTN.setDisable(true);
				dashboardBTN.setDisable(false);
				settingsBTN.setDisable(false);
			}
		});

		settingsBTN = new Button();
		settingsBTN.setText("Settings");
		settingsBTN.setOnAction(e -> {
			if (rootPaneBP.getCenter() != settings) {
				rootPaneBP.setCenter(settings);
				settingsBTN.setDisable(true);
				dashboardBTN.setDisable(false);
				hwTreeBTN.setDisable(false);
			}
		});

		aboutBTN = new Button();
		aboutBTN.setText("About");
		aboutBTN.setOnAction(e -> about());

		AnchorPane apLeft = new AnchorPane();
		HBox.setHgrow(apLeft, Priority.ALWAYS);	// pro posunuti Up-Timu doprava

		upTimeLabel = new Label();
		AnchorPane apRight = new AnchorPane();
		apRight.getChildren().addAll(upTimeLabel);

		topPaneHB.getChildren().addAll(dashboardBTN, hwTreeBTN, settingsBTN, aboutBTN, apLeft, apRight);
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
				"Discord:\t\t\tnedvi\n" +
				"\nFaculty of Applied Sciences\n" +
				"University of West Bohemia";

		Alert aboutAlert = new Alert(Alert.AlertType.INFORMATION);
		aboutAlert.setTitle("About");
		aboutAlert.setHeaderText("About");
		aboutAlert.setContentText(aboutAuthorSB);

		DialogPane alertDP = aboutAlert.getDialogPane();
		alertDP.getStylesheets().add(Config.ACTIVE_STYLE_SHEET.get());
		alertDP.getStyleClass().add("alertDP");

		aboutBTN.setDisable(true);

		aboutAlert.show();
		aboutAlert.setOnCloseRequest(e -> aboutBTN.setDisable(false));
	}

	/**
	 * Akce pro tlacitko ukonceni programu (X).
	 * Vytvori potvrzovaci dialog - kdyz ano, program se ukonci, kdyz ne ukonceni se prerusi a program pokracuje v behu.
	 *
	 * @param event ukonceni programu
	 */
	private void exitBTNAction(WindowEvent event) {
		String exitStr = "Are you sure you want to exit the program?";

		Alert exitAlert = new Alert(Alert.AlertType.CONFIRMATION);
		exitAlert.setTitle("Exit");
		exitAlert.setHeaderText("Exit");
		exitAlert.setContentText(exitStr);

		DialogPane alertDP = exitAlert.getDialogPane();
		alertDP.getStylesheets().add(Config.ACTIVE_STYLE_SHEET.get());
		alertDP.getStyleClass().add("alertDP");

		Optional<ButtonType> result = exitAlert.showAndWait();
		if (result.get() == ButtonType.OK){
			Platform.exit();
			System.exit(0);
		} else {
			event.consume();	// prerusi ukonceni programu
			exitAlert.close();
		}
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
						upTimeLabel.setText(String.format("Up-Time: %02d:%02d:%02d", upTimeData.getHour(), upTimeData.getMin(), upTimeData.getSec()));
						if (upTimeData.getSec()<59 && upTimeData.getMin()<59) {
							upTimeData.setSec(upTimeData.getSec() + 1);
						} else if (upTimeData.getSec()==59 && upTimeData.getMin()<59) {
							upTimeData.setMin(upTimeData.getMin() + 1);
							upTimeData.setSec(0);
						} else {
							upTimeData.setHour(upTimeData.getHour() + 1);
							upTimeData.setMin(0);
							upTimeData.setSec(0);
						}
					}
				});
			}
		}, 0, 1000);
	}


}
