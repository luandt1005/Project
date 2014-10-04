package com.example.projectluan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AlarmScreen extends Activity {
	
	DatabaseController mDataBaseController;
	Cursor cursor;
	
	TextView txtHour,txtMinute,txtLabel;
	Button btnDismiss;
	private WakeLock mWakeLock;
	public MediaPlayer mp;
	 Vibrator vibrator;
	
	String Label,Hour,Minute,RingtonePath;
	
	int ID;
	
	public final String TAG = this.getClass().getSimpleName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.alarm_screen_active);
		
		txtLabel = (TextView)findViewById(R.id.alarm_screen_label);
		txtHour = (TextView)findViewById(R.id.txtHour);
		txtMinute = (TextView)findViewById(R.id.txtMinute);
		btnDismiss = (Button)findViewById(R.id.alarm_screen_dismiss);
		
		
		final Intent intent = getIntent();
		ID = intent.getIntExtra("dID", 0);
		Log.d("NgadV", "dID= " + ID);
		mDataBaseController = new DatabaseController(this);
		
		
		//=============================================//
		
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

		
		// Acquire wakelock
		PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
			if (mWakeLock == null) {
				mWakeLock = pm.newWakeLock((PowerManager.FULL_WAKE_LOCK | PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), TAG);
			}

			if (!mWakeLock.isHeld()) {
				mWakeLock.acquire();
			}
			btnDismiss.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					mp.stop();
					vibrator.cancel();
					finish();
					System.exit(0);
				}
			});
	}
	
	// khoa' phim' back
	@Override
	public void onBackPressed() {
		
	}
	// chay activity len tren man hinh khoa'
	@Override
	public void onAttachedToWindow() {
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | 
				WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED );
	}
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		Log.d("NgaDV", "keycode -= " + keyCode);
//		if (keyCode == event.KEYCODE_HOME) {
//			Toast.makeText(getApplicationContext(), "may vua bam home", 1).show();
//			mp.stop();
//			finish();
//		}
//		else {
//			Toast.makeText(getApplicationContext(), " home", 1).show();
//		}
//		return super.onKeyDown(keyCode, event);
//	}
	
	//tat bao thuc khi click phim Home
	@Override
	protected void onUserLeaveHint() {
		mp.stop();
		vibrator.cancel();
		finish();
		super.onUserLeaveHint();
	}
	@Override
	protected void onResume() {

		String stSQL = "SELECT * FROM " + DatabaseController.TABLE_NAME_AL + " WHERE " + DatabaseController.ID_AL_COL + "=" + ID + ";";
		cursor = mDataBaseController.rawQuery(stSQL, null);
		if(cursor.moveToFirst()){
			do{
				Label = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.LABEL_AL_COL));
				txtLabel.setText(Label);
				
				Log.d("NgaDV", Label);
				
				Hour = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.HOUR_AL_COL));
				if (Integer.parseInt(Hour) < 10) {
					txtHour.setText("0"+Hour);
				}
				else {
					txtHour.setText(Hour);
				}
				
				
				Log.d("NgaDV", Hour);
				Minute = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.MINUTE_AL_COL));
				if (Integer.parseInt(Minute) < 10) {
					txtMinute.setText("0"+Minute);
				}
				else {
					txtMinute.setText(Minute);
				}
				RingtonePath = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.RINGTONE_PATH_AL_COL));

				Log.d("NgaDV", Minute);
				Log.d("NgaDV", "tone : " + RingtonePath);
				
			}while (cursor.moveToNext());
			
			// rung máy
			
			vibrator=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(3000);
			
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
						Log.d("NgaDV", "mp = " + RingtonePath);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		super.onResume();
	}
	
	
}
