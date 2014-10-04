package com.example.projectluan;


import java.util.HashMap;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MyEventFragment extends Fragment {
	
	
	DatabaseController mDbController;
	Cursor cursor;
	MyCursorAdapter mCursorAdapter;
	ListView mListView;
	ImageView imgNewEvent;
	TextView mScreenBegin;
	int did;
	
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.my_event_layout, container, false);
		imgNewEvent = (ImageView) root.findViewById(R.id.img_add_event);
		imgNewEvent.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), NewEvent.class);
				startActivity(intent);
				
			}
		});
		
		mListView = (ListView) root.findViewById(R.id.list_events);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				HashMap<String, String> getArray = (HashMap) view.getTag();
				Intent intent = new Intent();
				intent.setClass(getActivity(), Event.class);
				intent.putExtra("id", getArray.get("id").toString());
				intent.putExtra("nameEvent", getArray.get("nameEvent").toString());
				intent.putExtra("location", getArray.get("location").toString());
				intent.putExtra("date", getArray.get("date").toString());
				intent.putExtra("hour", getArray.get("hour").toString());
				intent.putExtra("note", getArray.get("note").toString());
				intent.putExtra("reminders", getArray.get("reminders").toString());
				startActivity(intent);
				
			}
			
		});
		
		mDbController = new DatabaseController(getActivity());
		
		return root;
		
	}
	
	@Override
	public void onResume() {
		cursor = mDbController.query(DatabaseController.TABLE_NAME, null, null, null, null, null, DatabaseController.ID_COL + " DESC");
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
			// TODO Auto-generated method stub
			TextView txtId = (TextView)view.findViewById(R.id.id);
			TextView txtName = (TextView)view.findViewById(R.id.nameEvent);
			TextView txtLocation = (TextView)view.findViewById(R.id.location);
			TextView txtDate = (TextView)view.findViewById(R.id.date);
			TextView txtHour = (TextView)view.findViewById(R.id.hour);
			
			txtId.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.ID_COL)));
			txtName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.NAME_EVENT_COL)));
			txtLocation.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.LOCATION_COL)));
			txtDate.setText(trimspace(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.DATE_COL))));
			txtHour.setText(trimspace(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.HOUR_COL))));
			
			HashMap<String, String> hashMap =new HashMap();
			hashMap.put("id", cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.ID_COL)));
			hashMap.put("nameEvent", cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.NAME_EVENT_COL)));
			hashMap.put("location", cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.LOCATION_COL)));
			hashMap.put("date", cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.DATE_COL)));
			hashMap.put("hour", cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.HOUR_COL)));
			hashMap.put("note", cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.NOTE_COL)));
			hashMap.put("reminders", cursor.getString(cursor.getColumnIndexOrThrow(DatabaseController.REMINDERS_COL)));
			
			view.setTag(hashMap);
		}

		public String trimspace(String str)
		{
		    str = str.replaceAll("\\s+", " ");
		    str = str.replaceAll("(^\\s+|\\s+$)", "");
		    return str;
		}
		
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = mLayoutInflater.inflate(R.layout.item_layout, parent, false);
			return view;
		}
		
	}

}
