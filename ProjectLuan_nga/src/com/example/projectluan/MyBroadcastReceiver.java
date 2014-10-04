package com.example.projectluan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyBroadcastReceiver extends BroadcastReceiver {
	
	int id;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent != null){
			id = intent.getIntExtra("id", 0);
		}
		
		Intent i = new Intent(); 
		i.putExtra("dID", id);
		i.setClassName("com.example.projectluan", "com.example.projectluan.DialogEvent");
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
		
	}
	

}
