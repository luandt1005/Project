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

public class AlarmEdit extends PreferenceActivity{

	protected static final int TIME_DIALOG_ID = 111;

	ActionBar mActionBar;
	DatabaseController mDatabaseController;
	
	private EditTextPreference preLabel;
	private Preference preTime;
	private RingtonePreference preRingtone;
	private ListPreference preSnoozeTime;
	private ListPreference preDismissMethod;
	private MultiSelectListPreference preRepeat;
	private TimePicker timePicker;
	
	String RingtonePath;
	
	static  int hour,minute;
	
	static String mLabel,mRingtone,mSnooze,mDismissMethod,mRepeat;
	static int[] mTime;
	Boolean mVibration,mStatus;
	
	static String ID,Label,Hour,Minute,Repeat,Ringtone,Snooze,DismissMethod;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.alarm_edit);
		
		mActionBar = getActionBar();
		mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ca4d3b")));
		mDatabaseController = new DatabaseController(this);
		
		Intent intent = getIntent();
		ID = intent.getStringExtra("id");
		Label = intent.getStringExtra("label");
		Hour = intent.getStringExtra("hour_alarm");
		Minute = intent.getStringExtra("minute_alarm");
		Repeat = intent.getStringExtra("repeat");
		Ringtone = intent.getStringExtra("ringtone");
		Snooze = intent.getStringExtra("snooze");
		DismissMethod = intent.getStringExtra("dismiss_method");
		
		mStatus = true;
		
		// pre SetLabel----------------------------------------------------------------------------------//
		
		preLabel = (EditTextPreference)findPreference("preference_label");
		preLabel.setText(Label);
		preLabel.setSummary(Label);
		preLabel.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				
				preLabel.setSummary(newValue.toString());
				preLabel.setText(newValue.toString());
				return true;
				
			}
		});
		// pre SetTime ---------------------------------------------------------------------------------//
		
		preTime = (Preference) findPreference("preference_time");
		final Calendar c = Calendar.getInstance();
		
		hour = Integer.parseInt(Hour);
		minute = Integer.parseInt(Minute);
		
		preTime.setSummary(new StringBuilder().append(padding_str(hour)).append(":").append(padding_str(minute)));
		
		mTime = new int[] {hour,minute};
		preTime.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {

				showDialog(TIME_DIALOG_ID); 
				
				return true;
			}
		});
		// preRepeat --------------------------------------------------------------------//
		
		preRepeat = (MultiSelectListPreference) findPreference("preference_repeat");
		preRepeat.setSummary(Repeat);
		preRepeat.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {

		        String strRepeat = "";
		        CharSequence[] entries = preRepeat.getEntries();
		        String[] values = ((Set<String>)newValue).toArray(new String[] {});
		        for(int i = 0; i< values.length; i++){
		        	strRepeat += entries[Integer.parseInt(values[i])] + ", ";
		        }
		        preference.setSummary(strRepeat);
		        
		        
				return true;
			}
		});
		
		// pre Ringtone --------------------------------------------------------------//
		preRingtone = (RingtonePreference)findPreference("preference_ringtone");
		preRingtone.setSummary(Ringtone);
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
		preSnoozeTime = (ListPreference) findPreference("preference_snooze_time");
		preSnoozeTime.setSummary(Snooze);
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
				preDismissMethod = (ListPreference) findPreference("preference_dismiss_method");
				preDismissMethod.setSummary(DismissMethod);
				preDismissMethod.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					
					@Override
					public boolean onPreferenceChange(Preference preference, Object newValue) {
						
						preDismissMethod.setValue(newValue.toString());
			            preference.setSummary(preDismissMethod.getEntry());
			            mDismissMethod = preDismissMethod.getEntry().toString();
						
						return true;
					}
				});
		
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
			preTime.setSummary(new StringBuilder().append("     ").append(padding_str(hour)).append(":").append(padding_str(minute)));
			mTime = new int[]{hour,minute };
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
			updateAlarm(preLabel.getSummary().toString(), 
					mTime[0], 
					mTime[1], 
					preRepeat.getSummary().toString(),
					preRingtone.getSummary().toString(),
					RingtonePath,
					preSnoozeTime.getSummary().toString(),
					preDismissMethod.getSummary().toString(),
					"0",
					mStatus);
			break;
		default:
			break;
		}
		return false;
	}
	
	public void updateAlarm (String label, int hour, int minute, String repeat, 
			String ringtone, String ringtonePath, String snooze, String dismiss,String enable, Boolean status){
		ContentValues values = new ContentValues();
		values.put(DatabaseController.LABEL_AL_COL, label);
		values.put(DatabaseController.HOUR_AL_COL, hour);
		values.put(DatabaseController.MINUTE_AL_COL, minute);
		values.put(DatabaseController.REPEAT_AL_COL, repeat); 
		values.put(DatabaseController.RINGTONE_AL_COL, ringtone);
		values.put(DatabaseController.RINGTONE_PATH_AL_COL, ringtonePath);
		values.put(DatabaseController.SNOOZE_AL_COL, snooze);
		values.put(DatabaseController.DISMISS_METHOD_AL_COL, enable);
		values.put(DatabaseController.ENABLE_AL_COL, dismiss);
		values.put(DatabaseController.STATUS_AL_COL, status);
		DatabaseController.updateAlarm(DatabaseController.TABLE_NAME_AL, values, "ID_AL_COL = ?", new String[]{ID});
		finish();
	}
	
}