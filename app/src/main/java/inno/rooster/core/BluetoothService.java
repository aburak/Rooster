package inno.rooster.core;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;

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
//
//    private synchronized void connectToDevice(String macAddress) {
//        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(macAddress);
//        if (mState == STATE_CONNECTING) {
//            if (mConnectThread != null) {
//                mConnectThread.cancel();
//                mConnectThread = null;
//            }
//        }
//
//        // Cancel any thread currently running a connection
//        if (mConnectedThread != null) {
//            mConnectedThread.cancel();
//            mConnectedThread = null;
//        }
//        mConnectThread = new ConnectThread(device);
//        mConnectThread.start();
//        setState(STATE_CONNECTING);
//    }
//
//    private void setState(int state) {
//        PrinterService.mState = state;
//        if (mHandler != null) {
//            mHandler.obtainMessage(AbstractActivity.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
//        }
//    }
//
//    public synchronized void stop() {
//        setState(STATE_NONE);
//        if (mConnectThread != null) {
//            mConnectThread.cancel();
//            mConnectThread = null;
//        }
//
//        if (mConnectedThread != null) {
//            mConnectedThread.cancel();
//            mConnectedThread = null;
//        }
//        if (mBluetoothAdapter != null) {
//            mBluetoothAdapter.cancelDiscovery();
//        }
//        stopSelf();
//    }
//
    @Override
    public boolean stopService(Intent name) {

        System.out.println("stopService is called");
        blueSmirfSPP.disconnect();
        return super.stopService(name);
    }
//
//    private void connectionFailed() {
//        PrinterService.this.stop();
//        Message msg = mHandler.obtainMessage(AbstractActivity.MESSAGE_TOAST);
//        Bundle bundle = new Bundle();
//        bundle.putString(AbstractActivity.TOAST, getString(R.string.error_connect_failed));
//        msg.setData(bundle);
//        mHandler.sendMessage(msg);
//    }
//
//    private void connectionLost() {
//        PrinterService.this.stop();
//        Message msg = mHandler.obtainMessage(AbstractActivity.MESSAGE_TOAST);
//        Bundle bundle = new Bundle();
//        bundle.putString(AbstractActivity.TOAST, getString(R.string.error_connect_lost));
//        msg.setData(bundle);
//        mHandler.sendMessage(msg);
//    }
//
//    private static Object obj = new Object();
//
//    public static void write(byte[] out) {
//        // Create temporary object
//        ConnectedThread r;
//        // Synchronize a copy of the ConnectedThread
//        synchronized (obj) {
//            if (mState != STATE_CONNECTED)
//                return;
//            r = mConnectedThread;
//        }
//        // Perform the write unsynchronized
//        r.write(out);
//    }
//
//    private synchronized void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice) {
//        // Cancel the thread that completed the connection
//        if (mConnectThread != null) {
//            mConnectThread.cancel();
//            mConnectThread = null;
//        }
//
//        // Cancel any thread currently running a connection
//        if (mConnectedThread != null) {
//            mConnectedThread.cancel();
//            mConnectedThread = null;
//        }
//
//        mConnectedThread = new ConnectedThread(mmSocket);
//        mConnectedThread.start();
//
//        // Message msg =
//        // mHandler.obtainMessage(AbstractActivity.MESSAGE_DEVICE_NAME);
//        // Bundle bundle = new Bundle();
//        // bundle.putString(AbstractActivity.DEVICE_NAME, "p25");
//        // msg.setData(bundle);
//        // mHandler.sendMessage(msg);
//        setState(STATE_CONNECTED);
//
//    }

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
//                AlertDialog ad = new AlertDialog.Builder(this).create();
//                ad.setCancelable(false);
//                ad.setTitle("Warning");
//                ad.setMessage("Bluetooth is not connected to the wristband!");
//                ad.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//                ad.show();
            }

            // For the last 30 minutes of sleep

            AnalysisMaker tmpAnalysisMaker = new AnalysisMaker(context);
            FileOutputStream fos = null;
            try {
                fos = openFileOutput((new FileNameManager()).getTempData_file_name(), MODE_PRIVATE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            int counter = 0;
            boolean sent = false;
            boolean isTimeSet = false;
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            int tmp = 0;
            byte b[] = new byte[8];
            Singleton s = Singleton.getInstance();

            while(blueSmirfSPP.isConnected()) {

                tmp = blueSmirfSPP.readByte();
                counter++;
                System.out.println("Counter: " + counter);
                if(tmp >= 0) {

                    System.out.println("tmp: " + Character.getNumericValue(tmp));
                    try {
                        fos.write((Character.getNumericValue(tmp) + "\n").getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // Set the time of the alarm in order to find the difference
                if(s.isAlarmSet() && !isTimeSet) {

                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.HOUR_OF_DAY, s.getAlarmHour());
                    c.set(Calendar.MINUTE, s.getAlarmMinute());
                    isTimeSet = true;
                }

                // The current time
                Calendar c2 = Calendar.getInstance();

                if( counter >= 12 /*(c2.getTimeInMillis() - c2.getTimeInMillis()) < 1800000  30 minutes */ && !sent) {

                    blueSmirfSPP.writeByte((int)'s'); // 115 -> s
                    System.out.println("s is sent!");
                    sent = true;
                    blueSmirfSPP.flush();
                    try { Thread.sleep((long) (1000.0F/30.0F)); }
                    catch(InterruptedException e) { System.out.println("Error while sleeping the thread");}
                }


            }

            try {
                FileInputStream fis = openFileInput((new FileNameManager()).getTempData_file_name());
                int at = -1;
                while((at = fis.read()) != -1) {

                    System.out.println("at: " + at + "--" + Character.getNumericValue(at));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("--Thread end--");
        }
    }
}
