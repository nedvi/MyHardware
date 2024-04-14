package gui;

import dataModel.LoadAndTemp;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import oshi.SystemInfo;

/**
 * Trida reprezentujici vychozi dashboard - tedy to, co uzivatel vidi jako prvni pri spusteni aplikace
 *
 * @author Dominik Nedved
 * @version 2023.05.11
 */
public class Dashboard extends BorderPane {

    //======================== Data ========================
    private final LoadAndTemp loadAndTemp;
    private final SystemInfo si;

    //======================== Sekce ========================
    private CPUPanel cpuPanel;
    private GPUPanel gpuPanel;
    private RAMPanel ramPanel;
    private NetworkPanel networkPanel;

    //======================== Nodes ========================
    private HBox dashBoardPaneVB;
    private VBox cpuRamHB;
    private VBox gpuNetworkHB;

    //===============================================================

    /**
     * Konstruktor Dashboardu
     *
     * @param si predany parametr SystemInfa
     */
    public Dashboard(SystemInfo si, LoadAndTemp loadAndTemp) {
        this.si = si;
        this.loadAndTemp = loadAndTemp;
        System.out.println("Dashboard initialization...");
        setCenter(getDashboard());
    }

    /**
     * Vytvoreni Dashbardu.
     * Vytvari se zde i instance panelu komponent CPU, GPU, RAM a sitoveho pripojeni.
     *
     * @return dashboard
     */
    private Node getDashboard() {
        dashBoardPaneVB = new HBox();

        cpuPanel = new CPUPanel(si, loadAndTemp);
        gpuPanel = new GPUPanel(loadAndTemp);
        ramPanel = new RAMPanel(si);
        networkPanel = new NetworkPanel(si);

        cpuRamHB = new VBox();
        cpuRamHB.getChildren().addAll(cpuPanel, ramPanel);
        cpuRamHB.setPadding(new Insets(5));
        cpuRamHB.setSpacing(5);
        cpuRamHB.setAlignment(Pos.TOP_CENTER);
        cpuRamHB.setBackground(Config.BACKGROUND);

        gpuNetworkHB = new VBox();
        gpuNetworkHB.getChildren().addAll(gpuPanel, networkPanel);
        gpuNetworkHB.setPadding(new Insets(5));
        gpuNetworkHB.setSpacing(5);
        gpuNetworkHB.setAlignment(Pos.TOP_CENTER);
        gpuNetworkHB.setBackground(Config.BACKGROUND);

        dashBoardPaneVB.getChildren().addAll(cpuRamHB, gpuNetworkHB);
        dashBoardPaneVB.setPadding(new Insets(5));
        dashBoardPaneVB.setSpacing(5);
        dashBoardPaneVB.setAlignment(Pos.CENTER);

        return dashBoardPaneVB;
    }

    /**
     * Provede refresh stylu.
     * Vola se pokazde pri prepnuti konkretniho panelu.
     */
    public void refreshStyleSheet() {
        dashBoardPaneVB.setBackground(Config.DASHBOARD_BACKGROUND);
        cpuRamHB.setBackground(Config.DASHBOARD_BACKGROUND);
        gpuNetworkHB.setBackground(Config.DASHBOARD_BACKGROUND);

        cpuPanel.refreshStyleSheet();
        gpuPanel.refreshStyleSheet();
        ramPanel.refreshStyleSheet();
        networkPanel.refreshStyleSheet();
    }
}
