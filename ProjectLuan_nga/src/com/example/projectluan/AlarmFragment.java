package com.example.projectluan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.R.string;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class AlarmFragment extends Fragment {
	
	
	DatabaseController mDatabaseController;
	Cursor cursor;
	MyCursorAdapter mCursorAdapter;
	AlertDialog.Builder mBuilder ;
	ListView mListView;
	ImageView mImgAdd;
	TextView txtHour,txtMinute,txtFormat,txtLabel;
	Switch swStatus;
	ImageView imgDelete;
	static String ID;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		View root = inflater.inflate(R.layout.alarm_layout, container, false);
		
		mDatabaseController = new DatabaseController(getActivity());
		
		mListView = (ListView)root.findViewById(R.id.listView1);
		registerForContextMenu(mListView);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int i,
					long l) {
				HashMap<String, String> hashMap = (HashMap<String, String>) view.getTag();
				
				
				Intent intent = new Intent();
				intent.setClass(getActivity().getApplicationContext(), AlarmEdit.class);
				intent.putExtra("id", hashMap.get("id"));
				intent.putExtra("hour_alarm", hashMap.get("hour_alarm"));
				intent.putExtra("minute_alarm", hashMap.get("minute_alarm"));
				intent.putExtra("label", hashMap.get("label"));
				intent.putExtra("status", hashMap.get("status"));
				intent.putExtra("repeat", hashMap.get("repeat"));
				intent.putExtra("ringtone", hashMap.get("ringtone"));
				intent.putExtra("snooze", hashMap.get("snooze"));
				intent.putExtra("vibration", hashMap.get("vibration"));
				intent.putExtra("dismiss_method", hashMap.get("dismiss_method"));
				startActivity(intent);
			}
		});
		
		mImgAdd = (ImageView)root.findViewById(R.id.imgAdd);
		mImgAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity().getApplicationContext(), AlarmNew.class);
				startActivity(intent);
			}
		});
		
		return root;
		
	}
	@Override
	public void onResume() {
		cursor = mDatabaseController.query(DatabaseController.TABLE_NAME_AL, null, null, null, null, null, null);
		if(cursor != null && cursor.getCount() > 0){
			cursor.moveToFirst();
			do{
  				Log.d("NgaDV", ""+ cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.LABEL_AL_COL)));
			}while (cursor.moveToNext());
		}
		mCursorAdapter = new MyCursorAdapter(getActivity(), cursor);
		mListView.setAdapter(mCursorAdapter); 
		super.onResume();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	private class MyCursorAdapter extends CursorAdapter {
		
		Context mContext;
		LayoutInflater mLayoutInflater;
		
		public MyCursorAdapter(Context context, Cursor cursor) {
			super(context, cursor, true);
			mContext = context;
			mLayoutInflater = LayoutInflater.from(context);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {

			txtHour = (TextView) view.findViewById(R.id.txtHour);
			txtMinute = (TextView) view.findViewById(R.id.txtMinute);
			txtLabel = (TextView)view.findViewById(R.id.txtLabel);
			imgDelete = (ImageView) view.findViewById(R.id.imgDelete);
			swStatus  =  (Switch) view.findViewById(R.id.statusAlarm);
			//txtHour=------------------------------------------------------------
			if(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.HOUR_AL_COL))) < 10 ){
				txtHour.setText("0"+cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.HOUR_AL_COL)));
				Log.d("NgaDV", "0"+cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.HOUR_AL_COL)));
			}
			else {
				txtHour.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.HOUR_AL_COL)));
			}
			
			//txtMinute=------------------------------------------------------------
			if(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.MINUTE_AL_COL))) < 10 ){
				txtMinute.setText("0"+cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.MINUTE_AL_COL)));
			}
			else {
				txtMinute.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.MINUTE_AL_COL)));
				
			}
			
			txtLabel.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.LABEL_AL_COL)));
			txtLabel.setTag(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.ID_AL_COL)));
			if (cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.ENABLE_AL_COL)).equals("0")) {
				startAlarm(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.HOUR_AL_COL))),
						Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.MINUTE_AL_COL))),
						Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.ID_AL_COL))));
				updateEnableAlarm("1",
						cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.ID_AL_COL)));
				Log.d("NgaDV", cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.ENABLE_AL_COL)));
			}
			
			Log.d("NgaDV", "enable = " + cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.ENABLE_AL_COL)));
			if (cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.STATUS_AL_COL)).equals("1")) {
				swStatus.setChecked(true);
			}
			else {
				swStatus.setChecked(false);
			}
			
			final HashMap<String, String> hashMap =new HashMap<String, String>();
			
			hashMap.put("id", cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.ID_AL_COL)));
			hashMap.put("hour_alarm", cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.HOUR_AL_COL)));
			hashMap.put("minute_alarm", cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.MINUTE_AL_COL)));
			hashMap.put("label", cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.LABEL_AL_COL)));
			hashMap.put("status", cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.STATUS_AL_COL)));
			hashMap.put("repeat", cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.REPEAT_AL_COL)));
			hashMap.put("ringtone", cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.RINGTONE_AL_COL)));
			hashMap.put("snooze", cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.SNOOZE_AL_COL)));
			hashMap.put("dismiss_method", cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.DISMISS_METHOD_AL_COL)));
			swStatus.setTag(hashMap);
			swStatus.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					ID = hashMap.get("id");
					
					updateStatusAlarm(isChecked);
				}
			});
			imgDelete.setTag(hashMap);
			view.setTag(hashMap);
			
			
		}
		
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			final View view = mLayoutInflater.inflate(R.layout.alarm_listview, parent, false);
	
