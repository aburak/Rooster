package inno.rooster.core;

/**
 * Created by aburak on 28.02.2015.
 */
public class Singleton {
    private static Singleton instance = null;


    private int alarmHour = -1;
    private int alarmMinute = -1;
    private boolean isAlarmSet = false;

    private Singleton() { }

    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }

        return instance;
    }

    /////////////////////////////////////////////////////////////////////
    // Alarm Properties
    public void setAlarmHour(int alarmHour) {

        this.alarmHour = alarmHour;
    }

    public void setAlarmMinute(int alarmMinute) {

        this.alarmMinute = alarmMinute;
    }

    public int getAlarmHour() {
        return alarmHour;
    }

    public int getAlarmMinute() {
        return alarmMinute;
    }

    public boolean isAlarmSet() {
        return isAlarmSet;
    }

    public void setAlarmSet(boolean isAlarmSet) {
        this.isAlarmSet = isAlarmSet;
    }
    /////////////////////////////////////////////////////////////////////

}
