package inno.rooster.core;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.provider.AlarmClock;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by aburak on 03.03.2015.
 */
public class BluetoothService extends Service {

    //Variables
    BlueSmirfSPP blueSmirfSPP;
    BluetoothThread bluetoothThread;
    String bluetoothAddress;

    @Override
    public void onCreate() {

        blueSmirfSPP = new BlueSmirfSPP();
        bluetoothAddress = "";
        System.out.println("Service created");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        System.out.println("onStartCommand is called");
        bluetoothAddress = intent.getStringExtra("Address");
        makeConnection();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        System.out.println("onDestroy is called");
        blueSmirfSPP.disconnect();
        stopSelf();
    }

    private void makeConnection() {

        System.out.println("Service:makeConnection");
        bluetoothThread = new BluetoothThread(this);
        bluetoothThread.start();
    }

    @Override
    public boolean stopService(Intent name) {

        System.out.println("stopService is called");
        blueSmirfSPP.disconnect();
        return super.stopService(name);
    }

    private class BluetoothThread extends Thread {

//        BlueSmirfSPP tmpBS;
//        String address;
        Context context;

        public BluetoothThread(Context context) {

            this.context = context;
        }

        @Override
        public void run() {

            System.out.println("Thread has begun");
            Looper.prepare();
            blueSmirfSPP.connect(bluetoothAddress);
            if(blueSmirfSPP.isConnected()) {

                System.out.println("Connected in run method");
            }
            else {

                System.out.println("Not connected in run method");
            }

            // For the last 30 minutes of sleep

            AnalysisMaker tmpAnalysisMaker = new AnalysisMaker(context);
            FileOutputStream fos = null;
            try {
                fos = openFileOutput((new FileNameManager()).getTempData_file_name(), MODE_PRIVATE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            boolean sent = false;
            boolean isTimeSet = false;
            boolean first = true;
            boolean isDataRead = false;
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            int tmp = 0;
            Singleton s = Singleton.getInstance();

            Calendar c = null;
            String toBeWritten = "";

            // During the night except from the last 30 minutes
            while(blueSmirfSPP.isConnected()) {

                tmp = blueSmirfSPP.readByte();
//                System.out.println("tmpNum: " + Character.getNumericValue(tmp) + " - tmpStr: " + Character.toString((char) tmp));

                // neither h nor a
                if(!Character.toString((char) tmp).equals("a") && !Character.toString((char) tmp).equals("h")) {

//                    try {
//                        fos.write(tmp);
//                    System.out.println("Neither case");
                    toBeWritten += "" + Character.getNumericValue(tmp);
                    first = false;
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
                // h
                else if(Character.toString((char) tmp).equals("h") && !first) {

                    try {
//                        fos.write(("\n").getBytes());
//                        System.out.println("h case");
                        toBeWritten += "\n";
                        fos.write(toBeWritten.getBytes());
                        System.out.println("toBeWritten: " + toBeWritten);
                        toBeWritten = "";
                        isDataRead = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // a
                else if(Character.toString((char) tmp).equals("a")) {

//                    try {
//                        fos.write((",").getBytes());
//                    System.out.println("a case");
                    toBeWritten += ",";
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }

                // Set the time of the alarm in order to find the difference
                if(s.isAlarmSet() && !isTimeSet) {

                    c = Calendar.getInstance();
                    c.set(Calendar.HOUR_OF_DAY, s.getAlarmHour());
                    c.set(Calendar.MINUTE, s.getAlarmMinute());
                    isTimeSet = true;
                }

                // The current time
                Calendar c2 = Calendar.getInstance();

                if( isTimeSet && (c.getTimeInMillis() - c2.getTimeInMillis()) < 1800000  /*30 minutes */ && !sent) {

                    blueSmirfSPP.writeByte((int)'s'); // 115 -> s
                    System.out.println("s is sent!");
                    sent = true;
                    blueSmirfSPP.flush();
                    try {
                        Thread.sleep((long) (1000.0F/30.0F));
                        break;
                    }
                    catch(InterruptedException e) { System.out.println("Error while sleeping the thread - in Service");}
                }
            }

            int value = 0;
            try {
                FileInputStream fis = openFileInput((new FileNameManager()).getTempData_file_name());
                int at = -1;
                while((at = fis.read()) != -1) {

                    if(Character.getNumericValue(at) >= 0 && Character.getNumericValue(at) <= 9) {

                        value = value * 10 + Character.getNumericValue(at);
                    }
                    else {
                        System.out.println("Value: " + value);
                        value = 0;
                    }
                }
                fis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // If we read data, then we can go to  the next step
            if( isDataRead) {

                first = true;
                boolean timeToWakeUp = false;
                tmpAnalysisMaker.instantAnalysisPreparation();
                int counter = 0;
                 value = 0;
                double hr = 0;
                double pre_hr = 0;
                double mv = 0;
                double pre_mv = 0;
                toBeWritten = "";
                Calendar calendar = Calendar.getInstance();

                // Last 30 minutes of the sleep
                while(blueSmirfSPP.isConnected() && !timeToWakeUp && c.getTimeInMillis() - calendar.getTimeInMillis() > 60000) {

                    tmp = blueSmirfSPP.readByte();

                    // neither h nor a
                    if (!Character.toString((char) tmp).equals("a") && !Character.toString((char) tmp).equals("h")) {

                        toBeWritten += "" + Character.getNumericValue(tmp);
                        value = value * 10 + Character.getNumericValue(tmp);
                        first = false;
                    }
                    // h
                    else if (Character.toString((char) tmp).equals("h") && !first) {

                    try {
                        toBeWritten += "\n";
                        fos.write(toBeWritten.getBytes());
                        counter++;
                        System.out.println("toBeWritten in last 30 min: " + toBeWritten);
                        toBeWritten = "";
                        mv = value;
                        value = 0;
                        if(counter % 2 == 0 && tmpAnalysisMaker.isThisMinuteREM(pre_hr, hr, pre_mv, mv)) {

                            // REM is detected and alarm is set to the next minute
                            Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
                            i.putExtra(AlarmClock.EXTRA_HOUR, calendar.get(Calendar.HOUR_OF_DAY));
                            i.putExtra(AlarmClock.EXTRA_MINUTES, calendar.get(Calendar.MINUTE) + 1);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            timeToWakeUp = true;
                        }
                        else {

                            pre_hr = hr;
                            pre_mv = mv;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    }
                    // a
                    else if (Character.toString((char) tmp).equals("a")) {

                        toBeWritten += ",";
                        hr = value;
                        value = 0;
                    }

                    // Get calendar again to find to current time
                    calendar = Calendar.getInstance();
                }

                // If no REM is detected, alarm will be set on the time that user specified.
                if(blueSmirfSPP.isConnected() && !timeToWakeUp) {

                    Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
                    i.putExtra(AlarmClock.EXTRA_HOUR, Singleton.getInstance().getAlarmHour());
                    i.putExtra(AlarmClock.EXTRA_MINUTES, Singleton.getInstance().getAlarmMinute() + 1);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }

                // Just to see what was written into the file
//                try {
//                    FileInputStream fis = openFileInput((new FileNameManager()).getTempData_file_name());
//                    int at = -1;
//                    while((at = fis.read()) != -1) {
//
//                        if(Character.getNumericValue(at) >= 0 && Character.getNumericValue(at) <= 9) {
//
//                            value = value * 10 + Character.getNumericValue(at);
//                        }
//                        else /*if(Character.toString((char)at) == "\n")*/ {
//                            System.out.println("Value: " + value);
//                            value = 0;
//                        }
//                    }
//                    fis.close();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                System.out.println("--If end--");
            }
            // Copy the data inside of temp file to the real data file
//            copyTempDataToData();
            System.out.println("--Thread end--");
        }

        // Copy the data inside of TempData file into the Data file which is the file that will be
        // used to draw the last night graph
        public void copyTempDataToData() {

            try {
                FileInputStream fis = openFileInput((new FileNameManager()).getTempData_file_name());
                FileOutputStream fos = openFileOutput((new FileNameManager()).getData_file_name(), MODE_PRIVATE);

                int at = -1;
                String line = "";
                while((at = fis.read()) != -1) {

                    // End of line - write that line and empty the line variable
                    if(Character.toString((char)at).equals("\n")) {

                        fos.write(line.getBytes());
                        line = "";
                    }
                    // Append the current char read from the file into the line variable
                    else {

                        line += Character.toString((char)at);
                    }
                }
                fis.close();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
