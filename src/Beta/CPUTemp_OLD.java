package Beta;

import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.model.components.Cpu;
import com.profesorfalken.jsensors.model.sensors.Temperature;
import oshi.SystemInfo;

import java.util.List;


public class CPUTemp_OLD implements Runnable {

    public SystemInfo si;
    public static Components components;
    public static List<Cpu> cpus;
    public static double cpuTempNow;

    public CPUTemp_OLD(SystemInfo si) {
        this.si = si;
    }

    @Override
    public void run() {
        while (true) {
            try {
                //System.out.println("Thread of CPU Temp counter is: " + Thread.currentThread().getName());
                components = JSensors.get.components();
                cpus = components.cpus;

                cpuTempNow = getCpuTemp(cpus);

                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static double getCpuTemp(List<Cpu> cpus) {
        if (cpus != null) {
            for (final Cpu cpu : cpus) {
                //System.out.println("Found CPU component: " + cpu.name);
                if (cpu.sensors != null) {
                    System.out.println("Sensors found.");
                    System.out.println(cpus.toString());

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
}
