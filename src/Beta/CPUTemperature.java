package Beta;

/**
 * Runnable pro implmentaci Threadu ve tride CPUPanel
 */
public class CPUTemperature implements Runnable {
    private double cpuTemp;

    /**
     *
     */
    @Override
    public void run() {
        while (true) {
            try {
                cpuTemp = GPULoadAndTemp.cpuTempNow;
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Getter pro teplotu CPU
     * @return
     */
    public double getCpuTempNow() {
        return cpuTemp;
    }
}
