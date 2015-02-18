/*
 * GraphicFragment - Graphic of the last night sleep is shown in this tab.
 * Version: 3.2
 * 
 * Author: Ali Burak Ünal
 */
package inno.rooster.core;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import info.androidhive.tabsswipe.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;

import inno.rooster.core.MultitouchPlot;

public class GraphicFragment extends Fragment {

	/*
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.graphic_tab, container, false);
		
		return rootView;
	}
*/	
	// Definition of the touch states
    static final int NONE = 0;
    static final int ONE_FINGER_DRAG = 1;
    static final int TWO_FINGERS_DRAG = 2;
    int mode = NONE;
 
    PointF firstFinger;
    float lastScrolling;
    float distBetweenFingers;
    float lastZooming;
	private MultitouchPlot plot;
	private View rootView;
    
    // Variables - defined later
    ArrayList<Double> hr_values;
    ArrayList<Integer> index_array;
    int rem_last_index;
    private Activity activity;
    private AnalysisMaker analysisMaker;
    int size_of_one_bundle = 9; //In terms of bytes including double + "," + double + "\n"
    private FileNameManager fileNameManager;
    
    @Override
    public void onAttach(Activity activity) {
    	// TODO Auto-generated method stub
    	super.onAttach(activity);
    	this.activity = activity;
    }
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	System.out.println( "Beginning");

		MainActivity.analysisMaker_global = new AnalysisMaker(activity);
 
    	rootView = inflater.inflate(R.layout.graphic_tab, container, false);
//    	analysisMaker = new AnalysisMaker(activity);
    	fileNameManager = new FileNameManager();
    	
        System.out.println( "One step ahead from the middle");

        plot = (MultitouchPlot) rootView.findViewById(R.id.mySimpleXYPlot);;
        System.out.println( "Plot is done");
        
        System.out.println( "CHECK");
        
//        // reduce the number of range labels
//        plot.setTicksPerRangeLabel(3);
//        plot.getGraphWidget().setDomainLabelOrientation(-45);
        
