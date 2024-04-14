package gui;

import dataModel.Component;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.*;
import oshi.SystemInfo;
import oshi.hardware.*;

import java.util.List;

/**
 * Trida reprezentujici strom s informacemi o komponentech PC
 *
 * @author Dominik Nedved
 * @version 2023.06.26
 */
public class HWTree extends BorderPane {

    /** Harwarovy strom */
    private TreeView<String> componentTreeView;

    /** Datovy model popisu kamponentu */
    public Component components;

    /** Node, na ktery se vypisuji informace o HW */
    private VBox componentInfoVB;

    /** Label, ktery obsahuje informace o HW */
    private final Label shownComponentLabel = new Label();

    /** Atribut pristupu k informacich o systemu */
    private final SystemInfo si;

    /**
     * Konstruktor
     * @param si predana instance SystemInfa
     */
    public HWTree(SystemInfo si) {
        super();
        this.si = si;
        this.setPadding(new Insets(5));

        init();

        // Nastavi listener pro kliknuti mysi na konkretni bunku stromu a vypise jeji obsah
        componentTreeView.setOnMouseReleased(event -> {
            TreeItem<String> focusedTI = componentTreeView.getSelectionModel().getSelectedItem();
            try {
                shownComponentLabel.setText(getComponentDescription(focusedTI.getParent().getValue(), focusedTI.getValue()));
                shownComponentLabel.setPadding(new Insets(10));

            } catch (Exception e) {
//                System.out.println("Nebyla vybrana zadna polozka stromu.");
            }
        });
    }

    /**
     * Nacteni hodnot
     */
    public void init() {
        System.out.println("HW Tree initialization...");

        components = new Component(si);

        componentTreeView = new TreeView<>();

        setCenter(hwSpecs());
        setLeft(getTree());
    }

    /**
     * @return Node s informacemi o HW
     */
    public Node hwSpecs() {
        componentInfoVB = new VBox();

        componentInfoVB.setSpacing(5);
        componentInfoVB.getChildren().addAll(shownComponentLabel);
//        componentInfoVB.setBackground(new Background(new BackgroundFill(Color.valueOf("#1c1c1b"), new CornerRadii(5), null)));

        return componentInfoVB;
    }

    /**
     * @return strom s komponenty
     */
    public Node getTree() {
        // Root
        TreeItem<String> rootTI = new TreeItem<>(components.getSystemName());
        rootTI.setExpanded(true);

        // OS
        TreeItem<String> osTI = new TreeItem<>("Operating system");

        // Motherboard
        TreeItem<String> motherboardCategoryTI = new TreeItem<>("Motherboard");
        TreeItem<String> exactMotherboardTI = new TreeItem<>(
                si.getHardware().getComputerSystem().getBaseboard().getManufacturer() + " " + si.getHardware().getComputerSystem().getBaseboard().getModel());
        motherboardCategoryTI.getChildren().add(exactMotherboardTI);

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

        // USB devices
        TreeItem<String> usbDevicesCategoryTI = new TreeItem<>("USB devices");
        List<UsbDevice> usbDevices = si.getHardware().getUsbDevices(true);
        for (UsbDevice usbDevice : usbDevices) {
            TreeItem<String> exactUsbDeviceTI = new TreeItem<>(usbDevice.getName());
            usbDevicesCategoryTI.getChildren().add(exactUsbDeviceTI);
        }

        // Sound cards
        TreeItem<String> soundCardsCategoryTI = new TreeItem<>("Sound cards");
        List<SoundCard> soundCards = si.getHardware().getSoundCards();
        for (SoundCard soundCard : soundCards) {
            TreeItem<String> exactSoundCardTI = new TreeItem<>(soundCard.getName());
            soundCardsCategoryTI.getChildren().add(exactSoundCardTI);
        }

        rootTI.getChildren().addAll(osTI, motherboardCategoryTI, cpuCategoryTI, gpuCategoryTI, ramCategoryTI, diskCategoryTI, powerSourceCategoryTI, usbDevicesCategoryTI, soundCardsCategoryTI);

        componentTreeView.setRoot(rootTI);
        componentTreeView.setMinWidth(450);

        return componentTreeView;
    }

    /**
     * @param componentCategory typ komponentu
     * @param componentName nazev komponentu
     * @return popis konkretniho komponentu
     */
    private String getComponentDescription(String componentCategory, String componentName) {
        if (componentName.equals("Operating system")) {
            return components.getOsDescription();
        } else if (componentCategory.equals("Motherboard")) {
            return components.getMotherboardDescription();
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
        } else if (componentCategory.equals("Sound cards")) {
            return components.getSoundCardDescription(componentName);
        } else {
            return "";
        }
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
