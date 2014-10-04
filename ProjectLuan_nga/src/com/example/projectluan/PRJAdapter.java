package com.example.projectluan;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

public class PRJAdapter extends FragmentPagerAdapter {

	public static final int MYEVENTS_TAP = 0;
	public static final int ALARM_TAP = MYEVENTS_TAP + 1;
	
	Context mContext;
	public PRJAdapter(FragmentManager fm, Context context) {
		super(fm);
		mContext = context;
	}

	@Override
	public Fragment getItem(int position) {
		Fragment ret = null;
		switch (position) {
		case ALARM_TAP:
			ret = new AlarmFragment();
			break;
		case MYEVENTS_TAP:
			ret = new MyEventFragment();
			break;

		default:
			break;
		}
		return ret;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 2;
	}

}
