package com.example.projectluan;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

public class MainActivity extends Activity implements ActionBar.TabListener {

	PRJAdapter mAdapter;
	DatabaseController mDbController;
	Cursor cursor;
	ViewPager mViewPager;
	ActionBar mActionBar;
	ImageView mImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mAdapter = new PRJAdapter(getFragmentManager(),getApplicationContext());
		
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mAdapter);
		
		mActionBar = getActionBar();
		
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mActionBar.setDisplayShowHomeEnabled ( false ); 
		mActionBar.setDisplayShowTitleEnabled ( false );
		mActionBar.addTab(mActionBar.newTab().setIcon(R.drawable.calendar).setTabListener(this));
		mActionBar.addTab(mActionBar.newTab().setIcon(R.drawable.alarm_icon).setTabListener(this));
		android.app.ActionBar actionBar = getActionBar();
		actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#b7321f")));
	
		
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						mActionBar.setSelectedNavigationItem(position);
					}
				});
		
	}
	
	
	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}
	

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

}
