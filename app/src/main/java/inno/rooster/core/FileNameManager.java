/*
 * FileNameManager - It holds all file names in order not to create confusion about file names used in other classes.
 * Version: 2.1
 * 
 * Author: Ali Burak Ünal
 */

package inno.rooster.core;

public class FileNameManager {

	// File Names
	private final String FILENAME_ANALYSIS = "rooster_analysis";
	private final String FILENAME_REM_LAST_INDEX = "rooster_rem_last_index";
	private String FILENAME_DATA = "rooster_data";
	
	public FileNameManager() {
		
	}
	
	public String getAnalysis_file_name() {
		return FILENAME_ANALYSIS;
	}

	public String getRem_last_index_file_name() {
		return FILENAME_REM_LAST_INDEX;
	}

	public String getData_file_name() {
		return FILENAME_DATA;
	}
}
