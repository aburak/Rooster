/*
 * TabsPagerAdapter - It is used as pager adapter for tabs.
 * Version: 2.1
 * 
 * Author: Ali Burak �nal
 */

package inno.rooster.tabsswipe.adapter;

import inno.rooster.core.AlarmFragment;
import inno.rooster.core.BluetoothFragment;
import inno.rooster.core.GraphicFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			// Alarm fragment
			return new AlarmFragment();
		case 1:
			// Graphics fragment
			return new GraphicFragment();
		case 2:
			// Bluetooth fragment
			return new BluetoothFragment();
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 3;
	}

}
