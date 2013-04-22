package com.islam.clock;

import com.islam.clock.R.drawable;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.PowerManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;

public class ClockWidgetProvider extends AppWidgetProvider {

	public static String THEMEVALUE = "DEFAULT";
	public static String SKINVALUE = "CYAN";

	public static int DISPLAYWIDTH;
	public static int DISPLAYHEIGHT;
	private static Canvas dial_Canvas;
	private static Drawable dial_drawable, hour_drawable, minute_drawable,
			second_drawable, background_drawable, markers_drawable,
			numbers_drawable;
	private static boolean mChanged = false;
	protected static Time mCalendar = new Time();
	private static float mMinutes;
	private static float mHour;
	private static float mSecond;
	private static RemoteViews views;
	private static Bitmap b;
	private static AppWidgetManager widgetManager;
	private static int widgetId;

	public static boolean hasBackground = false;
	public static boolean hasNumbers = false;
	public static boolean hasMarkers = false;
	public static boolean isWidgetCredated = false;

	private PowerManager.WakeLock wl;
	private PowerManager pm;
	private AlarmManager am;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		Log.e("onUpdate", "Inside onUpdate");

		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			updateAppWidget(context, appWidgetManager, appWidgetId);

		}

	}

	public void setAlarm(Context context) {
		Log.e("AlarmManager", "Inside setAlarm");
		wl.acquire();
		Intent i = new Intent(context, AlarmClass.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				3600000, pi);
		wl.release();
		// 3600000, 60000
	}

	public void cancelAlarm(Context context) {
		Intent i = new Intent(context, AlarmClass.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				3600000, pi);
		am.cancel(pi);
		wl.release();
	}

	public static void updateAppWidget(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId) {

		widgetManager = appWidgetManager;
		widgetId = appWidgetId;

		Intent intent = new Intent(context, ClockWidgetConfig.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId); // Identifies
																			// the
																			// particular
																			// widget...
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
		PendingIntent pendIntent = PendingIntent.getActivity(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		views = new RemoteViews(context.getPackageName(),
				R.layout.clockwidget_layout);

		background_drawable = context.getResources().getDrawable(
				drawable.back_org);
		markers_drawable = context.getResources()
				.getDrawable(drawable.mark_org);
		numbers_drawable = context.getResources()
				.getDrawable(drawable.numb_org);

		if (THEMEVALUE.equalsIgnoreCase("DEFAULT")) {
			dial_drawable = context.getResources().getDrawable(
					drawable.clock_dial);
			hour_drawable = context.getResources().getDrawable(
					drawable.clock_hand_hour);
			minute_drawable = context.getResources().getDrawable(
					drawable.clock_hand_minute);

			second_drawable = context.getResources().getDrawable(
					drawable.clock_hand_second);

		} else if (THEMEVALUE.equalsIgnoreCase("NEON")) {

			if (hasBackground)
				background_drawable = context.getResources().getDrawable(
						drawable.neon_back);

			if (hasNumbers)
				numbers_drawable = context.getResources().getDrawable(
						drawable.numb_org);

			if (hasMarkers)
				markers_drawable = context.getResources().getDrawable(
						drawable.neon_blue_markers);

			dial_drawable = context.getResources().getDrawable(
					drawable.neon_blue_face);
			hour_drawable = context.getResources().getDrawable(
					drawable.neon_blue_hand_hour);
			minute_drawable = context.getResources().getDrawable(
					drawable.neon_blue_hand_minute);

			second_drawable = context.getResources().getDrawable(
					drawable.neon_blue_hand_second);
		}

		DISPLAYWIDTH = dial_drawable.getIntrinsicWidth();
		DISPLAYHEIGHT = dial_drawable.getIntrinsicHeight();

		b = Bitmap.createBitmap(DISPLAYWIDTH, DISPLAYHEIGHT,
				Bitmap.Config.ARGB_8888);
		dial_Canvas = new Canvas(b);
		onTimeChanged();

		views.setOnClickPendingIntent(R.id.Widget, pendIntent);
		appWidgetManager.updateAppWidget(appWidgetId, views);

	}

	public void onReceive(Context context, Intent intent) {

		Log.e("onReceive", "Inside onReceive");

		if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(intent)) {
			AppWidgetManager amgr = AppWidgetManager.getInstance(context);
			int length = amgr.getAppWidgetIds(intent.getComponent()).length;
			if (length == 0)// WidgetService.StopService(context);
				context.stopService(new Intent(context, WidgetService.class));
			isWidgetCredated = false;
			WidgetService.timer.cancel();
			WidgetService.timer.purge();
		}

		pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AnalogClock");
		am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		setAlarm(context);

		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.clockwidget_layout);

		Intent AlarmClockIntent = new Intent(Intent.ACTION_MAIN).addCategory(
				Intent.CATEGORY_LAUNCHER).setComponent(
				new ComponentName("com.android.alarmclock",
						"com.android.alarmclock.AlarmClock"));
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				AlarmClockIntent, 0);
		views.setOnClickPendingIntent(R.id.Widget, pendingIntent);

		AppWidgetManager.getInstance(context).updateAppWidget(
				intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS),
				views);

	}

	protected static void onDrawClock() {

		boolean changed = mChanged;
		if (changed) {
			mChanged = false;
		}

		Log.e("Inside onDraw", "Inside onDraw");

		int availableWidth = DISPLAYWIDTH;
		int availableHeight = DISPLAYHEIGHT;

		int x = availableWidth / 2;
		int y = availableHeight / 2;

		final Drawable dial = dial_drawable;
		int w = dial.getIntrinsicWidth();
		int h = dial.getIntrinsicHeight();

		boolean scaled = false;

		// //////////////////////
		final Drawable background = background_drawable;

		if (availableWidth < w || availableHeight < h) {
			scaled = true;
			float scale = Math.min((float) availableWidth / (float) w,
					(float) availableHeight / (float) h);
			dial_Canvas.save();
			dial_Canvas.scale(scale, scale, x, y);
		}

		if (changed) {
			background.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y
					+ (h / 2));
		}
		background.draw(dial_Canvas);
		dial_Canvas.save();

		// ////////////////////////////
		final Drawable numbers = numbers_drawable;

		if (availableWidth < w || availableHeight < h) {
			scaled = true;
			float scale = Math.min((float) availableWidth / (float) w,
					(float) availableHeight / (float) h);
			dial_Canvas.save();
			dial_Canvas.scale(scale, scale, x, y);
		}

		if (changed) {
			numbers.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y
					+ (h / 2));
		}
		numbers.draw(dial_Canvas);
		dial_Canvas.save();

		// //////////////////////////////////////
		final Drawable markers = markers_drawable;

		if (availableWidth < w || availableHeight < h) {
			scaled = true;
			float scale = Math.min((float) availableWidth / (float) w,
					(float) availableHeight / (float) h);
			dial_Canvas.save();
			dial_Canvas.scale(scale, scale, x, y);
		}

		if (changed) {
			markers.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y
					+ (h / 2));
		}
		markers.draw(dial_Canvas);
		dial_Canvas.save();

		// ////////////////////////////////
		if (availableWidth < w || availableHeight < h) {
			scaled = true;
			float scale = Math.min((float) availableWidth / (float) w,
					(float) availableHeight / (float) h);
			dial_Canvas.save();
			dial_Canvas.scale(scale, scale, x, y);
		}

		if (changed) {
			dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
		}
		dial.draw(dial_Canvas);

		dial_Canvas.save();
		dial_Canvas.rotate(mHour / 12.0f * 360.0f, x, y);

		final Drawable hourHand = hour_drawable;
		if (changed) {
			w = hourHand.getIntrinsicWidth();
			h = hourHand.getIntrinsicHeight();
			hourHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y
					+ (h / 2));
		}
		hourHand.draw(dial_Canvas);
		dial_Canvas.restore();

		dial_Canvas.save();
		dial_Canvas.rotate(mMinutes / 60.0f * 360.0f, x, y);

		final Drawable minuteHand = minute_drawable;
		if (changed) {
			w = minuteHand.getIntrinsicWidth();
			h = minuteHand.getIntrinsicHeight();
			minuteHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y
					+ (h / 2));
		}
		minuteHand.draw(dial_Canvas);

		if (WidgetService.hasSecondsHand) {
			// /////////
			dial_Canvas.restore();

			dial_Canvas.save();
			// dial_Canvas.rotate(mSecond / 30.0f * 360.0f, x, y);

			dial_Canvas.rotate(mSecond / 60.0f * 60.0f * 360.0f, x, y);

			Log.e("second onTimeChanged", mSecond / 30.0f * 360.0f + "");

			final Drawable secondHand = second_drawable;
			if (changed) {
				w = secondHand.getIntrinsicWidth();
				h = secondHand.getIntrinsicHeight();
				secondHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y
						+ (h / 2));
			}
			secondHand.draw(dial_Canvas);
			// ////////////
		}

		dial_Canvas.restore();

		if (scaled) {
			dial_Canvas.restore();
		}

		isWidgetCredated = true;
	}

	protected static void onTimeChanged() {

		mCalendar.setToNow();
		int hour = mCalendar.hour;
		int minute = mCalendar.minute;
		int second = mCalendar.second;

		mSecond = second / 60.0f;
		mMinutes = minute + second / 60.0f;
		mHour = hour + mMinutes / 60.0f;
		mChanged = true;

		b.eraseColor(Color.TRANSPARENT);

		onDrawClock();

		views.setImageViewBitmap(R.id.dialimg, b);
		// widgetManager.updateAppWidget(widgetId, views);
	}

	protected static void broadcastTimeChanging() {
		mCalendar.setToNow();
		int hour = mCalendar.hour;
		int minute = mCalendar.minute;
		int second = mCalendar.second;

		mSecond = second / 60.0f;
		mMinutes = minute + second / 60.0f;
		mHour = hour + mMinutes / 60.0f;
		mChanged = true;
	}
}
