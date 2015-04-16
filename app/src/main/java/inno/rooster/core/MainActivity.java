/*
 * MainActivity - Main activity of Rooster.
 * Version: 3.2
 * 
 * Author: Ali Burak �nal
 */
package inno.rooster.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

import info.androidhive.tabsswipe.R;
import inno.rooster.tabsswipe.adapter.TabsPagerAdapter;
import inno.rooster.viewadapter.NonSwipeableViewPager;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.sax.StartElementListener;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	private FileOutputStream fos;
	private Handler handler;
	private long handInterval = 5000;
	
	private long INSTANCE_ANALYSIS_TIME = 500;
	private Handler ia_handler;
	private int numOfHR;
	private double former;
	
	private FileNameManager fileNameManager;
	private int numOf30Minutes;
	
	public static AnalysisMaker analysisMaker_global;
	
	// Tab titles
	private String[] tabs = { "ALARM", "GRAPHICS", "BLUETOOTH"};

//    @Override
//    public void onAttach(Activity activity) {
//    	// TODO Auto-generated method stub
//    	super.onAttach(activity);
//    	this.activity = activity;
//    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Initilization

        //--------------------------------------------
        // JUST FOR THE DEMONSTRATION
        fromFileToInternalFile();
        //--------------------------------------------

//		viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        if(viewPager == null) {

            System.out.println("Null viewpager in MainActivity");
        }
		actionBar = getActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
		fileNameManager = new FileNameManager();
		numOf30Minutes = 0;
		
		ia_handler = new Handler();
		numOfHR = 0;
		
		// For the data read in each 30 minutes -------------------------------------
		handler = new Handler();
//		startRepeatingTask();
		
		// Data storage
		// In order to show that we can store the coming data -------------------------------------
//		System.out.println("Data storing started..");
//		storeData();
//		System.out.println("Data storing finished!\nFiles are located: " + getFilesDir());
//		System.out.println("Files are: ");
//		String[] temp = fileList();
//		for( int i = 0; i < temp.length; i++) {
//
//			System.out.println(temp[i]);
//		}
		
		viewPager.setAdapter(mAdapter);
		//actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);		

		// Adding Tabs
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}
		
		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

//    @Override
//    public void onDestroy() {
//
//        super.onDestroy();
//
//    }

    // Display a dialog which will appear when the alarm is ringed in order to stop the alarm
	private void displayAlarmStopDialog() {
		
		AlertDialog ad = new AlertDialog.Builder((Activity)this).create();
		ad.setCancelable(false);
		ad.setTitle("Alarm");
		ad.setMessage("It is time to wake up!");
		ad.setButton(AlertDialog.BUTTON_POSITIVE, "Alright", new DialogInterface.OnClickListener() {
		
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.dismiss();
		    }
		});
		ad.show();
	}
	
	// To test periodic task execution
    // Bluetooth communication will be done here!
	private void test() {
		
		System.out.println("5 more seconds has passed");
		Toast.makeText(getBaseContext(),"Bluetooth communication!",
                Toast.LENGTH_SHORT).show();
	}
	
	// Instant Analysis-----------------------------------------
//	Runnable at = new Runnable() {
//
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			int range = 100;
//			double temp_hr = random.nextDouble() * range + 20; // Here, the min value is assumed to be 55
////			System.out.println("Random HR: " + temp_hr);
//			numOfHR++;
//			if( numOfHR % 2 == 0) {
//
////				boolean b = analysisMaker_global.isThisMinuteREM(former, temp_hr);
////				if( b) {
////
////					displayAlarmStopDialog();
//					ia_stopRepeatingTask();
////				}
////				else {
////
////					ia_handler.postDelayed(at, INSTANCE_ANALYSIS_TIME);
////				}
//			}
//			else {
//
//				former = temp_hr;
//				ia_handler.postDelayed(at, INSTANCE_ANALYSIS_TIME);
//			}
//		}
//	};
	
//	void ia_startRepeatingTask() {
//		at.run();
//	}
//
//	void ia_stopRepeatingTask() {
//		ia_handler.removeCallbacks(at);
//	}
//
//	private void prepareForInstantAnalysis() {
//
//            analysisMaker_global.readDataAndCalculate();
//	}
	//---------------------------------------------------------
	
	// In order to get data from the wristband in every 30 minutes
