package inno.rooster.core;

import android.content.Intent;

/**
 * Created by aburak on 28.02.2015.
 */
public class Singleton {

    private static Singleton instance = null;




    private Intent blueIntent;
    private  int age = -1;
    private int alarmHour = -1;
    private int alarmMinute = -1;
    private boolean isAlarmSet = false;



    private String hr_fileName = "kiz_heart_rate_EV#-2.txt";
    private String mv_fileName = "kiz_movement_EV#-6.txt";

    private Singleton() { }

    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }

        return instance;
    }

    /////////////////////////////////////////////////////////////////////
    // Age
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
    //////////////////////////////////////////////////////////////////////
    // Bluetooth Intent
    public Intent getBlueIntent() {
        return blueIntent;
    }

    public void setBlueIntent(Intent blueIntent) {
        this.blueIntent = blueIntent;
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
    // Heart Rate and Movement File Names
    public String getHr_fileName() {
        return hr_fileName;
    }

    public void setHr_fileName(String hr_fileName) {
        this.hr_fileName = hr_fileName;
    }

    public String getMv_fileName() {
        return mv_fileName;
    }

    public void setMv_fileName(String mv_fileName) {
        this.mv_fileName = mv_fileName;
    }
    ///////////////////////////////////////////////////////////////////////////////
}
