package alpha;

import alpha.dataModel.LoadAndTemp;
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
 * @author Dominik Nedved, A22B0109P
 * @version 2023.05.11
 */
public class Dashboard extends BorderPane {

    /** Instance SystemInfa */
    private final SystemInfo si;

    /** Instance */
    private final LoadAndTemp loadAndTemp;

    /**
     * Konstruktor Dashboardu
     *
     * @param si           predany parametr SystemInfa
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
        HBox dashBoardPaneVB = new HBox();

        CPUPanel cpuPanel = new CPUPanel(si, loadAndTemp);
        GPUPanel gpuPanel = new GPUPanel(loadAndTemp);
        RAMPanel ramPanel = new RAMPanel(si);
        NetworkPanel networkPanel = new NetworkPanel(si);

        VBox cpuRamHB = new VBox();
        cpuRamHB.getChildren().addAll(cpuPanel, ramPanel);
        cpuRamHB.setPadding(new Insets(5));
        cpuRamHB.setSpacing(5);
        cpuRamHB.setAlignment(Pos.TOP_CENTER);
        cpuRamHB.setBackground(Config.BACKGROUND);

        VBox gpuNetworkHB = new VBox();
        gpuNetworkHB.getChildren().addAll(gpuPanel, networkPanel);
        gpuNetworkHB.setPadding(new Insets(5));
        gpuNetworkHB.setSpacing(5);
        gpuNetworkHB.setAlignment(Pos.TOP_CENTER);
        gpuNetworkHB.setBackground(Config.BACKGROUND);;

        dashBoardPaneVB.getChildren().addAll(cpuRamHB, gpuNetworkHB);
        dashBoardPaneVB.setPadding(new Insets(5));
        dashBoardPaneVB.setSpacing(5);
        dashBoardPaneVB.setAlignment(Pos.CENTER);
        dashBoardPaneVB.setBackground(Config.BACKGROUND);

        return dashBoardPaneVB;
    }
}
