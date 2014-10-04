package com.example.projectluan;

import android.R.integer;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Event extends Activity {
	
	ActionBar mActionBar;
	TextView mIDTxt;
	TextView mNameEventTxt;
	TextView mLocationTxt;
	TextView mDateTxt;
	TextView mHourTxt;
	TextView mNoteTxt;
	TextView mRemindersTxt;
	ListView mListView;
	
	DatabaseController mDbController;
	Cursor cursor;
	String ID;
	int ID1;
	String NameEvent;
	String Location;
	String Date;
	String Date1;
	String Hour;
	String Note;
	String Loop;
	String Reminders;
	String Ringtone;
	String SilenceAfter;
	String RingtonePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event);
		
		mActionBar = getActionBar();
		mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ca4d3b")));
		Intent intent = getIntent();
		ID1 = intent.getIntExtra("did", 0);
		ID = intent.getStringExtra("id");
		mDbController = new DatabaseController(this);
		
	}
	
	@Override
	protected void onResume() {
		String stSQL = "SELECT * FROM " + DatabaseController.TABLE_NAME + " WHERE " + DatabaseController.ID_COL + "=" + ID + " or " + DatabaseController.ID_COL  + "=" + ID1 + ";";
		cursor = mDbController.rawQuery(stSQL, null);
		if(cursor.moveToFirst()){
			do{
				mNameEventTxt = (TextView) findViewById(R.id.txtNameEvent);
				NameEvent = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.NAME_EVENT_COL));
				mNameEventTxt.setText(NameEvent);
				mLocationTxt = (TextView)findViewById(R.id.txtLocation);
				Location = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.LOCATION_COL));
				mLocationTxt.setText(Location);
				mDateTxt = (TextView)findViewById(R.id.txtDate);
				Date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.DATE_COL));
				Date1 = trimspace(Date);
				mDateTxt.setText(Date1);
				mHourTxt = (TextView)findViewById(R.id.txtHour);
				Hour = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.HOUR_COL));
				mHourTxt.setText(Hour);
				mNoteTxt = (TextView)findViewById(R.id.txtNote);
				Note = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.NOTE_COL));
				mNoteTxt.setText(Note);
				mRemindersTxt = (TextView)findViewById(R.id.txtR);
				Reminders = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.REMINDERS_COL));
				mRemindersTxt.setText(Reminders);
				Ringtone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.RINGTONE_COL));
				SilenceAfter = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.SILENCE_AFTER_COL));
				RingtonePath = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.RINGTONE_PATH_COL));
				Loop = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.LOOP_COL));
				
			}while (cursor.moveToNext());
		}
		super.onResume();
	}
	
	public String trimspace(String str)
	{
	    str = str.replaceAll("\\s+", " ");
	    str = str.replaceAll("(^\\s+|\\s+$)", "");
	    return str;
	}
	
	private void deleteEvent() {
		String[] id = new String[]{ID};
		mDbController.deleteByID(id);
		finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_event, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.delete:
			AlertDialog.Builder builder = new AlertDialog.Builder(Event.this);
			builder.setTitle("Xác nhận xóa")
			.setMessage("Bạn có muốn xóa sự kiện: " + " \" " + NameEvent + " \" ")
			.setCancelable(false)
			.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					deleteEvent();
					Toast.makeText(getApplicationContext(), "Bạn đã xóa sự kiện : " + NameEvent, Toast.LENGTH_LONG).show();
					
				}
			})
			.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					
				}
			}); 
			AlertDialog alertDialog = builder.create();
			alertDialog.show();
			break;

		case R.id.edit:
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), EditEvent.class);
			intent.putExtra("id", ID);
			intent.putExtra("nameEvent", NameEvent);
			intent.putExtra("location", Location);
			intent.putExtra("date", Date);
			intent.putExtra("hour", Hour);
			intent.putExtra("note", Note);
			intent.putExtra("loop", Loop);
			intent.putExtra("reminders", Reminders);
			intent.putExtra("ringtone", Ringtone);
			intent.putExtra("silenceAfter", SilenceAfter);
			intent.putExtra("ringtonePath", RingtonePath);
			startActivity(intent);
			break;
		default:
			break;
		}
		return false;
	}
	
}
