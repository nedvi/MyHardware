package alpha;

import javafx.beans.property.SimpleStringProperty;

import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.hardware.CentralProcessor.ProcessorIdentifier;
import oshi.software.os.OperatingSystem;;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Component {

    double gigabyte = 1073741824;
    double bilion = 1000000000;

    private String osDescription;

    private SimpleStringProperty cpuDescription = new SimpleStringProperty();

    private List<String> gpuList = new ArrayList<>();

    private List<String> diskList = new ArrayList<>();

    private List<String> ramList = new ArrayList<>();

    private List<String> powerSourceList = new ArrayList<>();

    private List<String> usbDevicesList = new ArrayList<>();

    private SystemInfo si = new SystemInfo();

    public Component() {
        // OS
        OperatingSystem os = si.getOperatingSystem();
        Date bootTime = new Date(os.getSystemBootTime() * 1000);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String bootTimeSB = formatter.format(bootTime);

        String osS = "Manufacturer: " + os.getManufacturer() + "\n" +
                "Family: " + os.getFamily() + "\n" +
                "Version: " + os.getVersionInfo() + "\n" +
                "Bitness: " + os.getBitness() + "\n" +
                "Boot time: " + bootTimeSB;
        osDescription = osS;

        // CPU

        ProcessorIdentifier cpu = si.getHardware().getProcessor().getProcessorIdentifier();
        String cpuS = cpu.getName() + "\n\n" +
                "Vendor: " + cpu.getVendor() + "\n" +
                "Model: " + cpu.getModel() + "\n" +
                "Family: " + cpu.getFamily() + "\n" +
                "Microarchitecture: " + cpu.getMicroarchitecture() + "\n" +
                "Frequency: " + cpu.getVendorFreq()/bilion + " GHz" + "\n" +
                "Physical cores: " + si.getHardware().getProcessor().getPhysicalProcessors().size() + "\n" +
                "Stepping: " + cpu.getStepping() + "\n" +
                "Processor ID: " + cpu.getProcessorID();
        cpuDescription.set(cpuS.toString());

        // GPU
        for (GraphicsCard graphicsCard : si.getHardware().getGraphicsCards()) {
            String gpuS = graphicsCard.getName() + "\n\n" +
                    "Vendor: " + graphicsCard.getVendor() + "\n" +
                    "VRAM: " + Math.round(graphicsCard.getVRam() / gigabyte) + " GB\n" +
                    "Device ID: " + graphicsCard.getDeviceId() + "\n" +
                    graphicsCard.getVersionInfo();
            gpuList.add(gpuS);
        }

        // RAM
        int order = 1;
        for (PhysicalMemory ram : si.getHardware().getMemory().getPhysicalMemory()) {
            String ramSB = order + ". - " + ram.getManufacturer() + "\n\n" +
                    "Memory type: " + ram.getMemoryType() + "\n" +
                    "Capacity: " + Math.round(ram.getCapacity() / gigabyte) + " GB\n" +
                    "Clock speed: " + ram.getClockSpeed() / 1000000 + " MHz\n\n" +
                    "-------------------------------------\n\n";
            ramList.add(ramSB);
            order++;
        }

        // Disks
        for (HWDiskStore disk : si.getHardware().getDiskStores()) {
            String diskSB = disk.getModel() + "\n\n" +
                    "Size: " + Math.round(disk.getSize() / gigabyte) + " GB\n" +
                    "Serial: " + disk.getSerial() + "\n";
            diskList.add(diskSB);
        }

        // Power source
        for (PowerSource powerSource : si.getHardware().getPowerSources()) {
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
        for (UsbDevice usbDevice : si.getHardware().getUsbDevices(true)) {
            String usbDevicesSB = usbDevice.getName() + "\n\n" +
                    "Vendor: " + usbDevice.getVendor() + "\n" +
                    "Vendor ID: " + usbDevice.getVendorId() + "\n" +
                    "Product ID: " + usbDevice.getProductId() + "\n" +
                    "Unique device ID: " + usbDevice.getUniqueDeviceId();
            usbDevicesList.add(usbDevicesSB);
        }

    }

    public String getSystemName() {
        return si.getOperatingSystem().getNetworkParams().getHostName();
    }

    public String getOsDescription() {
        return osDescription;
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
            System.out.println(disk.split("\n")[0]);
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
            System.out.println(powerSource.split("\n")[0]);
            if (powerSource.split("\n")[0].equals(powerSourceName)) {
                return powerSource;
            }
        }
        return null;
    }

    public String getUsbDeviceDescription(String usbDeviceName) {
        for (String usbDevice : usbDevicesList) {
            System.out.println(usbDevice.split("\n")[0]);
            if (usbDevice.split("\n")[0].equals(usbDeviceName)) {
                return usbDevice;
            }
        }
        return null;
    }
}
