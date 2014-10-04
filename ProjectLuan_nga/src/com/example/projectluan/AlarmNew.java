package com.example.projectluan;


import java.util.Calendar;
import java.util.Set;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.RingtonePreference;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TimePicker;
import android.widget.Toast;

public class AlarmNew extends PreferenceActivity {
	
	protected static final int TIME_DIALOG_ID = 111;

	ActionBar mActionBar;
	DatabaseController mDatabaseController;
	Cursor cursor;
	
	private EditTextPreference preLabel;
	private Preference preTime;
	private RingtonePreference preRingtone;
	private ListPreference preSnoozeTime;
	private ListPreference preDismissMethod;
	private MultiSelectListPreference preRepeat;
	private TimePicker timePicker;
	
	String RingtonePath,ID;
	int hour,minute;
	
	String mLabel,mSnooze,mDismissMethod,mRepeat;
	int mHour;
	int mMinute;
	int IDMAX = 0;
	public int IDPut;
	Boolean mVibration,mStatus;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.alarm_new);
		
		mActionBar = getActionBar();
		mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ca4d3b")));
		mDatabaseController = new DatabaseController(this);
		
		

		// Set preference------------------------------------------------------------------
		preLabel = (EditTextPreference)findPreference("preference_label");
		preTime = (Preference) findPreference("preference_time");
		preRepeat = (MultiSelectListPreference) findPreference("preference_repeat");
		preRingtone = (RingtonePreference)findPreference("preference_ringtone");
		preSnoozeTime = (ListPreference) findPreference("preference_snooze_time");
		preDismissMethod = (ListPreference) findPreference("preference_dismiss_method");
		
		mStatus = true;
		
		// pre SetLabel----------------------------------------------------------------------------------//
		preLabel.setText(" ");
		preLabel.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				
				preLabel.setSummary(newValue.toString());
				return true;
			}
		});
		// pre SetTime ---------------------------------------------------------------------------------//
		
		
		final Calendar c = Calendar.getInstance();
		
		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);
		preTime.setSummary(new StringBuilder().append("").append(padding_str(hour)).append(":").append(padding_str(minute)));
		mHour = hour;
		mMinute = minute;
		preTime.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				showDialog(TIME_DIALOG_ID);
				
				return true;
			}
		});
		
		// preRepeat --------------------------------------------------------------------//
		preRepeat.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				
		        String strRepeat = "";
		        CharSequence[] entries = preRepeat.getEntries();
		        String[] values = ((Set<String>)newValue).toArray(new String[] {});
		        for(int i = 0; i< values.length; i++){
		        	strRepeat +=  entries[Integer.parseInt(values[i])] + ", ";
		        }
		        preRepeat.setSummary(strRepeat);
		        mRepeat = strRepeat;
				return true;
			}
		});		
		// pre Ringtone --------------------------------------------------------------//
		Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		RingtonePath = uri.toString();
		preRingtone.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
		
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				Ringtone ringtone =  RingtoneManager.getRingtone(getApplicationContext(), Uri.parse(newValue.toString()));
				RingtonePath = newValue.toString();
				preRingtone.setSummary(ringtone.getTitle(getApplicationContext()));
				return true;
			}
		});
		
		// pre Snooze -----------------------------------------------------------------//
		
		preSnoozeTime.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				
				preSnoozeTime.setValue(newValue.toString());
	            preference.setSummary(preSnoozeTime.getEntry());
	            mSnooze = preSnoozeTime.getEntry().toString();
				
				return true;
			}
		});
		
		// pre Dismiss -----------------------------------------------------------------//
				preDismissMethod.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					
					@Override
					public boolean onPreferenceChange(Preference preference, Object newValue) {
						
						preDismissMethod.setValue(newValue.toString());
						preDismissMethod.setSummary(preDismissMethod.getEntry());
			            mDismissMethod = preDismissMethod.getEntry().toString();
						
						return true;
					}
				});
				
				
		String stSQL = "SELECT MAX(" + DatabaseController.ID_AL_COL + ") AS "
				+ DatabaseController.IDMAX_AL_COL + " FROM "
				+ DatabaseController.TABLE_NAME_AL + ";";
		cursor = mDatabaseController.rawQuery(stSQL, null);
		
		if (cursor.moveToFirst()) {
			do {
				IDMAX = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseController.IDMAX_AL_COL));

			} while (cursor.moveToNext());
		}
		
	}
	private static String padding_str(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}
	
	
		
	private TimePickerDialog.OnTimeSetListener timePickerListener = new OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
			hour = selectedHour;
			minute = selectedMinute;
			
			preTime.setSummary(new StringBuilder().append("       ").append(padding_str(hour)).append(":").append(padding_str(minute)));
			mHour = hour;
			mMinute = minute;
		}
	};
	
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TIME_DIALOG_ID:
			return new TimePickerDialog(this, timePickerListener, hour, minute, true);
		
		}
		return null;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_actionbar, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.cancel:
			finish();
			break;

		case R.id.done:
			
			addAlarm(preLabel.getSummary().toString(),
					 mHour,
					 mMinute,
					 preRepeat.getSummary().toString(),
					 preRingtone.getSummary().toString(),
					 RingtonePath,
					 preSnoozeTime.getSummary().toString(),
					 preDismissMethod.getSummary().toString(),
					 "0",
					 mStatus);
//			startAlarm();
			finish();
			break;
		default:
			break;
		}
		return false;
	}
	
	private void addAlarm(String label, int hour, int minute, String repeat,
			String ringtone, String ringtonePath, String snooze, String dismiss,String enable, Boolean status) {
		ContentValues values = new ContentValues();
		values.put(DatabaseController.LABEL_AL_COL, label);
		values.put(DatabaseController.HOUR_AL_COL, hour);
		values.put(DatabaseController.MINUTE_AL_COL, minute);
		values.put(DatabaseController.REPEAT_AL_COL, repeat); 
		values.put(DatabaseController.RINGTONE_AL_COL, ringtone);
		values.put(DatabaseController.RINGTONE_PATH_AL_COL, ringtonePath);
		values.put(DatabaseController.SNOOZE_AL_COL, snooze);
		values.put(DatabaseController.DISMISS_METHOD_AL_COL, dismiss);
		values.put(DatabaseController.ENABLE_AL_COL, enable);
		values.put(DatabaseController.STATUS_AL_COL, status);
		DatabaseController.insert(DatabaseController.TABLE_NAME_AL, null, values);
		
	}
	
//private void startAlarm() {
//		
//		Calendar calendar = Calendar.getInstance();
//	        calendar.set(Calendar.HOUR_OF_DAY, mHour);
//	        calendar.set(Calendar.MINUTE, mMinute);
//	        calendar.set(Calendar.SECOND,00);
//	        
//	        Log.d("NgaDV","startAlarm "+ mHour + ":" + mMinute);
//	        IDPut = IDMAX + 1;
//			Intent intent = new Intent(String.valueOf(calendar.getTimeInMillis()));
//			intent.setClass(getApplicationContext(), AlarmReceiver.class);
//			intent.putExtra("id", IDPut);
//			Log.d("NgaDV", "IDPut : " + IDPut);
//			PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
//			AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//			alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
//
//		Toast.makeText(getApplicationContext(), "Thêm báo thức thành công", Toast.LENGTH_SHORT).show();
//	}
}