//	Runnable mStatusChecker = new Runnable() {
//		@Override
//		public void run() {
////			test(); //this function can change value of mInterval.
//			numOf30Minutes++;
//			if( numOf30Minutes < 4) {
//
//				handler.postDelayed(mStatusChecker, handInterval);
//			}
//			else {
//
//
//				stopRepeatingTask();
//				System.out.println( "Dadeylidaladulali: " + actionBar.getTabAt(2));
////				Toast.makeText(getBaseContext(), "20 sec has passed!",Toast.LENGTH_SHORT).show();
//			}
//
//		}
//	};
//
//	void startRepeatingTask() {
//		mStatusChecker.run();
//	}
//
//	void stopRepeatingTask() {
//		System.out.println("MainActivity - prepareForInst - before");
//		prepareForInstantAnalysis();
//		System.out.println("MainActivity - prepareForInst");
////		ia_startRepeatingTask(); // For this
//		handler.removeCallbacks(mStatusChecker);
//	}
	//-------------------------------------------------------------------
	
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// on tab selected
		// show respected fragment view
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}


    private void fromFileToInternalFile() {

        Singleton s = Singleton.getInstance();
        try {

            AssetManager am = getAssets();
            System.out.println("Asset - after");
            BufferedReader inp_stream;
            BufferedReader mv_inp_stream;

            inp_stream = new BufferedReader(new InputStreamReader(am.open(s.getHr_fileName()), "UTF-8"));
            mv_inp_stream = new BufferedReader( new InputStreamReader(am.open(s.getMv_fileName()), "UTF-8"));

            FileOutputStream fos_datafile = openFileOutput((new FileNameManager()).getData_file_name(), MODE_PRIVATE);

                String tmp = "";

                int atkafa = 0;

                do {

                    try {
                        // Heart Rate
                        tmp = inp_stream.readLine();
                        if( tmp != null/* && i % 2 == 0*/) {
                            fos_datafile.write(((tmp.split(","))[7] + ",").getBytes());
                        }
                        // Movement Data
                        tmp = mv_inp_stream.readLine();
                        if( tmp != null) {

                            Double x = Double.parseDouble((tmp.split(","))[7]);
                            Double y = Double.parseDouble((tmp.split(","))[8]);
                            Double z = Double.parseDouble((tmp.split(","))[9]);

                            fos_datafile.write( (Math.sqrt( x*x + y*y + z*z) + "\n").getBytes());
                        }
                        atkafa++;
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } while( tmp != null && atkafa < 1000);

                inp_stream.close();
                mv_inp_stream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

	// Write the data read from the Bluetooth into a txt file in order to use it to analysis
	private void storeData() {
		
		try {
//            File hr_file = new File( Environment.getExternalStorageDirectory().getPath() + "/hrfile.txt");
//            File mv_file = new File( Environment.getExternalStorageDirectory().getPath() + "/mvfile.txt");
//            hr_file.createNewFile();
//            mv_file.createNewFile();
//            FileOutputStream hr_fOut = new FileOutputStream(hr_file);
//            FileOutputStream mv_fOut = new FileOutputStream(mv_file);
//            OutputStreamWriter hr_OutWriter = new OutputStreamWriter(hr_fOut);
//            OutputStreamWriter mv_OutWriter = new OutputStreamWriter(mv_fOut);
            
            // Open the file in which the data coming from the wristband is stored.
			// If the file is already created
			if( Arrays.asList(fileList()).contains(fileNameManager.getData_file_name()) && numOf30Minutes != 0) {
				
				fos = openFileOutput(fileNameManager.getFILENAME_TMP(), Context.MODE_APPEND);
			}
			// If the file is created at the first time
			else {
				
				fos = openFileOutput(fileNameManager.getFILENAME_TMP(), this.MODE_WORLD_READABLE);
			}
            
			// 60 data representing a bundle coming each 30 minutes
        /*    String fake_data = "93,0.94;85,0.49;83,0.50;77,0.49;76,0.96;71,0.49;79,0.49;69,0.50;93,0.94;82,0.63;" +
            		"76,0.96;71,0.49;79,0.49;69,0.50;93,0.94;85,0.49;83,0.50;77,0.49;69,0.50;68,0.33;" +
            		"76,0.96;71,0.49;79,0.49;69,0.50;93,0.94;85,0.49;83,0.50;77,0.49;69,0.50;68,0.33;" +
            		"76,0.96;71,0.49;79,0.49;69,0.50;93,0.94;85,0.49;83,0.50;77,0.49;69,0.50;68,0.33;" +
            		"76,0.96;71,0.49;79,0.49;69,0.50;93,0.94;85,0.49;83,0.50;77,0.49;69,0.50;68,0.33;" +
            		"76,0.96;71,0.49;79,0.49;69,0.50;93,0.94;85,0.49;83,0.50;77,0.49;69,0.50;68,0.33";*/
            
//            String[] data_per_half_min = fake_data.split(";");
            
//            for( int i = 0; i < data_per_half_min.length; i++) {
            	
//            	String[] temp = data_per_half_min[i].split(",");
//            	System.out.println("In storeData: " + data_per_half_min[i]);
//            	fos.write((data_per_half_min[i] + "\n").getBytes()); // Heart rate+"\n"
//            	fos.write((temp[1]+"\n").getBytes()); // Movement Data
            	
//            	hr_OutWriter.append(temp[0] + "\n");
//            	mv_OutWriter.append(temp[1] + "," + temp[2] + "," + temp[3] + "\n");
//            }

            fos.write(("54,68").getBytes());
            System.out.println("Bytes: " + ("54,68").getBytes());

            fos.close();

            System.out.println("//-------------------------------------------------------------------------------//");

            FileInputStream fis = openFileInput(fileNameManager.getFILENAME_TMP());
            int at;
            while((at = fis.read()) != -1) {

                System.out.println(Character.getNumericValue(at));
            }
            
//            hr_OutWriter.close();
//            mv_OutWriter.close();
//            hr_fOut.close();
//            mv_fOut.close();
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
	}

}
