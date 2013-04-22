package com.islam.clock;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class ClockDisplayOptions extends Activity {

	CheckBox chkbg, chknmbrs, chkmarkrs, chkseconds;

	Button btnFinish;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_options);
		setResult(RESULT_CANCELED);
		WidgetService.contex = getBaseContext();

		btnFinish = (Button) findViewById(R.id.btnFinish);
		btnFinish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent returnIntent = new Intent();
				returnIntent.putExtra("result", 1);
				setResult(RESULT_OK, returnIntent);
				finish();
			}
		});

		chkbg = (CheckBox) findViewById(R.id.chxbg);
		chkbg.setOnCheckedChangeListener(myOptionOnCheckedChangeListener);

		chknmbrs = (CheckBox) findViewById(R.id.chxnmbrs);
		chknmbrs.setOnCheckedChangeListener(myOptionOnCheckedChangeListener);

		chkmarkrs = (CheckBox) findViewById(R.id.chxmrkrs);
		chkmarkrs.setOnCheckedChangeListener(myOptionOnCheckedChangeListener);
		
		chkseconds = (CheckBox) findViewById(R.id.chxsecond);
		chkseconds.setOnCheckedChangeListener(myOptionOnCheckedChangeListener);

		if (ClockWidgetProvider.hasBackground)
			chkbg.setChecked(true);
		else
			chkbg.setChecked(false);

		if (ClockWidgetProvider.hasNumbers)
			chknmbrs.setChecked(true);
		else
			chknmbrs.setChecked(false);

		if (ClockWidgetProvider.hasMarkers)
			chkmarkrs.setChecked(true);
		else
			chkmarkrs.setChecked(false);
		
		if (WidgetService.hasSecondsHand)
			chkseconds.setChecked(true);
		else
			chkseconds.setChecked(false);

	}

	CheckBox.OnCheckedChangeListener myOptionOnCheckedChangeListener = new CheckBox.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			switch (buttonView.getId()) {
			case R.id.chxbg:
				ClockWidgetProvider.hasBackground = isChecked;
				break;

			case R.id.chxnmbrs:
				ClockWidgetProvider.hasNumbers = isChecked;
				break;

			case R.id.chxmrkrs:
				ClockWidgetProvider.hasMarkers = isChecked;
				break;
				
			case R.id.chxsecond:
				WidgetService.hasSecondsHand = isChecked;
				break;
			}

		}

	};
}
