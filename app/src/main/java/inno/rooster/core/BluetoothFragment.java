/*
 * BluetoothFragment - Bluetooth control is done in this tab.
 * Version: 2.0
 * 
 * Author: Ali Burak Ünal
 */
package inno.rooster.core;

import info.androidhive.tabsswipe.R;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class BluetoothFragment extends Fragment {

	//Variables
	View rootView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.bluetooth_tab, container, false);
		
		ToggleButton tb = (ToggleButton) rootView.findViewById(R.id.toggleButton1);
		if( tb != null) {
			tb.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					
					BluetoothAdapter blue_adap = BluetoothAdapter.getDefaultAdapter(); 
					boolean isBluetoothEnabled = blue_adap.isEnabled();
					
					if( isChecked && !isBluetoothEnabled) {
						
						System.out.println("Enable");
						blue_adap.enable();
					}
					// How can I control if the alarm is set or not?
					else if( !isChecked && isBluetoothEnabled) {
						
						System.out.println("Disable");
						blue_adap.disable();
					}
				}
			});
		}
		else {
			
			System.out.println( "NULL tb");
		}
		
		return rootView;
	}

}
