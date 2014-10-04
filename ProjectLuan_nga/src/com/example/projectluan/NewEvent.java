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
import android.database.Cursor;
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
import android.preference.PreferenceActivity;
import android.preference.RingtonePreference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class NewEvent extends PreferenceActivity {
	
	ActionBar mActionBar;
	DatabaseController mDbController;
	Cursor cursor;
	private static String LOOP_KEY = "loop";
	private static String DATE_KEY = "date";
	private static String HOUR_KEY = "hour";
	private static String REMINDERS_KEY = "reminders";
	private static String SILENCE_AFTER_KEY = "silence_after";
	private static String RINGTONE_NEW_KEY = "ringtone_new";
	private static String EVENT_NAME_KEY = "event_name";
	private static String LOCATION_KEY = "location";
	private static String NOTE_KEY = "note";
	
	private int no;
	private int date;
	private int months;
	private int years_now;
	private int hour;
	private int minute;
	
	private EditText editText;
	
	static final int DATE_DIALOG_ID = 111;
	static final int TIME_DIALOG_ID = 999;
	
	EditTextPreference mEdtEventName;
	EditTextPreference mEdtLocation;
	EditTextPreference mNote;
	Preference mLoop;
	ListPreference mSilenceAfter;
	ListPreference mReminders;
	RingtonePreference mRingtone;
	Preference mDate;
	Preference mHour;
	String RingtonePath;
	
	String ID;
	String NameEvent;
	String Location;
	String Date;
	String Hour;
	String Note;
	String Reminders;
	String Ringtone;
	String SilenceAfter;
	String value;
	int IDMAX;
	int IDPut;
	int timeReminder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.new_event);
		
		mActionBar = getActionBar();
		mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ca4d3b")));
		mDbController = new DatabaseController(this);

		String stSQL = "SELECT MAX(" + DatabaseController.ID_COL + ") AS "
				+ DatabaseController.IDMAX_COL + " FROM "
				+ DatabaseController.TABLE_NAME + ";";
		cursor = mDbController.rawQuery(stSQL, null);
		
		if (cursor.moveToFirst()) {
			do {
				IDMAX = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseController.IDMAX_COL));

			} while (cursor.moveToNext());
		}

		//----------------Event name---------------------//

		mEdtEventName = (EditTextPreference) findPreference(EVENT_NAME_KEY);
		mEdtEventName.setText("");
		mEdtEventName.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				mEdtEventName.setSummary(newValue.toString());
				return false;
			}
		});

		//----------------Location---------------------//
		
		mEdtLocation = (EditTextPreference) findPreference(LOCATION_KEY);
		mEdtLocation.setText("");
		mEdtLocation.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				mEdtLocation.setSummary(newValue.toString());
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
	
		mDate.setTitle(new StringBuilder().append("       Th ")
				.append(padding_str(no)).append(" ").append(padding_str(date))
				.append("-").append(padding_str(months + 1)).append("-")
				.append(padding_str(years_now)));

		mDate.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				showDialog(DATE_DIALOG_ID);
				return false;
			}
		});

		
		//----------------Hour---------------------//
		
		mHour = (Preference) findPreference(HOUR_KEY);
		mHour.setTitle(new StringBuilder().append("       ")
				.append(padding_str(hour)).append(":")
				.append(padding_str(minute)));
		mHour.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				showDialog(TIME_DIALOG_ID);
				return false;
			}
		});

		// /----------------Note-------------///

		mNote = (EditTextPreference) findPreference(NOTE_KEY);
		mNote.setText("");
		mNote.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				mNote.setSummary(newValue.toString());
				return true;
			}
		});

		//----------------Loop---------------------//
		
		mLoop = (Preference) findPreference(LOOP_KEY);
		mLoop.setSummary("Sự kiện diễn ra một lần");
		mLoop.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {

				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), LoopDialog.class);
				intent.putExtra("date", date);
				startActivityForResult(intent, 1);

				return true;
			}
		});
		
		
		//----------------Reminders---------------------//
		
		mReminders = (ListPreference) findPreference(REMINDERS_KEY);
		mReminders.setSummary("0 phút trước sự kiện");
		mReminders.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {

				mReminders.setSummary(newValue.toString());
				TimeReminders();
				return true;
			}
		});
		
		//----------------Ringtone---------------------//
		
		mRingtone = (RingtonePreference) findPreference(RINGTONE_NEW_KEY);
		mRingtone.setSummary("Nhạc chuông mặc định");
		Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		RingtonePath = uri.toString();
		
		mRingtone.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
		
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), Uri.parse(newValue.toString()));
				mRingtone.setSummary(ringtone.getTitle(getApplicationContext()));
				RingtonePath = newValue.toString();
				return true;
				
			}
		});
		
		///----------------SilenceAfter-------------///
		
		mSilenceAfter = (ListPreference) findPreference(SILENCE_AFTER_KEY);
		mSilenceAfter.setSummary("20 giây");
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
	
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (resultCode == RESULT_OK
//				&& data.getExtras().containsKey("information")) {
//			String information = data.getExtras().getString("information");
//			mLoop.setSummary(information);
//		}
//	}

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
			addEvent(mEdtEventName.getSummary().toString(), mEdtLocation.getSummary().toString(), 
					mDate.getTitle().toString(), 
					mHour.getTitle().toString(), 
					mNote.getSummary().toString(),
					mLoop.getSummary().toString(),
					mReminders.getSummary().toString(), 
					RingtonePath.toString(), 
					mRingtone.getSummary().toString(), 
					mSilenceAfter.getSummary().toString());
			startEvent();

			
			break;
		default:
			break;
		}
		return false;
	}
	
	private void addEvent(String eventName, String location, String date,
			String hour, String note, String loop, String reminders, String ringtone_path, 
			String ringtone, String silenceAfter) {
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
		mDbController.insert(DatabaseController.TABLE_NAME, null, values);
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

		IDPut = IDMAX + 1;
		Intent intent = new Intent(String.valueOf(calendar.getTimeInMillis()));
		intent.setClass(getApplicationContext(), MyBroadcastReceiver.class);
		intent.putExtra("id", IDPut);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int reminders = timeReminder * 60 * 1000;
		alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis() - reminders, AlarmManager.INTERVAL_DAY, pendingIntent);

		Toast.makeText(getApplicationContext(), "Tạo sự kiện thành công",Toast.LENGTH_SHORT).show();
	}

	
}