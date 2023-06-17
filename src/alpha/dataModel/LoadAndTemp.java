package alpha.dataModel;

import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.model.components.Cpu;
import com.profesorfalken.jsensors.model.components.Gpu;
import com.profesorfalken.jsensors.model.sensors.Load;
import com.profesorfalken.jsensors.model.sensors.Temperature;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Trida implementujici rozhrani Runnable pro jednoduche pouziti v separatnim vlakne.
 * Slouzi k zisku teplot CPU a GPU a zateze GPU
 *
 * @author Dominik Nedved, A22B0109P
 * @version 2023.05.11
 */
public class LoadAndTemp implements Runnable {

    /** Instance komponentu (u knihovny JSensors) */
    private Components components;

    /** List s GPU */
    private List<Gpu> gpus;

    /** Property k zatezi GPU */
    private final DoubleProperty gpuLoad = new SimpleDoubleProperty();

    /** Property k teplote GPU */
    private final DoubleProperty gpuTemp = new SimpleDoubleProperty();;

    /** List s CPU */
    private List<Cpu> cpus;

    /** Property k teplote CPU */
    private final DoubleProperty cpuTemp = new SimpleDoubleProperty();;

    /**
     * Kazdou sekundu ziskava aktualni hodnoty pomoci Timeru.
     * Sledovane hodnoty: Zatez a teplota GPU, teplota CPU
     */
    public void run() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                components = JSensors.get.components();

                cpus = components.cpus;
                cpuTemp.set(getCpuTemp(cpus));

                gpus = components.gpus;
                gpuLoad.set(getGpuLoad(gpus));
                gpuTemp.set(getGpuTemp(gpus));
            }
        }, 0, 1000);
    }

    /**
     * Ziska aktualni teplotu GPU
     *
     * @param gpus seznam GPU
     * @return aktualni teplota GPU
     */
    private static double getGpuLoad(List<Gpu> gpus) {
        if (gpus != null) {
            for (Gpu gpu : gpus) {
                if (gpu.sensors != null) {
                    List<Load> gpuLoads = gpu.sensors.loads;
                    String wantedGpuLoadValue = "Load GPU Core";
                    int i = 0;
                    while (i < gpuLoads.size()) {
                        if (gpuLoads.get(i).name.equals(wantedGpuLoadValue)) {
                            return gpuLoads.get(i).value;
                        }
                        i++;
                    }
                }
            }
        }
        return 0;
    }

    /**
     * Ziska aktualni zatez GPU
     *
     * @param gpus sezban GPU
     * @return aktualni zatez GPU
     */
    private static double getGpuTemp(List<Gpu> gpus) {
        if (gpus != null) {
            for (final Gpu gpu : gpus) {
                if (gpu.sensors != null) {
                    List<Temperature> temps = gpu.sensors.temperatures;
                    String wantedGpuValue = "Temp GPU Core";
                    int i = 0;
                    while (i < temps.size()) {
                        if (temps.get(i).name.equals(wantedGpuValue)) {
                            return temps.get(i).value;
                        }
                        i++;
                    }
                }
            }
        }
        return 0;
    }

    /**
     * Ziska aktualni teplotu CPU
     *
     * @param cpus seznam procesoru
     * @return aktualni teplota CPU
     */
    private double getCpuTemp(List<Cpu> cpus) {
        if (cpus != null) {
            for (final Cpu cpu : cpus) {
                if (cpu.sensors != null) {
                    List<Temperature> temps = cpu.sensors.temperatures;
                    String wantedCpuValue = "Temp CPU Package";
                    int i = 0;
                    while(i < temps.size()) {
                        if(temps.get(i).name.equals(wantedCpuValue)) {
                            return temps.get(i).value;
                        }
                        i++;
                    }
                }
            }
        }
        return 0;
    }

    /**
     * Getter pro gpuLoadProperty
     *
     * @return gpuLoadProperty
     */
    public DoubleProperty gpuLoadProperty() {
        return gpuLoad;
    }

    /**
     * Getter pro gpuTempProperty
     *
     * @return gpuTempProperty
     */
    public DoubleProperty gpuTempProperty() {
        return gpuTemp;
    }

    /**
     * Getter pro cpuTempProperty
     *
     * @return cpuTempProperty
     */
    public DoubleProperty cpuTempProperty() {
        return cpuTemp;
    }
}
