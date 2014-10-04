package com.example.projectluan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DialogEvent extends Activity implements OnClickListener {
	
	LinearLayout mLayout;
	TextView mTxtCancel;
	private WakeLock mWakeLock;
	public MediaPlayer mp;
	
	DatabaseController mDbController;
	Cursor cursor;
	TextView mNameEventTxt;
	TextView mLocationTxt;
	TextView mDateTxt;
	TextView mHourTxt;

	int ID;
	String NameEvent;
	String Location;
	String Date;
	String Date1;
	String Hour;
	String RingtonePath;
	String SilenceAfter;
	int timeRingtone;
	
	public final String TAG = this.getClass().getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_event);
		
		mLayout = (LinearLayout) findViewById(R.id.dialog_layout);
		mTxtCancel = (TextView) findViewById(R.id.txtCancelDialog);
		mLayout.setOnClickListener(this);
		mTxtCancel.setOnClickListener(this);
		Intent intent = getIntent();
		ID = intent.getIntExtra("dID", 0);
		mDbController = new DatabaseController(this);
		
		String stSQL = "SELECT * FROM " + DatabaseController.TABLE_NAME + " WHERE " + DatabaseController.ID_COL + "=" + ID + ";";
		cursor = mDbController.rawQuery(stSQL, null);
		if(cursor.moveToFirst()){
			do{
				mNameEventTxt = (TextView) findViewById(R.id.dNameEvent);
				NameEvent = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.NAME_EVENT_COL));
				mNameEventTxt.setText(NameEvent);
				mLocationTxt = (TextView)findViewById(R.id.dLocation);
				Location = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.LOCATION_COL));
				mLocationTxt.setText(Location);
				mDateTxt = (TextView)findViewById(R.id.dDate);
				Date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.DATE_COL));
				Date1 = trimspace(Date);
				mDateTxt.setText(Date1);
				mHourTxt = (TextView)findViewById(R.id.dHour);
				Hour = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.HOUR_COL));
				mHourTxt.setText(Hour);
				RingtonePath = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.RINGTONE_PATH_COL));
				SilenceAfter = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.SILENCE_AFTER_COL));

			}while (cursor.moveToNext());
		}

		// -----------------------------//
		
		Runnable releaseWakelock = new Runnable() {
			
			@Override
			public void run() {
				
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
				if (mWakeLock != null && mWakeLock.isHeld()) {
					mWakeLock.release();
				}
			}
		};
		
		
		
//		mp = new MediaPlayer();
//		mp = MediaPlayer.create(getApplicationContext(), R.raw.mx);
//		mp.setLooping(true);
//		mp.start();
		
		mp = new MediaPlayer();
		try {
			if (RingtonePath != null && !RingtonePath.equals("")) {
				Uri toneUri = Uri.parse(RingtonePath);
				if (toneUri != null) {
					mp.setDataSource(this, toneUri);
					mp.setAudioStreamType(AudioManager.STREAM_ALARM);
					mp.setLooping(true);
					mp.prepare();
					mp.start();
//					SilenceAfter();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Acquire wakelock
		PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
			if (mWakeLock == null) {
				mWakeLock = pm.newWakeLock((PowerManager.FULL_WAKE_LOCK | PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), TAG);
			}

			if (!mWakeLock.isHeld()) {
				mWakeLock.acquire();
			}
	}
	
	public void SilenceAfter() {
		if(SilenceAfter.contains("20"))
			timeRingtone = 20;
		if(SilenceAfter.contains("30"))
			timeRingtone = 30;
		if(SilenceAfter.contains("50"))
			timeRingtone = 50;
		if(SilenceAfter.contains("60"))
			timeRingtone = 60;
		Log.d("LuanDT", "TG do chuong : " + timeRingtone);
	}
	
	public void seekTo(int i) {
		i = 3000;
	}
	
	public String trimspace(String str)
	{
	    str = str.replaceAll("\\s+", " ");
	    str = str.replaceAll("(^\\s+|\\s+$)", "");
	    return str;
	}
	
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.dialog_layout:
			intent.setClass(getApplicationContext(), MainActivity.class);
			intent.setClass(getApplicationContext(), Event.class);
			intent.putExtra("did", ID);
			mp.stop();
			startActivity(intent);
			
			break;
		case R.id.txtCancelDialog:
			setResult(RESULT_CANCELED, intent);
			mp.stop();
			break;

		default:
			break;
		}
		finish();
	}

}
