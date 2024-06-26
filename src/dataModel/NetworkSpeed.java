package dataModel;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import oshi.SystemInfo;
import oshi.hardware.NetworkIF;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Trida implementujici rozhrani Runnable pro jednoduche pouziti v separatnim vlakne.
 * Slouzi k zisku rychlosti downloadu.
 *
 * @author Dominik Nedved
 * @version 2023.05.11
 */
public class NetworkSpeed implements Runnable {

    /** Instance SystemInfa */
    private final SystemInfo si;

    /** Property pro download */
    private final DoubleProperty download = new SimpleDoubleProperty();

    /** Promenna pro ukladani posledniho downloadu v okamzitem case */
    private long download1 = 0;

    /** Promenna pro ukladani casove znamky pro posledni download */
    private long timestamp1 = 0;

    private final DoubleProperty upload = new SimpleDoubleProperty();

    /** Promenna pro ukladani posledniho downloadu v okamzitem case */
    private long upload1 = 0;

    /**
     * Konstruktor
     *
     * @param si reference na SystemInfo
     */
    public NetworkSpeed(SystemInfo si) {
        this.si = si;
    }

    /**
     * Metoda pro aktualizaci aktivniho sitoveho adapteru
     *
     * @return aktivni rozhrani
     */
    private NetworkIF activeNetworkIF() {
        // list rozhrani sitovych adapteru
        List<NetworkIF> networkIFlist = si.getHardware().getNetworkIFs(false);

        NetworkIF activeNetworkIF = null;

        Enumeration<NetworkInterface> netifs;
        try {
            netifs = NetworkInterface.getNetworkInterfaces();

            String hostname = InetAddress.getLocalHost().getHostName();
            InetAddress myAddr = InetAddress.getByName(hostname);

            while (netifs.hasMoreElements()) {
                NetworkInterface networkInterface = netifs.nextElement();
                Enumeration<InetAddress> inAddrs = networkInterface.getInetAddresses();
                while (inAddrs.hasMoreElements()) {
                    InetAddress inAddr = inAddrs.nextElement();
                    if (inAddr.equals(myAddr)) {
                        for (NetworkIF wantedIF : networkIFlist) {
                            if (wantedIF.getName().equals(networkInterface.getName())) {
                                activeNetworkIF = wantedIF;
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Bez pripojeni k siti. Vypojeny eth kabel nebo vypnute/nedostupne pripojeni k Wi-Fi...");
        }
        return activeNetworkIF;
    }

    /**
     * Kazdou sekundu ziskava aktualni hodnotu downloadu pomoci Timeru.
     */
    public void run() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    NetworkIF activeNetworkAdapter = activeNetworkIF();
                    activeNetworkAdapter.updateAttributes();
                    long download2 = activeNetworkAdapter.getBytesRecv();
                    long upload2 = activeNetworkAdapter.getBytesSent();
                    long timestamp2 = activeNetworkAdapter.getTimeStamp();

                    download.set((double) (download2 - download1) / (timestamp2 - timestamp1));
                    upload.set((double) (upload2 - upload1) / (timestamp2 - timestamp1));

                    download1 = activeNetworkAdapter.getBytesRecv();
                    upload1 = activeNetworkAdapter.getBytesSent();
                    timestamp1 = activeNetworkAdapter.getTimeStamp();

                } catch (NullPointerException e) {
                    System.out.println("Neni dostupne pripojeni z zadneho zapnuteho adapteru.");
                }

            }
        }, 0, 1000);
    }

    public DoubleProperty downloadProperty() {
        return download;
    }

    public DoubleProperty uploadProperty() {
        return upload;
    }
}