//			startAlarm(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.HOUR_AL_COL))),
//					Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.MINUTE_AL_COL))),
//					Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.ID_AL_COL))));
			
			imgDelete = (ImageView) view.findViewById(R.id.imgDelete);
			imgDelete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
				final HashMap<String, String> hashMap = (HashMap<String, String>) v.getTag();
				
				mBuilder = new AlertDialog.Builder(getActivity());
				mBuilder.setTitle("Xác nhận xóa").setMessage("Bạn có muốn xóa báo thức: " + hashMap.get("label")).setCancelable(false).setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						String[] id = new String[]{hashMap.get("id")};
						//delete database
						mDatabaseController.deleteAlarmByID(id);
						
						Toast.makeText(getActivity().getApplicationContext(), "Bạn đã xóa Báo thức :"  + hashMap.get("label"), Toast.LENGTH_LONG).show();
						getActivity().recreate();
						}
					})
					.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							
						}
					}); 
				AlertDialog alertDialog = mBuilder.create();
				alertDialog.show();
				}
				
			});
			return view;
		}
		
	}
	public void updateStatusAlarm ( Boolean status){
		ContentValues values = new ContentValues();
		
		values.put(DatabaseController.STATUS_AL_COL, status);
		DatabaseController.updateAlarm(DatabaseController.TABLE_NAME_AL, values, "ID_AL_COL = ?", new String[]{ID});
	}
	public void updateEnableAlarm ( String enable, String id){
		ContentValues values = new ContentValues();
		
		values.put(DatabaseController.ENABLE_AL_COL, enable);
		DatabaseController.updateAlarm(DatabaseController.TABLE_NAME_AL, values, "ID_AL_COL = ?", new String[]{id});
	}
private void startAlarm(int hour, int minute,int id) {
		
		Calendar calendar = Calendar.getInstance();
	        calendar.set(Calendar.HOUR_OF_DAY, hour);
	        calendar.set(Calendar.MINUTE, minute);
	        calendar.set(Calendar.SECOND,00);
	        
		Intent intent = new Intent(String . valueOf ( calendar . getTimeInMillis ()));
		intent.setClass(getActivity(), AlarmReceiver.class);
		intent.putExtra("id", id);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
		
		Toast.makeText(getActivity(), "Sửa báo thức thành công", Toast.LENGTH_SHORT).show();

	}
}	

