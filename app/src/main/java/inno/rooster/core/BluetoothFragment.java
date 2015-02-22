/*
 * BluetoothFragment - Bluetooth control is done in this tab.
 * Version: 2.0
 * 
 * Author: Ali Burak �nal
 */
package inno.rooster.core;

import info.androidhive.tabsswipe.R;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class BluetoothFragment extends Fragment implements View.OnClickListener {

	//Variables
	private View rootView;
    private ArrayList<String> bluetoothAddresses;
    private String bluetoothAddress;
    private BlueSmirfSPP blueSmirfSPP;
    private boolean isThreadRunning;

    private Spinner paired_devices;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = context;
        isThreadRunning = false;
        bluetoothAddress = null;
        blueSmirfSPP = new BlueSmirfSPP();
        bluetoothAddresses = new ArrayList<String>();
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.bluetooth_tab, container, false);

        // Initialization



		final ToggleButton tb = (ToggleButton) rootView.findViewById(R.id.toggleButton1);
        final Button con_but = (Button) rootView.findViewById(R.id.connect_button);
        paired_devices = (Spinner) rootView.findViewById(R.id.paired_device_spinner);

        paired_devices.setOnItemSelectedListener(this);

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
                        while( isBluetoothEnabled == blue_adap.isEnabled());
					}
					// How can I control if the alarm is set or not?
					else if( !isChecked && isBluetoothEnabled) {
						
						System.out.println("Disable");
						blue_adap.disable();
                        con_but.setEnabled(false);
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

//            MainActivity.blueSmirfSPP.connect();
        }
    }
}
