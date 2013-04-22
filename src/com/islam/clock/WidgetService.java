package com.islam.clock;

import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.text.format.Time;
import android.util.Log;

public class WidgetService extends Service {
	private final IBinder binder = new MyBinder();
	protected static Context contex;
	private final Handler mHandler = new Handler();
	int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	public static boolean isServiceStartes = false;
	private boolean isScreenOff = false;

	public static boolean hasSecondsHand = false;
	public static Timer timer;
	private TimerTask updateWidget;

	private PowerManager.WakeLock wl;
	private PowerManager pm;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return binder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		Log.e("Service Started", "Service started");

		isServiceStartes = true;

		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AnalogClock");

		if (!hasSecondsHand && timer != null) {
			timer.cancel();
			timer.purge();
		}

		if (hasSecondsHand) {
			statScheduler();
		}

		ClockWidgetProvider.onDrawClock();

		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt("WidgetId",
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		// If they gave us an intent without the widget id, just bail.
		if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			stopSelf();
		}

		IntentFilter filter = new IntentFilter();

		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);

		filter.addAction(Intent.ACTION_TIME_TICK);
		filter.addAction(Intent.ACTION_TIME_CHANGED);
		filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

		getBaseContext().registerReceiver(mIntentReceiver, filter, null,
				mHandler);

		return START_STICKY;
	}

	public class MyBinder extends Binder {
		WidgetService getService() {
			return WidgetService.this;
		}

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		stopSelf();
	}

	private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
				String tz = intent.getStringExtra("time-zone");
				ClockWidgetProvider.mCalendar = new Time(TimeZone.getTimeZone(
						tz).getID());
			}

			if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
				isScreenOff = false;

				if (hasSecondsHand) {
					statScheduler();
				}
			}

			if (isScreenOff)
				return;

			if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				isScreenOff = true;
				if (hasSecondsHand) {
					timer.cancel();
					timer.purge();
				}

				return;
			}

			if (hasSecondsHand)
				ClockWidgetProvider.broadcastTimeChanging();

			if (!hasSecondsHand) {
				if (ClockWidgetProvider.isWidgetCredated) {

					AppWidgetManager appWidgetManager = AppWidgetManager
							.getInstance(context);
					ClockWidgetProvider.updateAppWidget(context,
							appWidgetManager, mAppWidgetId);
				}
			}
		}
	};

	private void statScheduler() {

		timer = new Timer();
		updateWidget = new UpdateClockWidget();
		timer.scheduleAtFixedRate(updateWidget, 0, 1000);
	}

	class UpdateClockWidget extends TimerTask {

		public void run() {
			Log.e("Service ", "Inside Scheduler");
			if (ClockWidgetProvider.isWidgetCredated) {
				AppWidgetManager appWidgetManager = AppWidgetManager
						.getInstance(getBaseContext());
				ClockWidgetProvider.updateAppWidget(getBaseContext(),
						appWidgetManager, mAppWidgetId);
			}

		}
	}

}
