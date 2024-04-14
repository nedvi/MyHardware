package dataModel;

import javafx.beans.property.SimpleStringProperty;

import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.hardware.CentralProcessor.ProcessorIdentifier;
import oshi.software.os.OperatingSystem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Trida reprezentujici datovy model popisku HW komponent pro tridu gui.HWTree
 *
 * @author Dominik Nedved, A22B0109P
 * @version 2023.06.26
 */
public class Component {

    //================================== Konstanty hodnot ==================================

    private final double GIGABYTE = 1073741824;
    private final double BILION = 1000000000;

    //================================== Popisky ==================================

    private final String osDescription;

    private final String motherboardDescription;

    private final SimpleStringProperty cpuDescription = new SimpleStringProperty();

    //================================== Listy s komponenty ==================================

    private final List<String> gpuList = new ArrayList<>();

    private final List<String> diskList = new ArrayList<>();

    private final List<String> ramList = new ArrayList<>();

    private final List<String> powerSourceList = new ArrayList<>();

    private final List<String> usbDevicesList = new ArrayList<>();

    private final List<String> soundCardsList = new ArrayList<>();


    /** Atribut pristupu k informacich o systemu */
    private final SystemInfo si;

    /**
     * Konstruktor
     *
     * @param si predana instance SystemInfa
     */
    public Component(SystemInfo si) {
        this.si = si;

        // OS
        OperatingSystem os = this.si.getOperatingSystem();
        Date bootTime = new Date(os.getSystemBootTime() * 1000);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String bootTimeSB = formatter.format(bootTime);

        osDescription = "Manufacturer: " + os.getManufacturer() + "\n" +
                "Family: " + os.getFamily() + "\n" +
                "Version: " + os.getVersionInfo() + "\n" +
                "Bitness: " + os.getBitness() + "\n" +
                "Boot time: " + bootTimeSB;

        // Motherboard
        Baseboard motherboard = this.si.getHardware().getComputerSystem().getBaseboard();
        motherboardDescription = "Manufacturer: " + motherboard.getManufacturer() + "\n" +
                "Model: " + motherboard.getModel() + "\n" +
                "BIOS/Firmware version: " + motherboard.getVersion() + "\n" +
                "Serial number: " + motherboard.getSerialNumber();

        // CPU
        ProcessorIdentifier cpu = this.si.getHardware().getProcessor().getProcessorIdentifier();
        String cpuS = cpu.getName() + "\n\n" +
                "Vendor: " + cpu.getVendor() + "\n" +
                "Model: " + cpu.getModel() + "\n" +
                "Family: " + cpu.getFamily() + "\n" +
                "Microarchitecture: " + cpu.getMicroarchitecture() + "\n" +
                "Frequency: " + cpu.getVendorFreq()/BILION + " GHz" + "\n" +
                "Physical cores: " + this.si.getHardware().getProcessor().getPhysicalProcessors().size() + "\n" +
                "Stepping: " + cpu.getStepping() + "\n" +
                "Processor ID: " + cpu.getProcessorID();
        cpuDescription.set(cpuS.toString());

        // GPU
        for (GraphicsCard graphicsCard : this.si.getHardware().getGraphicsCards()) {
            String gpuS = graphicsCard.getName() + "\n\n" +
                    "Vendor: " + graphicsCard.getVendor() + "\n" +
                    "VRAM: " + Math.round(graphicsCard.getVRam() / GIGABYTE) + " GB\n" +
                    "Device ID: " + graphicsCard.getDeviceId() + "\n" +
                    graphicsCard.getVersionInfo();
            gpuList.add(gpuS);
        }

        // RAM
        int order = 1;
        for (PhysicalMemory ram : this.si.getHardware().getMemory().getPhysicalMemory()) {
            String ramSB = order + ". - " + ram.getManufacturer() + "\n\n" +
                    "Memory type: " + ram.getMemoryType() + "\n" +
                    "Capacity: " + Math.round(ram.getCapacity() / GIGABYTE) + " GB\n" +
                    "Clock speed: " + ram.getClockSpeed() / 1000000 + " MHz\n\n" +
                    "-------------------------------------\n\n";
            ramList.add(ramSB);
            order++;
        }

        // Disks
        for (HWDiskStore disk : this.si.getHardware().getDiskStores()) {
            String diskSB = disk.getModel() + "\n\n" +
                    "Size: " + Math.round(disk.getSize() / GIGABYTE) + " GB\n" +
                    "Serial: " + disk.getSerial() + "\n";
            diskList.add(diskSB);
        }

        // Power source
        for (PowerSource powerSource : this.si.getHardware().getPowerSources()) {
            String powerSourceSB = powerSource.getName() + "\n\n" +
                    "Manufacturer: " + powerSource.getManufacturer() + "\n" +
                    "Device name: " + powerSource.getDeviceName() + "\n" +
                    "Chemistry: " + powerSource.getChemistry() + "\n" +
                    "Serial number: " + powerSource.getSerialNumber() + "\n" +
                    "Current capacity: " + powerSource.getCurrentCapacity() + " " + powerSource.getCapacityUnits() + "\n" +
                    "Max capacity: " + powerSource.getMaxCapacity() + " " + powerSource.getCapacityUnits() + "\n" +
                    "Design capacity: " + powerSource.getDesignCapacity() + " " + powerSource.getCapacityUnits();
            powerSourceList.add(powerSourceSB);
        }

        // USB devices
        for (UsbDevice usbDevice : this.si.getHardware().getUsbDevices(true)) {
            String usbDevicesSB = usbDevice.getName() + "\n\n" +
                    "Vendor: " + usbDevice.getVendor() + "\n" +
                    "Vendor ID: " + usbDevice.getVendorId() + "\n" +
                    "Product ID: " + usbDevice.getProductId() + "\n" +
                    "Unique device ID: " + usbDevice.getUniqueDeviceId();
            usbDevicesList.add(usbDevicesSB);
        }

        // Sound cards
        for (SoundCard soundCard : this.si.getHardware().getSoundCards()) {
            String soundCardSB = soundCard.getName() + "\n\n" +
                    "Codec: " + soundCard.getCodec() +
                    "Driver version: " + soundCard.getDriverVersion() + "\n";
            soundCardsList.add(soundCardSB);
        }
    }

