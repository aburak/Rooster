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
import android.content.Intent;
import android.net.Uri;
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class BluetoothFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

	//Variables
	private View rootView;
    private ArrayList<String> bluetoothAddresses;
    private String bluetoothAddress;

    private Handler handler;
    private ArrayAdapter arrayAdapterDevices;
    private Intent bluetoothIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bluetoothAddress = null;
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
        Spinner paired_devices = (Spinner) rootView.findViewById(R.id.paired_device_spinner);

        // Fill the spinner
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
//            System.out.println("The size of devices: " + devices.size());
            for(BluetoothDevice device : devices)
            {
//                System.out.println("atkafa: " + device.getName());
                arrayAdapterDevices.add(device.getName());
                bluetoothAddresses.add(device.getAddress());
            }

            // request that the user selects a device
//            if(bluetoothAddress == null)
//            {
//                paired_devices.performClick();
//            }
        }
        else
        {
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
					boolean isBluetoothEnabled = blue_adap.isEnabled();
					
					if( isChecked && !isBluetoothEnabled) {
						
						System.out.println("Enable - Huseyin");
                        blue_adap.enable();
                        con_but.setEnabled(true);
                        discon_but.setEnabled(true);

                        while(!blue_adap.isEnabled()) {}
                        Set<BluetoothDevice> devices = blue_adap.getBondedDevices();


//                        System.out.println("Device size inside: " + devices.size());
//                        paired_devices.setAdapter(arrayAdapterDevices);

                        if(devices.size() > 0)
                        {
//                            System.out.println("The size of devices - Huseyin: " + devices.size());
                            for(BluetoothDevice device : devices)
                            {
//                                System.out.println("atlıkarınca: " + device.getName());
                                arrayAdapterDevices.add(device.getName());
                                bluetoothAddresses.add(device.getAddress());
                            }

                            // request that the user selects a device
//                            if(bluetoothAddress == null)
//                            {
//                                paired_devices.performClick();
//                            }
                        }
                        else
                        {
                            bluetoothAddress = null;
                        }

                        while( isBluetoothEnabled == blue_adap.isEnabled());
					}
					else if( !isChecked && isBluetoothEnabled) {
						
						System.out.println("Disable - Hüseyin");
                        blue_adap.disable();
                        con_but.setEnabled(false);
                        discon_but.setEnabled(false);
                        arrayAdapterDevices.clear();
                        bluetoothAddresses.clear();
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

            bluetoothIntent = new Intent(getActivity(), BluetoothService.class);
            bluetoothIntent.putExtra("Address", bluetoothAddress);
            getActivity().startService(bluetoothIntent);
            System.out.println("startService is called -- BluetoothFragment");
            break;

        case R.id.disconnect_button:
            System.out.println("Disconnect is clicked on");
            getActivity().stopService(bluetoothIntent);
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

//    public void run() {
//
//        Looper.prepare();
//        blueSmirfSPP.connect(bluetoothAddress);
//        if(blueSmirfSPP.isConnected()) {
//
//            System.out.println("Connected in run method");
//        }
//        else {
//
//            AlertDialog ad = new AlertDialog.Builder(getActivity()).create();
//            ad.setCancelable(false);
//            ad.setTitle("Warning");
//            ad.setMessage("Bluetooth is not connected to the wristband!");
//            ad.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
//
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });
//            ad.show();
//        }
//
//        // For the last 30 minutes of sleep
//
//        AnalysisMaker tmpAnalysisMaker = new AnalysisMaker(getActivity());
//        FileOutputStream fos = null;
//        try {
//            fos = getActivity().openFileOutput((new FileNameManager()).getTempData_file_name(), getActivity().MODE_PRIVATE);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        int counter = 0;
//        boolean sent = false;
//        boolean isTimeSet = false;
//        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
//        int tmp = 0;
//        byte b[] = new byte[8];
//        Singleton s = Singleton.getInstance();
//
//        while(blueSmirfSPP.isConnected()) {
//
//            tmp = blueSmirfSPP.readByte();
//            counter++;
//            System.out.println("Counter: " + counter);
//            if(tmp >= 0) {
//
//                System.out.println("tmp: " + Character.getNumericValue(tmp));
//                try {
//                    fos.write((Character.getNumericValue(tmp) + "\n").getBytes());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            // Set the time of the alarm in order to find the difference
//            if(s.isAlarmSet() && !isTimeSet) {
//
//                Calendar c = Calendar.getInstance();
//                c.set(Calendar.HOUR_OF_DAY, s.getAlarmHour());
//                c.set(Calendar.MINUTE, s.getAlarmMinute());
//                isTimeSet = true;
//            }
//
//            // The current time
//            Calendar c2 = Calendar.getInstance();
//
//            if( counter >= 12 /*(c2.getTimeInMillis() - c2.getTimeInMillis()) < 1800000  30 minutes */ && !sent) {
//
//                blueSmirfSPP.writeByte((int)'s'); // 115 -> s
//                System.out.println("s is sent!");
//                sent = true;
//                blueSmirfSPP.flush();
//                try { Thread.sleep((long) (1000.0F/30.0F)); }
//                catch(InterruptedException e) { System.out.println("Error while sleeping the thread");}
//            }
//
//
//        }
//
//        try {
//            FileInputStream fis = getActivity().openFileInput((new FileNameManager()).getTempData_file_name());
//            int at = -1;
//            while((at = fis.read()) != -1) {
//
//                System.out.println("at: " + at + "--" + Character.getNumericValue(at));
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println("Thread end!!!");
//    }
}
