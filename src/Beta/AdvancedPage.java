package Beta;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import oshi.SystemInfo;

import java.util.Collection;


public class AdvancedPage extends BorderPane {
    /**
     * Instance systemeovych informaci
     */
    private SystemInfo si = new SystemInfo();

    private TreeView<Object> componentTreeView;

    public Component componentCpu = new Component(Component.componentType.CPU);

    public AdvancedPage() throws Exception {
        super();
        init();
        /*
        VBox cpuVB = new VBox();
        Label cpuLabel = new Label("CPU: " + si.getHardware().getProcessor().toString());
        cpuLabel.setStyle("-fx-text-fill: WHITE;");
        cpuLabel.setFont(Font.font("Helvetica", 15));
        cpuVB.getChildren().addAll(cpuLabel);
         */
    }

    public void init() throws Exception {
        System.out.println("Advanced Page initialization...");

        setCenter(hwSpecs());
        setLeft(getTree());
    }

    public Node hwSpecs() {
        VBox cpuVB = new VBox();
        Label cpuLabel = new Label("CPU: " + si.getHardware().getProcessor().toString());
        cpuLabel.setStyle("-fx-text-fill: BLACK;");
        cpuLabel.setFont(Font.font("Helvetica", 15));
        cpuVB.getChildren().addAll(cpuLabel);

        return cpuVB;
    }

    public Node getTree() {
        componentTreeView = new TreeView<Object>();

        TreeItem<Object> cpuTreeItem = new TreeItem<Object>(componentCpu);
            Label cpuLabel = new Label("Ahoj");
        cpuTreeItem.getChildren().addAll();
        componentTreeView.setRoot(cpuTreeItem);
        return componentTreeView;
    }

    public Label getCpus() {
        Label cpuLabel = new Label(si.getHardware().getProcessor().getPhysicalProcessors().get(0).toString());
        return cpuLabel;
    }
}
