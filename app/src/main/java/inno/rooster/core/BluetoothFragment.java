/*
 * BluetoothFragment - Bluetooth control is done in this tab.
 * Version: 2.0
 * 
 * Author: Ali Burak �nal
 */
package inno.rooster.core;

import info.androidhive.tabsswipe.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class BluetoothFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, Runnable {

	//Variables
	private View rootView;
    private ArrayList<String> bluetoothAddresses;
    private String bluetoothAddress;
    private BlueSmirfSPP blueSmirfSPP;
    private boolean isThreadRunning;

    private Spinner paired_devices;
    private Handler handler;
    private ArrayAdapter arrayAdapterDevices;
    private Thread t;
    private boolean isConnected;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isConnected = false;
        isThreadRunning = false;
        bluetoothAddress = null;
        blueSmirfSPP = new BlueSmirfSPP();
        bluetoothAddresses = new ArrayList<String>();
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.bluetooth_tab, container, false);
        System.out.println("------------------------------BluetoothFragment - onCreateView");

        // Initialization
        final ToggleButton tb = (ToggleButton) rootView.findViewById(R.id.toggleButton1);
        final Button con_but = (Button) rootView.findViewById(R.id.connect_button);
        final Button discon_but = (Button) rootView.findViewById(R.id.disconnect_button);
        paired_devices = (Spinner) rootView.findViewById(R.id.paired_device_spinner);

        ArrayList<String> items = new ArrayList<String>();
        arrayAdapterDevices    = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, items);
        handler                = new Handler();
        paired_devices.setOnItemSelectedListener(this);
        arrayAdapterDevices.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paired_devices.setAdapter(arrayAdapterDevices);


        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> devices = adapter.getBondedDevices();
        arrayAdapterDevices.clear();
        bluetoothAddresses.clear();

        if(devices.size() > 0)
        {
            System.out.println("The size of devices: " + devices.size());
            for(BluetoothDevice device : devices)
            {
                arrayAdapterDevices.add(device.getName());
                bluetoothAddresses.add(device.getAddress());
            }

            // request that the user selects a device
            if(bluetoothAddress == null)
            {
                paired_devices.performClick();
            }
        }
        else
        {
            System.out.println("Null devices");
            bluetoothAddress = null;
        }

        // Adjust the status of button and toggle button according to the Bluetooth status
        if(!adapter.isEnabled()) {
            con_but.setEnabled(false);
            discon_but.setEnabled(false);
            tb.setChecked(false);
        }

        //Bluetooth Disconnect Button - to break the connection between the smartphone and the wristband
        discon_but.setOnClickListener(this);

        //Bluetooth Connect Button - to establish connection between the smartphone and the wristband
        con_but.setOnClickListener(this);

        //Bluetooth Toggle Button - to control the Bluetooth functionality of the smartphone
		if( tb != null) {
			tb.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

					BluetoothAdapter blue_adap = BluetoothAdapter.getDefaultAdapter();

                    if(blue_adap == null) {

                        System.out.println("Null Bluetooth Adapter");
                    }
					boolean isBluetoothEnabled = blue_adap.isEnabled();
					
					if( isChecked && !isBluetoothEnabled) {
						
						System.out.println("Enable");

						blue_adap.enable();
                        con_but.setEnabled(true);
                        discon_but.setEnabled(true);
                        Set<BluetoothDevice> devices = blue_adap.getBondedDevices();
                        arrayAdapterDevices.clear();
                        bluetoothAddresses.clear();

                        if(devices.size() > 0)
                        {
                            System.out.println("The size of devices: " + devices.size());
                            for(BluetoothDevice device : devices)
                            {
                                arrayAdapterDevices.add(device.getName());
                                bluetoothAddresses.add(device.getAddress());
                            }

                            // request that the user selects a device
                            if(bluetoothAddress == null)
                            {
                                paired_devices.performClick();
                            }
                        }
                        else
                        {
                            System.out.println("Null devices");
                            bluetoothAddress = null;
                        }
                        while( isBluetoothEnabled == blue_adap.isEnabled());
					}
					else if( !isChecked && isBluetoothEnabled) {
						
						System.out.println("Disable");
						blue_adap.disable();
                        con_but.setEnabled(false);
                        discon_but.setEnabled(false);
                        while( isBluetoothEnabled == blue_adap.isEnabled());
					}
				}
			});
		}
		else {
			
			System.out.println("NULL tb");
		}

		return rootView;
	}

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        case R.id.connect_button:
            System.out.println("Connect is clicked on");
            System.out.println("Bluetooth address: " + bluetoothAddress);

            if(!isThreadRunning) {

                System.out.println("connect-1");
                isThreadRunning = true;
                t = new Thread(this);
                System.out.println("connect-2");
                t.start();
                System.out.println("connect-3");
            }
            break;

        case R.id.disconnect_button:
            System.out.println("Disconnect is clicked on");
            blueSmirfSPP.disconnect();
            isThreadRunning = false;
            if(!blueSmirfSPP.isConnected()) {

                Toast.makeText(getActivity(),"Bluetooth is disconnected!",
                        Toast.LENGTH_SHORT).show();
            }
            else {

                Toast.makeText(getActivity(),"Bluetooth disconnection failure!",
                        Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        bluetoothAddress = bluetoothAddresses.get(pos);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

        bluetoothAddress = null;
    }

    public void run() {

        Looper.prepare();
        blueSmirfSPP.connect(bluetoothAddress);
        if(blueSmirfSPP.isConnected()) {

            System.out.println("Connected in run method");
        }
        else {

            AlertDialog ad = new AlertDialog.Builder(getActivity()).create();
            ad.setCancelable(false);
            ad.setTitle("Warning");
            ad.setMessage("Bluetooth is not connected to the wristband!");
            ad.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            ad.show();
        }

        // For the last 30 minutes of sleep

        int counter = 0;
        long difference = -1;
        boolean sent = false;
        boolean isTimeSet = false;
        Date d;
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
            }

            if(s.isAlarmSet() && !isTimeSet) {

                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, s.getAlarmHour());
                c.set(Calendar.MINUTE, s.getAlarmMinute());
                Calendar c2 = Calendar.getInstance();
                System.out.print("c: " + c);
                System.out.println("c2: " + c2);
                System.out.println("Difference: " + (c.getTimeInMillis() - c2.getTimeInMillis()) / 60000);
                isTimeSet = true;
            }

            if(counter >= 13 && !sent) {

                blueSmirfSPP.writeByte((int)'s'); // 115 -> s
                System.out.println("s is sent!");
                sent = true;
                blueSmirfSPP.flush();
                try { Thread.sleep((long) (1000.0F/30.0F)); }
                catch(InterruptedException e) { System.out.println("Error while sleeping the thread");}
            }
        }

        System.out.println("Thread end!!!");
    }
}
