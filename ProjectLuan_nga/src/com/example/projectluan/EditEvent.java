package com.example.projectluan;

import java.util.Calendar;
import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.RingtonePreference;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class EditEvent extends PreferenceActivity {
	
	ActionBar mActionBar;
	DatabaseController mDbController;
	private static String LOOP_KEY = "loop";
	private static String DATE_KEY = "date";
	private static String HOUR_KEY = "hour";
	private static String REMINDERS_KEY = "reminders";
	private static String SILENCE_AFTER_KEY = "silence_after";
	private static String RINGTONE_KEY = "ringtone";
	private static String EVENT_NAME_KEY = "event_name";
	private static String LOCATION_KEY = "location";
	private static String NOTE_KEY = "note";
	
	private int no;
	private int date;
	private int months;
	private int years_now;
	private int hour;
	private int minute;
	
	static final int DATE_DIALOG_ID = 111;
	static final int TIME_DIALOG_ID = 999;
	
	EditTextPreference mEdtEventName;
	EditTextPreference mEdtLocation;
	EditTextPreference mNote;
	Preference mLoop;
	ListPreference mSilenceAfter;
	ListPreference mReminders;
	RingtonePreference mRingtonePreference;
	Preference mDate;
	Preference mHour;
	String RingtonePath;
	
	String ID;
	int id1;
	String NameEvent;
	String Location;
	String Date;
	String Hour;
	String Note;
	String Loop;
	String Reminders;
	String Ringtone;
	String SilenceAfter;
	String value;
	int timeReminder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.edit_event);
		
		mActionBar = getActionBar();
		mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ca4d3b")));
		mDbController = new DatabaseController(this);
		
		Intent intent = getIntent();
		ID = intent.getStringExtra("id");
		id1 = Integer.parseInt(ID);
		NameEvent = intent.getStringExtra("nameEvent");
		Location = intent.getStringExtra("location");
		Date = intent.getStringExtra("date");
		Hour = intent.getStringExtra("hour");
		Note = intent.getStringExtra("note");
		Loop = intent.getStringExtra("loop");
		Reminders = intent.getStringExtra("reminders");
		Ringtone = intent.getStringExtra("ringtone");
		SilenceAfter = intent.getStringExtra("silenceAfter");
		RingtonePath = intent.getStringExtra("ringtonePath");
		
		//----------------Event name---------------------//
		
		mEdtEventName = (EditTextPreference) findPreference(EVENT_NAME_KEY);
		mEdtEventName.setSummary(NameEvent);
		mEdtEventName.setText(NameEvent);
		mEdtEventName.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				mEdtEventName.setSummary(newValue.toString());
				mEdtEventName.setText(newValue.toString());
				return false;
			}
		});

		//----------------Location---------------------//
		
		mEdtLocation = (EditTextPreference) findPreference(LOCATION_KEY);
		mEdtLocation.setSummary(Location);
		mEdtLocation.setText(Location);
		mEdtLocation.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				mEdtLocation.setSummary(newValue.toString());
				mEdtLocation.setText(newValue.toString());
				return false;
			}
		});		
		
		//----------------Date---------------------//
		
		mDate = (Preference) findPreference(DATE_KEY);
		final Calendar c = Calendar.getInstance();
			no = c.get(Calendar.DAY_OF_WEEK);
			date = c.get(Calendar.DATE);
			months = c.get(Calendar.MONTH);
			years_now = c.get(Calendar.YEAR);
			hour = c.get(Calendar.HOUR_OF_DAY);
			minute = c.get(Calendar.MINUTE);
	
		mDate.setTitle(Date);
		mDate.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				showDialog(DATE_DIALOG_ID);
				return false;
			}
		});
		
		//----------------Hour---------------------//
		
		mHour = (Preference) findPreference(HOUR_KEY);
		mHour.setTitle(Hour);
		mHour.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				showDialog(TIME_DIALOG_ID);
				return false;
			}
		});
		
		//----------------Note---------------------//
		
		mNote = (EditTextPreference) findPreference(NOTE_KEY);
		mNote.setSummary(Note);
		mNote.setText(Note);
		mNote.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				mNote.setSummary(newValue.toString());
				mNote.setText(newValue.toString());
				return false;
			}
		});		
		
		//----------------Loop---------------------//
		
		mLoop = (Preference) findPreference(LOOP_KEY);
		mLoop.setSummary(Loop);
		mLoop.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {

				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), LoopDialog.class);
				intent.putExtra("date", date);
				Log.d("LuanDT", "Date = " + date);
				startActivityForResult(intent, 1);

				return true;
			}
		});
		
		//----------------Reminders---------------------//
		
		mReminders = (ListPreference) findPreference(REMINDERS_KEY);
		mReminders.setSummary(Reminders);
		mReminders.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {

				mReminders.setSummary(newValue.toString());
				TimeReminders();
				return true;
			}
		});
		
		//----------------Ringtone---------------------//
		
		mRingtonePreference = (RingtonePreference) findPreference(RINGTONE_KEY);
		mRingtonePreference.setSummary(Ringtone);
		Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		RingtonePath = uri.toString();
		
		mRingtonePreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), Uri.parse(newValue.toString()));
				mRingtonePreference.setSummary(ringtone.getTitle(getApplicationContext()));
				RingtonePath = newValue.toString();
				return true;
			}
		});
		
		///----------------SilenceAfter-------------///
		
		mSilenceAfter = (ListPreference) findPreference(SILENCE_AFTER_KEY);
		mSilenceAfter.setSummary(SilenceAfter);
		mSilenceAfter.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					
					@Override
					public boolean onPreferenceChange(Preference preference, Object newValue) {
						mSilenceAfter.setSummary(newValue.toString());
						return true;
					}
				});		
		
	}
	
	//----------------Dialog---------------------//
	
	private static String padding_str(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}
	
	private DatePickerDialog.OnDateSetListener datePickerListener = new OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			date = dayOfMonth;
			months = monthOfYear;
			years_now = year;
			
			Calendar calendar = Calendar.getInstance();
			calendar.set(year, monthOfYear, dayOfMonth);
			int no = calendar.get(Calendar.DAY_OF_WEEK);
			
			mDate.setTitle(new StringBuilder().append("       Th " + no + " ")
				.append(padding_str(date)).append("-").append(padding_str(months + 1)).append("-").append(padding_str(years_now)));
		}
	};

	private TimePickerDialog.OnTimeSetListener timePickerListener = new OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
			hour = selectedHour;
			minute = selectedMinute;
			
			mHour.setTitle(new StringBuilder().append("       ").append(padding_str(hour)).append(":").append(padding_str(minute)));
			
		}
	};
	
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TIME_DIALOG_ID:
			return new TimePickerDialog(this, timePickerListener, hour, minute, false);
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, datePickerListener, years_now, months, date);
		}
		return null;
	}
	
	//----------------OptionsMenu---------------------//
	
	public void TimeReminders(){
		value = mReminders.getSummary().toString();
		if(value.contains("1"))
			timeReminder = 1;
		if(value.contains("5"))
			timeReminder = 5;
		if(value.contains("15"))
			timeReminder = 15;
		if(value.contains("30"))
			timeReminder = 30;
		if(value.contains("60"))
			timeReminder = 60;
		if(value.contains("2 giờ"))
			timeReminder = 120;
		if(value.contains("24 giờ"))
			timeReminder = 1440;
		if(value.contains("2 ngày"))
			timeReminder = 2880;
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_new_event, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.cancelAdd:
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), MainActivity.class);
			startActivity(intent);
			break;

		case R.id.done:
			updateEvent(mEdtEventName.getText().toString(), mEdtLocation.getText().toString(), 
					mDate.getTitle().toString(),
					mHour.getTitle().toString(), 
					mNote.getText().toString(), 
					mLoop.getSummary().toString(), 
					mReminders.getSummary().toString(),
					RingtonePath.toString(), 
					mRingtonePreference.getSummary().toString(), 
					mSilenceAfter.getSummary().toString());
			
			startEvent();
			
			break;
		default:
			break;
		}
		return false;
	}
	
	public void updateEvent (String eventName, String location, String date, String hour, String note, String loop, String reminders, 
			String ringtone_path, String ringtone, String silenceAfter){
		ContentValues values = new ContentValues();
		values.put(DatabaseController.NAME_EVENT_COL, eventName);
		values.put(DatabaseController.LOCATION_COL, location);
		values.put(DatabaseController.DATE_COL, date);
		values.put(DatabaseController.HOUR_COL, hour);
		values.put(DatabaseController.NOTE_COL, note);
		values.put(DatabaseController.LOOP_COL, loop);
		values.put(DatabaseController.REMINDERS_COL, reminders);
		values.put(DatabaseController.RINGTONE_PATH_COL, ringtone_path);
		values.put(DatabaseController.RINGTONE_COL, ringtone);
		values.put(DatabaseController.SILENCE_AFTER_COL, silenceAfter);
		mDbController.update (DatabaseController.TABLE_NAME, values, "ID_COL = ?", new String[]{ID});
		finish();
		
	}
	
	private void startEvent() {
		
		Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.MONTH, months);
	        calendar.set(Calendar.YEAR, years_now);
	        calendar.set(Calendar.DAY_OF_MONTH, date);
	 
	        calendar.set(Calendar.HOUR_OF_DAY, hour);
	        calendar.set(Calendar.MINUTE, minute);
	        calendar.set(Calendar.SECOND, 00);
	        
		Intent intent = new Intent(String . valueOf ( calendar . getTimeInMillis ()));
		intent.setClass(getApplicationContext(), MyBroadcastReceiver.class);
		intent.putExtra("id", id1);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int reminders = timeReminder * 60 * 1000;
		alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis() - reminders, AlarmManager.INTERVAL_DAY, pendingIntent);
		
		Toast.makeText(getApplicationContext(), "Sửa sự kiện thành công", Toast.LENGTH_SHORT).show();
	}
	
}
