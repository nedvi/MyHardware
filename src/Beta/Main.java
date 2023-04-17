package Beta;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import oshi.SystemInfo;
import oshi.hardware.GraphicsCard;

import java.util.List;

/**
 * Hlavni trida s resenim zadani semestralni prace (ZCU-FAV-UUR)
 *
 * @author Dominik Nedved, A22B0109P
 * @version 2023.04.17
 */
public class Main extends Application {
	// konstanty titulku okna
	private static final String MAIN_TITLE_STR = "MyHardware";

	// reference pro Primary Stage
	private Stage primaryStage;
	private BorderPane rootPaneBP;
	//-------------------------------------------------------------------------------
	private Background background;
	private BackgroundFill bFill;
	private static final BackgroundFill tileBFill = new BackgroundFill(Color.valueOf("#1c1c1b"), null, null);
	private static final Background DARKER_BACKGROUND = new Background(tileBFill);

	private Background buttonBackground;

	private BackgroundFill buttonFill;

	public static SystemInfo si;
	public static CPUPanel cpuPanel;
	public static GPUPanel gpuPanel;
	public static RAMPanel ramPanel;
	public static NetworkPanel networkPanel;
	public static List<GraphicsCard> gpuList;
	public static Button dashboardBTN = new Button();
	public static Button advancedBTN;
	public static Button settingsBTN;
	public static Button aboutBTN;

	private static AdvancedPage advancedPage;