    //============================= Gettery pro jednotlive popisky komponentu =============================

    public String getSystemName() {
        return si.getOperatingSystem().getNetworkParams().getHostName();
    }

    public String getOsDescription() {
        return osDescription;
    }

    public String getMotherboardDescription() {
        return motherboardDescription;
    }

    public String getCpuDescription() {
        return cpuDescription.get();
    }

    public String getGpuDescription(String gpuName) {
        for (String gpu : gpuList) {
            if (gpu.split("\n")[0].equals(gpuName)) {
                return gpu;
            }
        }
        return null;
    }

    public String getDiskDescription(String diskName) {

        for (String disk : diskList) {
            if (disk.split("\n")[0].equals(diskName)) {
                return disk;
            }
        }
        return null;
    }

    public String getRamDescription() {
        StringBuilder sb = new StringBuilder();
        for (String ram : ramList) {
            sb.append(ram);
        }
        return sb.toString();
    }

    public String getPowerSourceDescription(String powerSourceName) {
        for (String powerSource : powerSourceList) {
            if (powerSource.split("\n")[0].equals(powerSourceName)) {
                return powerSource;
            }
        }
        return null;
    }

    public String getUsbDeviceDescription(String usbDeviceName) {
        for (String usbDevice : usbDevicesList) {
            if (usbDevice.split("\n")[0].equals(usbDeviceName)) {
                return usbDevice;
            }
        }
        return null;
    }

    public String getSoundCardDescription(String soundCardName) {
        for (String soundCard : soundCardsList) {
            if (soundCard.split("\n")[0].equals(soundCardName)) {
                return soundCard;
            }
        }
        return null;
    }
}
