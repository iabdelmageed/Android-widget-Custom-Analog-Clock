package com.islam.clock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmClass extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		// TODO Auto-generated method stub

		Log.e("BroadcastReceiver", "Inside onReceive");
		if (ClockWidgetProvider.isWidgetCredated) {
		Intent serviceIntent = new Intent(context, WidgetService.class);
		serviceIntent.putExtra("WidgetId", ClockWidgetConfig.mAppWidgetId);
		context.startService(serviceIntent);
		}
	}

}
