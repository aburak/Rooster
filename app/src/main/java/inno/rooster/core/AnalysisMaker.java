/*
 * AnalysisMaker - Both instance analysis and general analysis of sleep are made in this class.
 * Version: 3.2
 * 
 * Author: Ali Burak Unal
 */
package inno.rooster.core;

//import info.androidhive.tabsswipe.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Handler;
import android.provider.AlarmClock;
import android.text.format.Time;
import android.widget.TextView;
import android.widget.Toast;

public class AnalysisMaker {

	//Variables
	
	private final int MODE = Context.MODE_WORLD_READABLE;
    ArrayList<Double> hr_values;
	ArrayList<Double> clone_hr_values;
    ArrayList<Double> mv_values;
    ArrayList<Integer> index_array;
    ArrayList<Double> Rk_values;
    double rem_percentage;
    int age_of_person = 40; //We need to get this value from the user as input
    int rem_last_index;
	private Context context;
	private FileNameManager fileNameManager;
	private final int LIMIT = 1000;
	private final int SIZE_OF_BUNDLE = 9; //In terms of bytes including double + "," + double + "\n"
	private final int averager = 20; //Averager is assigned 20. This can be changed and accuracy could be affected by this.
	private Random random;
	private int numOfHR;
	private double former;
	private Handler handler;
	
	public AnalysisMaker( Context context) {
		
		this.context = context;
	}
	
	public double getRem_percentage() {
		return rem_percentage;
	}
	
	public ArrayList<Double> getHr_values() {
		return hr_values;
	}

	public ArrayList<Double> getMv_values() {
		return mv_values;
	}

	public ArrayList<Integer> getIndex_array() {
		return index_array;
	}
	
	public ArrayList<Double> getRk_values() {
		return Rk_values;
	}

	public int getRem_last_index() {
		return rem_last_index;
	}
	
	public int getAge() {
		
		return age_of_person;
	}
	
