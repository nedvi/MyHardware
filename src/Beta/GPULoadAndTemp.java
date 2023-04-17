package Beta;

import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.model.components.Cpu;
import com.profesorfalken.jsensors.model.components.Gpu;
import com.profesorfalken.jsensors.model.sensors.Load;
import com.profesorfalken.jsensors.model.sensors.Temperature;

import java.util.List;

public class GPULoadAndTemp implements Runnable {
    private static List<Gpu> gpus;
    public static Components components;
    private static double gpuLoadNowNum;
    private static double gpuTempNowNum;

    public static List<Cpu> cpus;

    public static double cpuTempNow;

    public void run() {
        while (true) {
            try {
               //System.out.println("Thread of CPU Temp counter is: " + Thread.currentThread());
                components = JSensors.get.components();

                cpus = components.cpus;
                cpuTempNow = getCpuTemp(cpus);

                gpus = components.gpus;
                gpuLoadNowNum = getGpuLoad(gpus);
                gpuTempNowNum = getGpuTemp(gpus);

                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static double getGpuLoad(List<Gpu> gpus) {
        if (gpus != null) {
            for (final Gpu gpu : gpus) {
                //System.out.println("Found CPU component: " + gpu.name);
                if (gpu.sensors != null) {
                    //System.out.println("Sensors found.");
                    List<Load> gpuLoads = gpu.sensors.loads;

                    // test ziskani jednotlivych hodnot
                    String wantedGpuLoadValue = "Load GPU Core";
                    int i = 0;
                    while (i < gpuLoads.size()) {
                        if (gpuLoads.get(i).name.equals(wantedGpuLoadValue)) {
                            //System.out.println("Ziskani pouze zateze - uspesne (" + gpuLoads.get(i).name + ": " + gpuLoads.get(i).value + ")");
                            return gpuLoads.get(i).value;
                        }
                        i++;
                    }
                }
            }
        }
        return 0;
    }

    private static double getGpuTemp(List<Gpu> gpus) {

        if (gpus != null) {
            for (final Gpu gpu : gpus) {
                //System.out.println("Found CPU component: " + gpu.name);
                if (gpu.sensors != null) {
                    //System.out.println("Sensors found.");

                    List<Temperature> temps = gpu.sensors.temperatures;
                    // test ziskani jednotlivych hodnot
                    String wantedGpuValue = "Temp GPU Core";
                    int i = 0;
                    while (i < temps.size()) {
                        if (temps.get(i).name.equals(wantedGpuValue)) {
                            //System.out.println("Ziskani pouze teploty - uspesne (" + temps.get(i).name + ": " + temps.get(i).value + ")");
                            return temps.get(i).value;
                        }
                        i++;
                    }
                }
            }
        }
        return 0;
    }

    private static double getCpuTemp(List<Cpu> cpus) {
        if (cpus != null) {
            for (final Cpu cpu : cpus) {
                //System.out.println("Found CPU component: " + cpu.name);
                if (cpu.sensors != null) {
                    //System.out.println("Sensors found.");

                    System.out.println("Sensors: ");

                    List<Temperature> temps = cpu.sensors.temperatures;
                    // test ziskani jednotlivych hodnot
                    String wantedCpuValue = "Temp CPU Package";
                    int i = 0;
                    while(i < temps.size()) {
                        if(temps.get(i).name.equals(wantedCpuValue)) {
                            //System.out.println("Ziskani pouze teploty - uspesne (" + temps.get(i).name + ": " + temps.get(i).value + ")");
                            return temps.get(i).value;
                        }
                        i++;
                    }
                }
            }
        }
        return 0;
    }

    public double getCpuTempNow() {
        return cpuTempNow;
    }

    public double getGpuLoad() {
        return gpuLoadNowNum;
    }

    public double getGpuTemp() {
        return gpuTempNowNum;
    }
}