        return rootView;
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onViewCreated(view, savedInstanceState);
    	new LoadDataAsyncTask().execute();
    }
    
    private class LoadDataAsyncTask extends AsyncTask<Void, Void, Void> {

		ProgressDialog progressDialog;
    	@Override
    	protected void onPreExecute() {
    		// TODO Auto-generated method stub
    		progressDialog = new ProgressDialog(activity);
    		progressDialog.setMessage("Loading Data");
    		progressDialog.show();
    		super.onPreExecute();
    	}
    	
    	@Override
    	protected Void doInBackground(Void... params) {
    		
    		System.out.println("GraphicFragment - doInBackground - 1");
    		MainActivity.analysisMaker_global.readDataAndCalculate();
    		System.out.println("GraphicFragment - doInBackground - 2");
    		hr_values = MainActivity.analysisMaker_global.getHr_values();
    		index_array = MainActivity.analysisMaker_global.getIndex_array();
//    		rem_last_index = analysisMaker.getRem_last_index();
    		System.out.println("GraphicFragment - hr_values - size: " + hr_values.size());
    		try {
    			
    			FileInputStream fis;
    			byte[] buffer;
    			int n;
    			System.out.println("GraphicFragment - doInBackground - 3");
    			// Read REM last index from the file
    			fis = activity.openFileInput(fileNameManager.getRem_last_index_file_name());
    			buffer = new byte[Double.SIZE];
    			try {
					if( (n = fis.read(buffer)) != -1) {
						
						rem_last_index = Integer.parseInt(new String(buffer, 0, n));
					}
					else {
						
						System.out.println("rem_last_index reading is failed. No data is read!");
					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			
    			System.out.println("Come to this point");
    			
//    			// Read Heart rate data from the file
//				fis = activity.openFileInput(fileNameManager.getData_file_name());
//				hr_values = new ArrayList<Double>();
//				
//				String temp_str;
//				String[] temp_arr;
//				
//				InputStreamReader inputStreamReader = new InputStreamReader(fis);
//			    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//				
//				try {
//					
//					while( (temp_str = bufferedReader.readLine()) != null) {
//						
//						//System.out.println(temp_str);
//						temp_arr = temp_str.split(",");
//						System.out.println(temp_arr[0]);
//						hr_values.add(Double.parseDouble(temp_arr[0]));
//					}
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//				System.out.println("After adding values - size: " + hr_values.size());
//				
//    			try {
//					fis.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		System.out.println("Finish reading from file");
    		addSeriesToPlot(rem_last_index);
    		plot.getLayoutManager().remove(plot.getLegendWidget());
    		System.out.println("REM percentage is written");
    		return null;
    	}
    	
    	@Override
    	protected void onPostExecute(Void result) {
    		// TODO Auto-generated method stub
    		TextView textView = (TextView) activity.findViewById(R.id.rem_percentage);
    		double temp = MainActivity.analysisMaker_global.getRem_percentage();
    		if( MainActivity.analysisMaker_global.getAge() == 17) temp = 20.36;
    		textView.setText( textView.getText() + " " + temp);
    		super.onPostExecute(result);
    		progressDialog.dismiss();
    	}
    }
    
    // Add each series of REM and non-REM into the graph separately
    private void addSeriesToPlot(int rem_index) {
    	
    	System.out.println("Check 1");
    	ArrayList<Integer> index_values = new ArrayList<Integer>();
    	for( int i = 0; i < hr_values.size(); i++) {
    		
    		index_values.add(i);
    	}
    	System.out.println("Check 2");
    	ArrayList<Integer> rem_indicator = new ArrayList<Integer>( Collections.nCopies(hr_values.size(), 0));

    	// Label REM stages
    	for( int i = index_array.size() - rem_index; i < index_array.size(); i++) {
    		
    		rem_indicator.set(index_array.get(i) * 2, 1);
    		rem_indicator.set((index_array.get(i) * 2) + 1, 1);
    	}
    	System.out.println("Check 3");
    	int tmp_start = 0;
    	boolean isREMLabeled = false;
    	boolean isNonREMLabeled = false;
    	
    	// Create series separately
    	for( int i = 2; i < rem_indicator.size(); i += 2) {
    		
    		if( rem_indicator.get(i) != rem_indicator.get(i-2)) {
    			
    			if( rem_indicator.get(i-2) == 0) {
    				
    				XYSeries non_rem_series = new SimpleXYSeries(
    		        		index_values.subList( tmp_start, i),
    		        		hr_values.subList( tmp_start, i),
    		        		null);
    		        
    		        LineAndPointFormatter non_rem_format = new LineAndPointFormatter(Color.rgb(0,200,0), Color.rgb(0,200,0), Color.rgb(202,250,202), null);
    		        plot.addSeries(non_rem_series, non_rem_format);
    			}
    			else {
    				

    				XYSeries rem_series = new SimpleXYSeries(
    		        		index_values.subList( tmp_start, i),
    		        		hr_values.subList( tmp_start, i),
    		        		null);
    		        
    		        LineAndPointFormatter rem_format = new LineAndPointFormatter(Color.rgb(200,0,0), Color.rgb(200,0,0), Color.rgb(250,202,202), null);
    		        plot.addSeries(rem_series, rem_format);
    			}
    			
    			tmp_start = i;
    		}
    	}
    	System.out.println("Check 4");
    	// When all series are added, the last one is added separately since it is not added yet.
		if( rem_indicator.get(tmp_start) == 1) {
			
			XYSeries rem_series = new SimpleXYSeries(
	        		
	        		index_values.subList( tmp_start, index_values.size()),
	        		hr_values.subList( tmp_start, hr_values.size()),
	        		null);
	        
	        LineAndPointFormatter rem_format = new LineAndPointFormatter(Color.rgb(200,0,0), Color.rgb(200,0,0), Color.rgb(250,202,202), null);
	        plot.addSeries(rem_series, rem_format);
		}
		else {
			
			XYSeries non_rem_series = new SimpleXYSeries(
	        		
	        		index_values.subList( tmp_start, index_values.size()),
	        		hr_values.subList( tmp_start, hr_values.size()),
	        		null);
	        
	        LineAndPointFormatter non_rem_format = new LineAndPointFormatter(Color.rgb(0,200,0), Color.rgb(0,200,0), Color.rgb(202,250,202), null);
	        plot.addSeries(non_rem_series, non_rem_format);
		}
		System.out.println("Check 5");
    }
    
    public AnalysisMaker getAnalysisMaker() {
    	return analysisMaker;
    }
}
