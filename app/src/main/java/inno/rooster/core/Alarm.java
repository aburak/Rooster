/*
 * Alarm - It is responsible for managing alarm.
 * Version: Deprecated
 * 
 * Author: Ali Burak Unal
 */

package inno.rooster.core;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.AlarmClock;
import android.widget.Toast;

public class Alarm extends BroadcastReceiver 
{    
	
	 final public static String ONE_TIME = "onetime";
	 int alarm_hour;
	 int alarm_minute;
	 @Override
	 public void onReceive(Context context, Intent intent) {
	 	 PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
	     PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
	     //Acquire the lock
	     wl.acquire();
	
	     //You can do the processing here.
	     /*
	     Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
     	 i.putExtra(AlarmClock.EXTRA_HOUR, alarm_hour);
     	 i.putExtra(AlarmClock.EXTRA_MINUTES, alarm_minute);
     	 context.startActivity(i);
     	 */
	     /*
	     AlertDialog.Builder builder = new AlertDialog.Builder(context);

	     builder.setTitle("Good Morning");
	     builder.setMessage("Time to wake up!");
	     builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int id) {
	             dialog.cancel();
	        }
	    });
	     //builder.setNegativeButton("Cancel", null);
	     builder.show();*/
	     
	     Bundle extras = intent.getExtras();
	     StringBuilder msgStr = new StringBuilder();
	     
	     if(extras != null && extras.getBoolean(ONE_TIME, Boolean.FALSE)){
			 //Make sure this intent has been sent by the one-time timer button.
			 msgStr.append("One time Timer : ");
	     }
	     Format formatter = new SimpleDateFormat("hh:mm:ss a");
	     msgStr.append(formatter.format(new Date()));
	
	     Toast.makeText(context, msgStr, Toast.LENGTH_LONG).show();
	     
	     //Release the lock
	     wl.release();
	 }

	 public void SetAlarm(Context context)
    {
		 AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		 Intent intent = new Intent(context, Alarm.class);
		 intent.putExtra(ONE_TIME, Boolean.FALSE);
		 PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		 //After after 5 seconds
		 am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 5 , pi); 
    }

    public void CancelAlarm(Context context)
    {
    	Intent intent = new Intent(context, Alarm.class);
    	PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    public void SetOnetimeTimer(Context context, int hour, int minute){
    	AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Alarm.class);
        intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
        intent.putExtra(ONE_TIME, Boolean.TRUE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 00);
		
		alarm_hour = hour;
		alarm_minute = minute;
		
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);//
    }
}