	//-------------------------------------------------------------------------------
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
	public void init() throws Exception {
		System.out.println("Initialization...");

		si = new SystemInfo();
		cpuPanel = new CPUPanel(si);
		gpuPanel = new GPUPanel(si);
		ramPanel = new RAMPanel(si);
		networkPanel = new NetworkPanel(si);

		advancedPage = new AdvancedPage();

		gpuList = si.getHardware().getGraphicsCards();

		bFill = new BackgroundFill(Color.valueOf("0x2a2a2aff"), null, null);
		background = new Background(bFill);
		buttonFill = new BackgroundFill(Color.valueOf("#525252"), new CornerRadii(2), new Insets(1));
		buttonBackground = new Background(buttonFill);
		dashboardBTN.setDisable(true);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle(MAIN_TITLE_STR);
		this.primaryStage.setScene(getScene());

		// minimalni velikost okna
		this.primaryStage.setMinWidth(1000);
		this.primaryStage.setMinHeight(700);

		this.primaryStage.show();

		// zavreni vsech vlaken po zavreni aplikace
		this.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {
				Platform.exit();
				System.exit(0);
			}
		});
	}

	/**
	 * nastaveni sceny
	 * @return scene
	 */
	private Scene getScene() {
		Scene scene = new Scene(getRoot(), 1000, 700);
		//scene.getStylesheets().add("styles.css");
		return scene;
	}

	/**
	 * korenovy panel, umistujeme na nej jednotlive komponenty
	 * @return rootPaneBP
	 */
	private Parent getRoot() {
		rootPaneBP = new BorderPane();
		rootPaneBP.setTop(getTopBar());
		rootPaneBP.setCenter(getDashboard());
		return rootPaneBP;
	}

	/**
	 * Metoda pro zobrazeni vrchniho baru s prepinanim jednotlivych sekciv
	 * @return topBar
	 */
	private Node getTopBar() {
		VBox topPaneVB = new VBox();

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
		if (rootPaneBP.getCenter()!=getDashboard()) {
			rootPaneBP.setCenter(getDashboard());
			dashboardBTN.setDisable(true);
			advancedBTN.setDisable(false);
		}
		});

		advancedBTN = new Button();
		advancedBTN.setText("Advanced");
		advancedBTN.setFont(Font.font("Helvetica", FontWeight.BOLD, 15));
		advancedBTN.setBackground(buttonBackground);
		advancedBTN.setTextFill(Color.valueOf("0x37b3fcff"));

		advancedBTN.setOnAction(e -> {
			if (rootPaneBP.getCenter()!=hwSpecs()) {
				rootPaneBP.setCenter(advancedPage);
				advancedBTN.setDisable(true);
				dashboardBTN.setDisable(false);
				//rootPaneBP.setLeft(advancedPage);
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

		topBarHB.getChildren().addAll(dashboardBTN, advancedBTN, settingsBTN, aboutBTN);

		topBarHB.setPadding(new Insets(5));
		topBarHB.setBackground(DARKER_BACKGROUND);
		//menuBarMB.setStyle(";

		topPaneVB.getChildren().addAll(topBarHB);	//TITLE zde
		//topPaneVB.setPadding(new Insets(5));
		topPaneVB.setSpacing(5);
		topPaneVB.setAlignment(Pos.CENTER);
		topPaneVB.setBackground(background);

		return topPaneVB;
	}

	/**
	 *
	 * @return
	 */
	private Node getDashboard() {
		HBox dashBoardPaneVB = new HBox();	// vertical

			VBox dbCpuPlusRamHB = new VBox();	// horizontal
			dbCpuPlusRamHB.getChildren().addAll(cpuPanel, ramPanel);
			dbCpuPlusRamHB.setPadding(new Insets(5));
			dbCpuPlusRamHB.setSpacing(5);
			dbCpuPlusRamHB.setAlignment(Pos.TOP_CENTER);
			dbCpuPlusRamHB.setBackground(background);

			VBox dbGpuPlusNetworkHB = new VBox();	// horizontal
			dbGpuPlusNetworkHB.getChildren().addAll(gpuPanel, networkPanel);
			dbGpuPlusNetworkHB.setPadding(new Insets(5));
			dbGpuPlusNetworkHB.setSpacing(5);
			dbGpuPlusNetworkHB.setAlignment(Pos.TOP_CENTER);
			dbGpuPlusNetworkHB.setBackground(background);;

			dashBoardPaneVB.getChildren().addAll(dbCpuPlusRamHB, dbGpuPlusNetworkHB);
			dashBoardPaneVB.setPadding(new Insets(5));
			dashBoardPaneVB.setSpacing(5);
			dashBoardPaneVB.setAlignment(Pos.CENTER);
			dashBoardPaneVB.setBackground(background);

		return dashBoardPaneVB;
	}

	public Node hwSpecs() {
		VBox cpuVB = new VBox();

		Label cpuLabel = new Label("CPU\n--------------------------");
		cpuLabel.setStyle("-fx-text-fill: WHITE;");
		cpuLabel.setFont(Font.font("Helvetica", 25));

		Label cpuSpecsLabel = new Label(si.getHardware().getProcessor().toString());
		cpuSpecsLabel.setStyle("-fx-text-fill: WHITE;");
		cpuSpecsLabel.setFont(Font.font("Helvetica", 15));

		Label gpuLabel = new Label("GPU\n--------------------------");
		gpuLabel.setStyle("-fx-text-fill: WHITE;");
		gpuLabel.setFont(Font.font("Helvetica", 25));

		Label gpuSpecsLabel = new Label();
		String gpus;
		String gpusSpecs = "";
		for (int i = 0; i < gpuList.size(); i++) {
			gpus = (gpuList.get(i).getName()) + "\n" +
					gpuList.get(i).getVendor() + "\n" +
					gpuList.get(i).getVRam()/1000000000.00 + " GB\n" +
					gpuList.get(i).getVersionInfo() + "\n" +
					gpuList.get(i).getDeviceId() + "\n";
			gpusSpecs += gpus + "\n";
		}
		gpuSpecsLabel.setText(gpusSpecs);

		gpuSpecsLabel.setStyle("-fx-text-fill: WHITE;");
		gpuSpecsLabel.setFont(Font.font("Helvetica", 15));


		cpuVB.getChildren().addAll(cpuLabel, cpuSpecsLabel, gpuLabel, gpuSpecsLabel);
		cpuVB.setPadding(new Insets(5));
		cpuVB.setSpacing(5);
		cpuVB.setBackground(background);

		return cpuVB;
	}
}
