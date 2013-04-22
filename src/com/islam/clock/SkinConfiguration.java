package com.islam.clock;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SkinConfiguration extends Activity {

	Button btncyan, btngreen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.config_skin);

		btncyan = (Button) findViewById(R.id.btncyan);
		btncyan.setOnClickListener(configOkButtonOnClickListenerSkin);

		btngreen = (Button) findViewById(R.id.btngreen);
		btngreen.setOnClickListener(configOkButtonOnClickListenerSkin);

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {

			if (resultCode == RESULT_OK) {
				Intent returnIntent = new Intent();
				returnIntent.putExtra("result", 1);
				setResult(RESULT_OK, returnIntent);
				finish();
			}
			if (resultCode == RESULT_CANCELED) {
				// Write your code on no result return
			}
		}
	}

	private Button.OnClickListener configOkButtonOnClickListenerSkin = new Button.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub

			switch (arg0.getId()) {
			case R.id.btncyan:
				ClockWidgetProvider.SKINVALUE = "CYAN";
				break;

			case R.id.btngreen:
				ClockWidgetProvider.SKINVALUE = "GREEN";
				break;

			}

			Log.e("Skin Activity", String.valueOf(arg0.getId()));

			Intent DisplayIntent = new Intent(getApplicationContext(),
					ClockDisplayOptions.class);
			startActivityForResult(DisplayIntent, 1);
		}
	};
}
