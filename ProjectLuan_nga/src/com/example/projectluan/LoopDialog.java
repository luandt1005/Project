package com.example.projectluan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;

public class LoopDialog extends Activity implements OnClickListener  {
	
	LinearLayout mLayoutW;
	LinearLayout mLayoutM;
	CheckBox mCheckTh2;
	
	LinearLayout mOk;
	LinearLayout mCancel;
	int Date;
	StringBuilder result = new StringBuilder();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_loop);
		
		Intent intent = getIntent();
		Date = intent.getIntExtra("date", -1);
		Log.d("LuanDT", "Date1 = " + Date);
		
		mOk = (LinearLayout) findViewById(R.id.ok);
		mCancel = (LinearLayout) findViewById(R.id.cancel);
		mOk.setOnClickListener(this);
		mCancel.setOnClickListener(this);
		
		mLayoutW = (LinearLayout) findViewById(R.id.weekLayout);
		mLayoutM = (LinearLayout) findViewById(R.id.monthLayout);
		
		mCheckTh2 = (CheckBox) findViewById(R.id.checkBoxTh2);
		if(mCheckTh2.isChecked()){
			result.append("Th 2");
		}
		
	}

	@Override
	public void onClick(View v) {
		Intent intent = getIntent();
		Date = intent.getIntExtra("date", -1);
		Log.d("LuanDT", "Date1 = " + Date);
		switch (v.getId()) {
		case R.id.ok:
			String information = result.toString();
			intent.putExtra("information", information);
			setResult(RESULT_OK, intent);
			break;

		case R.id.cancel:
			setResult(RESULT_CANCELED, intent);
			break;
		default:
			break;
		}
		
		finish();
	}

}
