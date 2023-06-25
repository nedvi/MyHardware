package alpha;

import alpha.dataModel.Component;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.*;
import oshi.SystemInfo;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HWDiskStore;
import oshi.hardware.PowerSource;
import oshi.hardware.UsbDevice;

import java.util.List;


public class HWTree extends BorderPane {

    /** Atribut pristupu k informacich o systemu */
    private final SystemInfo si = new SystemInfo();

    /** Harwarovy strom */
    private TreeView<String> componentTreeView;

    public Component components;

    private VBox componentInfoVB;

    private Label shownComponentLabel = new Label();

    private String getComponentDescription(String componentCategory, String componentName) {
        if (componentName.equals("Operating system")) {
            return components.getOsDescription();
        } else if (componentCategory.equals("CPU")) {
            return components.getCpuDescription();
        } else if (componentCategory.equals("GPU")) {
            return components.getGpuDescription(componentName);
        } else if (componentName.equals("RAM")) {
            return components.getRamDescription();
        } else if (componentCategory.equals("Disks")) {
            return components.getDiskDescription(componentName);
        } else if (componentCategory.equals("Power sources")) {
            return components.getPowerSourceDescription(componentName);
        } else if (componentCategory.equals("USB devices")) {
            return components.getUsbDeviceDescription(componentName);
        } else {
            return "";
        }
    }

    public HWTree() {
        super();
        this.setPadding(new Insets(5));


        init();

        componentTreeView.setOnMouseReleased(event -> {
            TreeItem<String> focusedTI = componentTreeView.getSelectionModel().getSelectedItem();
            try {
                shownComponentLabel.setText(getComponentDescription(focusedTI.getParent().getValue(), focusedTI.getValue()));
//                shownComponentLabel.setFont(Font.font("Helvetica", 15));
                shownComponentLabel.setPadding(new Insets(10));


            } catch (Exception e) {
//                System.out.println("Nebyla vybrana zadna polozka stromu.");
            }
        });
    }

    public void init() {
        System.out.println("HW Tree initialization...");

        components = new Component();

        componentTreeView = new TreeView<>();

        setCenter(hwSpecs());
        setLeft(getTree());
    }

    public Node hwSpecs() {
        componentInfoVB = new VBox();

        componentInfoVB.setSpacing(5);
        componentInfoVB.getChildren().addAll(shownComponentLabel);
//        componentInfoVB.setBackground(new Background(new BackgroundFill(Color.valueOf("#1c1c1b"), new CornerRadii(5), null)));

        return componentInfoVB;
    }

    public Node getTree() {
        // Root
        TreeItem<String> rootTI = new TreeItem<>(components.getSystemName());
        rootTI.setExpanded(true);

        // OS
        TreeItem<String> osTI = new TreeItem<>("Operating system");

        // CPU
        TreeItem<String> cpuCategoryTI = new TreeItem<>("CPU");
            TreeItem<String> exactCpuTI = new TreeItem<>(si.getHardware().getProcessor().getProcessorIdentifier().getName());
            cpuCategoryTI.getChildren().add(exactCpuTI);

        // GPU
        TreeItem<String> gpuCategoryTI = new TreeItem<>("GPU");
        List<GraphicsCard> gpuList = si.getHardware().getGraphicsCards();
        for (GraphicsCard graphicsCard : gpuList) {
            TreeItem<String> exactGpuTI = new TreeItem<>(graphicsCard.getName());
            gpuCategoryTI.getChildren().add(exactGpuTI);
        }

        // RAM
        TreeItem<String> ramCategoryTI = new TreeItem<>("RAM");

        // Disks
        TreeItem<String> diskCategoryTI = new TreeItem<>("Disks");
        for (HWDiskStore disk : si.getHardware().getDiskStores()) {
            TreeItem<String> exactDiskTI = new TreeItem<>(disk.getModel());
            diskCategoryTI.getChildren().add(exactDiskTI);
        }

        // Power sources
        TreeItem<String> powerSourceCategoryTI = new TreeItem<>("Power sources");
        List<PowerSource> powerSourceList = si.getHardware().getPowerSources();
        for (PowerSource powerSource : powerSourceList) {
            TreeItem<String> exactPowerSourceTI = new TreeItem<>(powerSource.getName());
            powerSourceCategoryTI.getChildren().add(exactPowerSourceTI);
        }

        TreeItem<String> usbDevicesCategoryTI = new TreeItem<>("USB devices");
        List<UsbDevice> usbDevices = si.getHardware().getUsbDevices(true);
        for (UsbDevice usbDevice : usbDevices) {
            TreeItem<String> exactUsbDeviceTI = new TreeItem<>(usbDevice.getName());
            usbDevicesCategoryTI.getChildren().add(exactUsbDeviceTI);
        }

        rootTI.getChildren().addAll(osTI, cpuCategoryTI, gpuCategoryTI, ramCategoryTI, diskCategoryTI, powerSourceCategoryTI, usbDevicesCategoryTI);

        componentTreeView.setRoot(rootTI);
        componentTreeView.setMinWidth(450);

        return componentTreeView;
    }

    /**
     * Provede refresh style sheetu CSS.
     * Vola se pokazde pri prepnuti konkretniho panelu. Je tak osetreno, ze style sheet bude aktualni pri zmene motivu.
     */
    public void refreshStyleSheet() {
        this.getStylesheets().clear();
        this.getStylesheets().add(Config.ACTIVE_STYLE_SHEET.get());
        this.getStyleClass().add("hwTree");

        componentTreeView.getStylesheets().clear();
        componentTreeView.getStylesheets().add(Config.ACTIVE_STYLE_SHEET.get());

        componentInfoVB.getStylesheets().clear();
        componentInfoVB.getStylesheets().add(Config.ACTIVE_STYLE_SHEET.get());
        componentInfoVB.getStyleClass().add("hwTreeComponentInfo");
    }
}