	public void readDataAndCalculate() {
		
		fileNameManager = new FileNameManager();
		random = new Random();
//		handler = new Handler();
		
		// The data is read from the file - for demonstration
   	 	readData("erkek_heart_rate_EV#-2.txt", "erkek_movement_EV#-6.txt"); // Real data
//		readFromInternalMemory(); // Fake data
   	 	
        clone_hr_values = (ArrayList<Double>) hr_values.clone();
        
        System.out.println( "AnalysisMaker - in readDataAndCalculate - hr_values.size() = " + hr_values.size());
        
        // Initialize index array
        index_array = new ArrayList<Integer>();
        for( int i = 0; i < (hr_values.size() / 2); i++) {
        	
        	index_array.add(i);
        }
        
        System.out.println( "AnalysisMaker - Between middle and one step ahead from the middle");
        
        if( hr_values.size() <= 0 || mv_values.size() <= 0) {
        	
        	//Exception handling
        }
        else {
        	
        	System.out.println( "AnalysisMaker - Size of index_array: " + index_array.size());
        	rem_last_index = findLastREMIndex(clone_hr_values, mv_values, age_of_person);
        }
        
        // Calculate the REM percentage and store the result in rem_percentage variable
        calculateREMPercentage(rem_last_index, Rk_values.size());
        
        // Store the analysis
        try {
			storeAnalysis();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   }
	
	// Find REM stages of the sleep
    private int findLastREMIndex( ArrayList<Double> heart_rate, ArrayList<Double> movement, int age) {
    	
    	System.out.println( "AnalysisMaker - Size of hr: " + heart_rate.size());
    	System.out.println( "AnalysisMaker - Size of ind: " + index_array.size());
    	
    	// R(k) values are calculated here.
    	calculateRkValues(heart_rate);
    	
    	System.out.println( "AnalysisMaker - Calculation of R(k) is finished");
    	
    	// Sort R(k) values
    	quickSort(Rk_values, index_array, 0, Rk_values.size() - 1);
//    	Collections.sort(list, new Comparator<T>() {
//    		public int compare(T lhs, T rhs) {
//    			return (lhs.getValue() < rhs.getValue()) ? 1 : -1;
//    		};
//		});
    	
    	System.out.println( "AnalysisMaker - Sort is finished");
    	
    	// r_rem(age) value - indicates the limit of the REM stages
    	double RValue = calculateRValue(age);

    	int r_index = (int)Math.floor(RValue * Rk_values.size() / 100);
    	// delta_r_rem(age) value - indicates the standard deviation
    	double Std = calculateStd(age);

    	int delta_r = (int)Math.floor(Std * Rk_values.size() / 100);
    	
    	System.out.println( "AnalysisMaker - r_index: " + r_index + " - delta_r: " + delta_r);
    	
    	double max = -9999;
    	int max_index = -1;
    	
    	System.out.println( "AnalysisMaker - RValue: " + RValue + " - Std: " + Std);
    	
    	// Find rem_last which indicates the last element of REM
    	//for( int i = (int)(Math.floor(RValue - Std) * movement.size() / 100); i < (int)(Math.floor(RValue + Std) * movement.size() / 100); i++) {
    	for( int i = r_index - delta_r; i < r_index + delta_r; i++) {
    		
    		if( movement.get(i) > max) {
    			
    			max = movement.get(i);
    			max_index = i;
    		}
    	}
    	
    	// Set the REM percentage according to the max_index
//    	TextView rem_per = (TextView)rootView.findViewById(R.id.rem_percentage);
//    	NumberFormat formatter = new DecimalFormat("#0.##");
//    	rem_per.setText( rem_per.getText() + " " + formatter.format(((max_index * 100) / Rk_values.size())));
    	
    	System.out.println(" AnalysisMaker - Max_index: " + max_index);
    	return max_index;
    }
    
    private void calculateREMPercentage( int value, int numOfInstances) {
    	
    	NumberFormat formatter = new DecimalFormat("#.##");
    	rem_percentage = Double.parseDouble((String.format("%.2f", ((value * 100.0) / numOfInstances))).replace(",","."));
    }
    
    // Calculate R(k) values
    private void calculateRkValues( ArrayList<Double> hr)  {
		
    	Rk_values = new ArrayList<Double>();
    	
    	for( int i = 1; i < hr.size(); i+= 2) {
    		
    		int tmp_sum = 0;
    		// For the case in which the index is  not enough to find the average with enough number
    		if( i < averager * 2) {
    			
    			for( int j = 0; j <= i; j+= 2) {
    				
    				tmp_sum += Math.abs(hr.get(j+1) - hr.get(j));
    			}
    			// Average
    			Rk_values.add( tmp_sum / ((i+1)/2.0));
    		}
    		// For the normal case
    		else {
    			
				for( int j = i - (averager-1) * 2 - 1; j <= i; j+= 2) {
    				
    				tmp_sum += Math.abs(hr.get(j+1) - hr.get(j));
    			}
				// Average
    			Rk_values.add( tmp_sum / (averager * 1.0));
    		}
    	}
	}
    
    // Standard deviation of REM stage (in percentage)
    private double calculateRValue( int age) {
		
    	return 3.36 * Math.pow(10,-6) * Math.pow(age,4) - 6.89 * Math.pow(10,-4) * Math.pow(age,3) + 4.55 *
    			Math.pow(10,-2) * Math.pow(age,2) - 1.19 * age + 35;
	}
    
    // Incidence ratio of REM stage (in percentage)
    private double calculateStd( int age) {
    	
    	return 1.14 * Math.pow(10,-6) * Math.pow(age,4) - 1.60 * Math.pow(10,-4) * Math.pow(age,3) + 6.63 *
    			Math.pow(10,-3) * Math.pow(age,2) - 6.02 * Math.pow(10,-2) * age + 3.49;
    }
    
    // ---------------------------------Quick Sort and Partition---------------------------------
    private int partition(ArrayList<Double> arr, ArrayList<Integer> indices, int left, int right) {
    	
          int i = left, j = right;
          double tmp;
          int tmp_index;
          double pivot = arr.get((left + right) / 2);
//          System.out.println( "AnalysisMaker - arr: " + arr.size() + " - indices: " + indices.size());
          while (i <= j) {
                while (arr.get(i) < pivot)
                      i++;
                while (arr.get(j) > pivot)
                      j--;
                if (i <= j) {
                	  //System.out.println("Array");
                      tmp = arr.get(i);
                      arr.set(i, arr.get(j));
                      arr.set(j, tmp);
                      //System.out.println("Index");
                      // Arrange indices
                      tmp_index = indices.get(i);
                      //System.out.println("Ya bu");
                      indices.set(i, indices.get(j));
                      //System.out.println("Ya da bu");
                      indices.set(j, tmp_index);
                      i++;
                      j--;
                      //System.out.println("Finish Array and Index");
                }
          };
         
          return i;
    }
     
    private void quickSort(ArrayList<Double> arr, ArrayList<Integer> indices, int left, int right) {
    	
          int index = partition(arr, indices, left, right);
          if (left < index - 1)
                quickSort(arr, indices, left, index - 1);
          if (index < right)
                quickSort(arr, indices, index, right);
    }
    //----------------------------------------------------------------------------------
    
    
    
    // Read heart rate and movement data from the specified file
    private void readData( String hr_file_name, String mv_file_name) {
    	
    	// PART OF OUR PROJECT
    	System.out.println("Asset - before");
        AssetManager am = context.getAssets();
        System.out.println("Asset - after");
        BufferedReader inp_stream;
        BufferedReader mv_inp_stream;
        try {
			inp_stream = new BufferedReader(new InputStreamReader(am.open(hr_file_name), "UTF-8"));
			mv_inp_stream = new BufferedReader( new InputStreamReader(am.open(mv_file_name), "UTF-8"));
			
			String tmp = "";
	        hr_values = new ArrayList<Double>();
	        mv_values = new ArrayList<Double>();
	        
	        int atkafa = 0;
	        
	        do {
	        	
	        	try {
	        		// Heart Rate
					tmp = inp_stream.readLine();
					if( tmp != null/* && i % 2 == 0*/) {
						
						hr_values.add( Double.parseDouble((tmp.split(","))[7]));
						//System.out.println(values.get(values.size() - 1));
					}
					//i++;
					// Movement Data
					tmp = mv_inp_stream.readLine();
					if( tmp != null) {
						
						Double x = Double.parseDouble((tmp.split(","))[7]);
						Double y = Double.parseDouble((tmp.split(","))[8]);
						Double z = Double.parseDouble((tmp.split(","))[9]);
						
						mv_values.add( Math.sqrt( x*x + y*y + z*z));
					}
					atkafa++;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        } while( tmp != null && atkafa < LIMIT);
	        System.out.println("AnalysisMaker - atkafa: " + atkafa);
//	        hr_values = (ArrayList<Double>)hr_values.subList(40, hr_values.size());
//	        mv_values = (ArrayList<Double>)mv_values.subList(40, mv_values.size());
	        inp_stream.close();
	        mv_inp_stream.close();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private void readFromInternalMemory() {
    	
    	FileInputStream fis = null;
		try {
			fis = context.openFileInput(fileNameManager.getData_file_name());
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		hr_values = new ArrayList<Double>();
		mv_values = new ArrayList<Double>();
		
		String temp_str;
		String[] temp_arr;
		
		InputStreamReader inputStreamReader = new InputStreamReader(fis);
	    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		
		try {
			
			while( (temp_str = bufferedReader.readLine()) != null) {
				
				//System.out.println(temp_str);
				temp_arr = temp_str.split(",");
//				System.out.println("AnalysisMaker - temp_arr[0] = " + temp_arr[0]);
//				System.out.println("AnalysisMaker - temp_arr[1] = " + temp_arr[1]);
				hr_values.add(Double.parseDouble(temp_arr[0]));
				mv_values.add(Double.parseDouble(temp_arr[1]));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private void storeAnalysis() throws IOException {
    	
    	// R(k) values are stored
		FileOutputStream fos = context.openFileOutput(fileNameManager.getAnalysis_file_name(), MODE);
        for( int i = 0; i < Rk_values.size(); i++) {
        	
        	fos.write((Rk_values.get(i) + "," + index_array.get(i) + "\n").getBytes()); // R(k) and their index values will be enough to evaluate the analysis
        }
        fos.close();
        // REM last index is stored
        fos = context.openFileOutput(fileNameManager.getRem_last_index_file_name(), MODE);
        fos.write((rem_last_index + "").getBytes()); // The last index indicating that REM is finished
        fos.close();
    }

    // Return if the current minute is REM or non-REM
    public boolean isThisMinuteREM( double hr_former, double hr_latter) {
    	
    	double temp_Rk = 0;
//    	hr_values.add(hr_former);
//    	hr_values.add(hr_latter);
    	System.out.println("Former: " + hr_former + " - Latter: " + hr_latter);
    	for( int i = hr_values.size() - (2*averager); i < hr_values.size(); i+=2) {
    		
    		temp_Rk =+ Math.abs(hr_values.get(i+1) - hr_values.get(i));
    	}
    	
    	temp_Rk = temp_Rk / averager;
    	
    	// Add the given heart rate values into the Heart Rate list
    	System.out.println("R(k): " + Rk_values.get(Rk_values.size() - rem_last_index) + "\n" + 
    			"temp_Rk:" + temp_Rk);
    	return true;//(Rk_values.get(Rk_values.size() - rem_last_index) < temp_Rk);
    }
}
