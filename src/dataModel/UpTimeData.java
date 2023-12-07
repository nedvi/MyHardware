package dataModel;

/**
 * Trida reprezentujici datovy model pro Up-Time timer
 *
 * @author Dominik Nedved, A22B0109P
 * @version 2023.06.26
 */
public class UpTimeData {

    private int sec = 0;
    private int min = 0;
    private int hour = 0;

    //========================= Gettery =========================

    public int getSec() {
        return sec;
    }

    public int getMin() {
        return min;
    }

    public int getHour() {
        return hour;
    }

    //========================= Settery =========================

    public void setSec(int sec) {
        this.sec = sec;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }
}
