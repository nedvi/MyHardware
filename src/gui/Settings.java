package gui;

import dataModel.UpTimeData;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import org.controlsfx.control.ToggleSwitch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Trida reprezentujci panel s nastavenim programu.
 * Umoznuje:
 *  zmenu motivu,
 *  reset timeru + namerenych prumernych, min a max hodnot CPU a GPU,
 *  ukonceni programu.
 *
 * @author Dominik Nedved
 * @version 2023.06.26
 */
public class Settings extends BorderPane {

    /** Datovy model Up-Timu */
    private final UpTimeData upTimeData;

    /**
     * Konstruktor
     *
     * @param upTimeData predany datovy model Up-Timu
     */
    public Settings(UpTimeData upTimeData) {
        super();
        this.setPadding(new Insets(5));
        this.getStylesheets().add(Config.ACTIVE_STYLE_SHEET.get());
	    this.getStyleClass().add("settings");

        this.upTimeData = upTimeData;

        setCenter(settingsPane());
    }

    /**
     * @return Node s nastavenim programu
     */
    private Node settingsPane() {
        VBox settingsVB = new VBox();

        settingsVB.getStylesheets().add(Config.ACTIVE_STYLE_SHEET.get());
        settingsVB.getStyleClass().add("settings");

        VBox uiVB = new VBox();

        uiVB.getStylesheets().add(Config.ACTIVE_STYLE_SHEET.get());
        uiVB.getStyleClass().add("settings-sub-panel");

        Label uiLabel = new Label("UI");
        uiLabel.setFont(new Font("Helvetica", 25));

        Line line = new Line();
        line.setStartX(200);
        line.setStartY(300);
        line.setEndX(800);
        line.setEndY(300);
        line.setStyle("-fx-stroke: WHITE;");

        HBox darkModeHB = new HBox();

        Label darkModeLabel = new Label("Dark mode");
        darkModeLabel.setFont(new Font("Helvetica", 20));
        darkModeLabel.setTextFill(Color.WHITE);

        ToggleSwitch darkModeSwitch = new ToggleSwitch();
        darkModeSwitch.setSelected(true);
        darkModeSwitch.setPadding(new Insets(8));

        darkModeHB.getChildren().addAll(darkModeLabel, darkModeSwitch);
        darkModeHB.setPadding(new Insets(5));
        darkModeHB.setAlignment(Pos.TOP_CENTER);

        uiVB.getChildren().addAll(uiLabel, line, darkModeHB);
        uiVB.setAlignment(Pos.TOP_CENTER);

        //====================================================================================

        VBox uptimeAndMeasuresVB = new VBox();

        uptimeAndMeasuresVB.getStylesheets().add(Config.ACTIVE_STYLE_SHEET.get());
        uptimeAndMeasuresVB.getStyleClass().add("settings-sub-panel");

        Label uptimeAndMeasuresLabel = new Label("Up-Time and measured values");
        uptimeAndMeasuresLabel.setFont(new Font("Helvetica", 25));

        Line line2 = new Line();
        line2.setStartX(200);
        line2.setStartY(300);
        line2.setEndX(800);
        line2.setEndY(300);
        line2.setStyle("-fx-stroke: WHITE;");

        HBox resetUpTimeAndValuesHB = new HBox();

        Label resetUpTimeAndValuesLabel = new Label("Reset up-time and measured values ");
        resetUpTimeAndValuesLabel.setFont(new Font("Helvetica", 20));
        resetUpTimeAndValuesLabel.setTextFill(Color.WHITE);

        Button resetUpTimeAndValuesBTN = new Button("Reset");
        resetUpTimeAndValuesBTN.setAlignment(Pos.CENTER);
        resetUpTimeAndValuesBTN.setOnAction(event -> resetUpTimeAndValuesBTNAction());

        resetUpTimeAndValuesHB.setPadding(new Insets(5));
        resetUpTimeAndValuesHB.setAlignment(Pos.TOP_CENTER);
        resetUpTimeAndValuesHB.getChildren().addAll(resetUpTimeAndValuesLabel, resetUpTimeAndValuesBTN);

        uptimeAndMeasuresVB.getChildren().addAll(uptimeAndMeasuresLabel, line2, resetUpTimeAndValuesHB);
        uptimeAndMeasuresVB.setAlignment(Pos.TOP_CENTER);

        //====================================================================================

        VBox programManageVB = new VBox();

        programManageVB.getStylesheets().add(Config.ACTIVE_STYLE_SHEET.get());
        programManageVB.getStyleClass().add("settings-sub-panel");

        Label programManageLabel = new Label("Program manage");
        programManageLabel.setFont(new Font("Helvetica", 25));

        Line line3 = new Line();
        line3.setStartX(200);
        line3.setStartY(300);
        line3.setEndX(800);
        line3.setEndY(300);
        line3.setStyle("-fx-stroke: WHITE;");

        Button exitBTN = new Button("Exit");
        exitBTN.setAlignment(Pos.CENTER);
        exitBTN.setOnAction(event -> exitBTNAction());

        Button restartBTN = new Button("Restart");
        restartBTN.setAlignment(Pos.CENTER);
        restartBTN.setOnAction(event -> {
            try {
                restartBTNAction();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        programManageVB.getChildren().addAll(programManageLabel, line3, exitBTN, restartBTN);
        programManageVB.setAlignment(Pos.TOP_CENTER);
        programManageVB.setPadding(new Insets(5));
        programManageVB.setSpacing(5);

        //====================================================================================

        settingsVB.getChildren().addAll(uiVB, uptimeAndMeasuresVB, programManageVB);
        settingsVB.setPadding(new Insets(10, 150, 10, 150));
        settingsVB.setAlignment(Pos.TOP_CENTER);
        settingsVB.setSpacing(10);

        darkModeSwitch.selectedProperty().addListener(event -> {
            if (Config.ACTIVE_STYLE_SHEET.get().equals(Config.DARK_STYLE_SHEET)) {
                Config.ACTIVE_STYLE_SHEET.set(Config.LIGHT_STYLE_SHEET);
                Config.setBrightStyle();
                line.setStyle("-fx-stroke: BLACK;");
                line2.setStyle("-fx-stroke: BLACK;");
                line3.setStyle("-fx-stroke: BLACK;");
            } else {
                Config.ACTIVE_STYLE_SHEET.set(Config.DARK_STYLE_SHEET);
                Config.setDarkStyle();
                line.setStyle("-fx-stroke: WHITE;");
                line2.setStyle("-fx-stroke: WHITE;");
                line3.setStyle("-fx-stroke: WHITE;");
            }
            this.getStylesheets().clear();
            this.getStylesheets().add(Config.ACTIVE_STYLE_SHEET.get());
            this.getStyleClass().add("settings");

            settingsVB.getStylesheets().clear();
            settingsVB.getStylesheets().add(Config.ACTIVE_STYLE_SHEET.get());
            settingsVB.getStyleClass().add("settings");

            uiVB.getStylesheets().clear();
            uiVB.getStylesheets().add(Config.ACTIVE_STYLE_SHEET.get());
            uiVB.getStyleClass().add("setting-sub-panel");

            uptimeAndMeasuresVB.getStylesheets().clear();
            uptimeAndMeasuresVB.getStylesheets().add(Config.ACTIVE_STYLE_SHEET.get());
            uptimeAndMeasuresVB.getStyleClass().add("settings-sub-panel");

            programManageVB.getStylesheets().clear();
            programManageVB.getStyleClass().add("settings-sub-panel");
            programManageVB.getStylesheets().add(Config.ACTIVE_STYLE_SHEET.get());
            programManageVB.getStyleClass().add("settings-sub-panel");
        });

        return settingsVB;
    }

    /**
     * Akce pro tlacitko resetUpTimeAndValuesBTN.
     * Resetne Up-Time a zaroven vynuluje namerene prumerne, min a max hodnoty CPU a GPU
     */
    private void resetUpTimeAndValuesBTNAction() {
        upTimeData.setHour(0);
        upTimeData.setMin(0);
        upTimeData.setSec(0);

        CPUPanel.cpuUsages = new ArrayList<>();
        CPUPanel.minCpuUsage[0] = 0;
        CPUPanel.maxCpuUsage[0] = 0;

        CPUPanel.cpuTemps = new ArrayList<>();
        CPUPanel.minCpuTemp[0] = 0;
        CPUPanel.maxCpuTemp[0] = 0;

        GPUPanel.gpuUsages = new ArrayList<>();
        GPUPanel.minGpuUsage[0] = 0;
        GPUPanel.maxGpuUsage[0] = 0;

        GPUPanel.gpuTemps = new ArrayList<>();
        GPUPanel.minGpuTemp[0] = 0;
        GPUPanel.maxGpuTemp[0] = 0;
    }

    /**
     * Akce pro tlacitko exitBTN.
     * Vytvori potvrzovaci dialog - kdyz ano, program se ukonci, kdyz ne ukonceni se prerusi a program pokracuje v behu.
     */
    private void exitBTNAction() {
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
            exitAlert.close();
        }
    }

    /**
     * Akce pro tlacitko restartBTN.
     * Vytvori potvrzovaci dialog - kdyz ano, program se restartuje, kdyz ne restart se prerusi a program pokracuje v behu.
     */
    private void restartBTNAction() throws IOException {
        String exitStr = "Are you sure you want to restart the program?";

        Alert restartAlert = new Alert(Alert.AlertType.CONFIRMATION);
        restartAlert.setTitle("Restart");
        restartAlert.setHeaderText("Restart");
        restartAlert.setContentText(exitStr);

        DialogPane alertDP = restartAlert.getDialogPane();
        alertDP.getStylesheets().add(Config.ACTIVE_STYLE_SHEET.get());
        alertDP.getStyleClass().add("alertDP");

        Optional<ButtonType> result = restartAlert.showAndWait();
        if (result.get() == ButtonType.OK){
            Runtime.getRuntime().exec("cmd /c Launch.bat");
            Platform.exit();
            System.exit(0);
        } else {
            restartAlert.close();
        }
    }

}
