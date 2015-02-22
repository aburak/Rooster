/*
 * AlarmFragment - Setting an alarm is done in this class. In order to set an alarm, Bluetooth should be opened in advance.
 * Version: 3.2
 * 
 * Author: Ali Burak Unal
 */
package inno.rooster.core;

import android.provider.AlarmClock;
import info.androidhive.tabsswipe.R;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

public class AlarmFragment extends Fragment implements View.OnClickListener {

	//Variables
	private Alarm alarm;
	private View rootView;
	private Button btn_set;
	private Button btn_cancel;
	protected boolean isAlarmSet;
	private int alarm_hour;
	private int alarm_minute;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.alarm_tab, container, false);
		// Set buttons' onClickListeners
		btn_set = (Button) rootView.findViewById(R.id.Set);
		btn_set.setOnClickListener(this);
//		btn_cancel = (Button) rootView.findViewById(R.id.Cancel);
//		btn_cancel.setOnClickListener(this);
		
//		isAlarmSet = false;
		
//		alarm = new Alarm();
		
		return rootView;
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
        case R.id.Set:
        	System.out.println("Set is clicked on");
        	BluetoothAdapter blue_adap = BluetoothAdapter.getDefaultAdapter(); 
			
        	if( !blue_adap.isEnabled()) {
        		
        		String text = "To set the alarm, first open Bluetooth";
        		Toast toast = Toast.makeText(rootView.getContext(), text, 5);
        		toast.show();
        	}
        	else {
        		
        		TimePicker tp = (TimePicker) rootView.findViewById(R.id.timePicker1);
	        	/*
	        	Context context_set = this.getActivity().getApplicationContext();
	        	alarm.SetOnetimeTimer(context_set, tp.getCurrentHour(), tp.getCurrentMinute());
	        	*/
        		
        		alarm_hour = tp.getCurrentHour();
        		alarm_minute = tp.getCurrentMinute();
	        	
	        	Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
	        	i.putExtra(AlarmClock.EXTRA_HOUR, tp.getCurrentHour());
	        	i.putExtra(AlarmClock.EXTRA_MINUTES, tp.getCurrentMinute());
	        	startActivity(i);
	        	
	        	isAlarmSet = true;
	        	
	            break;
        	}
//        case R.id.Cancel:
//            System.out.println("Cancel is clicked on");
//            Context context_cancel = this.getActivity().getApplicationContext();
//            alarm.CancelAlarm(context_cancel);
//            break;
		}
	}
	
	protected boolean getAlarmState() {
		
		return isAlarmSet;
	}
	
	public int getAlarm_hour() {
		
		return alarm_hour;
	}
	
	public int getAlarm_minute() {
		
		return alarm_minute;
	}
}
