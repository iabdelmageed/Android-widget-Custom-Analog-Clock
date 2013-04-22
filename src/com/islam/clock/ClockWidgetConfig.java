package com.islam.clock;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

public class ClockWidgetConfig extends Activity {

	Button configOkButton, configOkButton2;
	public static int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	private WidgetService serviceBinder;
	Intent serviceIntent;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.config_theme);
		
		
		configOkButton = (Button) findViewById(R.id.okconfig);
		configOkButton.setOnClickListener(configOkButtonOnClickListener);

		configOkButton2 = (Button) findViewById(R.id.okconfig2);
		configOkButton2.setOnClickListener(configOkButtonOnClickListener);

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		// If they gave us an intent without the widget id, just bail.
		if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			finish();
		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {

			if (resultCode == RESULT_OK) {

//				if (!WidgetService.isServiceStartes) {
					if (serviceBinder != null)
						serviceBinder.unbindService(connection);
					else {
						serviceIntent = new Intent(this, WidgetService.class);
						serviceIntent.putExtra("WidgetId", mAppWidgetId);
						bindService(serviceIntent, connection,
								Context.BIND_AUTO_CREATE);

					}
//				}

				ClockWidgetProvider.DISPLAYWIDTH = getWindowManager()
						.getDefaultDisplay().getWidth();
				ClockWidgetProvider.DISPLAYHEIGHT = getWindowManager()
						.getDefaultDisplay().getHeight();

				final Context context = ClockWidgetConfig.this;

				AppWidgetManager appWidgetManager = AppWidgetManager
						.getInstance(context);
				ClockWidgetProvider.updateAppWidget(context, appWidgetManager,
						mAppWidgetId);

				Intent resultValue = new Intent();
				resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
						mAppWidgetId);
				setResult(RESULT_OK, resultValue);
				
//				setAlarm();
				
				finish();
			}
			if (resultCode == RESULT_CANCELED) {
				// Write your code on no result return
			}
		}
	}

	private Button.OnClickListener configOkButtonOnClickListener = new Button.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub

			switch (arg0.getId()) {
			case R.id.okconfig:
				ClockWidgetProvider.THEMEVALUE = "NEON";
				break;

			case R.id.okconfig2:
				ClockWidgetProvider.THEMEVALUE = "DEFAULT";
				break;

			}

			Intent SkinIntent = new Intent(ClockWidgetConfig.this,
					SkinConfiguration.class);
			startActivityForResult(SkinIntent, 1);

		}
	};

	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			serviceBinder = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			serviceBinder = ((WidgetService.MyBinder) service).getService();

			startService(serviceIntent);

		}
	};
}
