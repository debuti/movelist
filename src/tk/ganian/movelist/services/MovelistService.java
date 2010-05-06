/*
 * Copyright (C) 2009 Cyril Jaquier
 *
 * This file is part of Movelist.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see <http://www.gnu.org/licenses/>.
 */

package tk.ganian.movelist.services;

import tk.ganian.movelist.R;
import tk.ganian.movelist.activities.Movelist;
import tk.ganian.movelist.services.MovelistAlarm.State;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * {@link MovelistService} is the service responsible for regular update of the
 * model. We have to update it regularly because we get our data from the
 * underlying Linux system and Android does not provide a "shutdown event" at
 * the moment. So we poll regularly in order not to lose too many information.
 */
public class MovelistService extends WakefulService {

	private final BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			registerAlarm();
		}
	};

	private final Runnable mUpdateRunnable = new Runnable() {
		public void run() {
			try {
				Log.d(getClass().getName(), "Pasando por aqui");

			} finally {
				// Releases the local wake lock.
				releaseLocalLock();
			}
		}
	};

	// Global classes
	/** SharedPreferences. */
	public static SharedPreferences preferences;

	private int mPollingMode = -1;

	private boolean mWifiUpdate = false;

	private MovelistAlarm mAlarm;

	private WifiManager mWifiManager;

	@Override
	public void onCreate() {
		super.onCreate();

		mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		mAlarm = new MovelistAlarm(this, OnAlarmReceiver.class);

		IntentFilter f = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
		registerReceiver(mWifiReceiver, f);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		// Tweak to check if the preference changed. TODO Remove this hack.
		this.preferences = PreferenceManager.getDefaultSharedPreferences(this);

		Context context = getApplicationContext();
		registerAlarm();

		HandlerThread looper = new HandlerThread("NetCounter Handler");
		looper.start();
		Handler handler = new Handler(looper.getLooper());
		handler.removeCallbacks(mUpdateRunnable);
		handler.post(mUpdateRunnable);

		Log.d(getClass().getName(), "Service onStart -> ");

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.d(getClass().getName(), "Service onDestroy.");

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * Registers the alarm, changing the refresh rate as appropriate.
	 */
	private void registerAlarm() {

		if (preferences.getBoolean("wifiUpdate", false)) {
			mAlarm.registerAlarm(State.LOW);
		} else {
			mAlarm.registerAlarm(State.HIGH);
		}

	}

	// Private methods

	private void panicNotification(String text) {
		// Get the static global NotificationManager object.
		NotificationManager nm = (NotificationManager) this
				.getSystemService(NOTIFICATION_SERVICE);

		int icon = R.drawable.icon;
		CharSequence tickerText = "PANIC!";
		long when = System.currentTimeMillis();

		Notification saludo = new Notification(icon, tickerText, when);

		Context context = getApplicationContext();
		CharSequence contentTitle = "PANIC!";
		CharSequence contentText = text;
		Intent notificationIntent = new Intent(this, Movelist.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);

		saludo.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
		final int HELLO_ID = 1;

		nm.notify(HELLO_ID, saludo);

	}

	private void severeNotification(String text) {
		// Get the static global NotificationManager object.
		NotificationManager nm = (NotificationManager) this
				.getSystemService(NOTIFICATION_SERVICE);

		int icon = R.drawable.icon;
		CharSequence tickerText = "Severe";
		long when = System.currentTimeMillis();

		Notification saludo = new Notification(icon, tickerText, when);

		Context context = getApplicationContext();
		CharSequence contentTitle = "Severe";
		CharSequence contentText = text;
		Intent notificationIntent = new Intent(this, Movelist.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);

		saludo.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
		final int HELLO_ID = 2;

		nm.notify(HELLO_ID, saludo);

	}

	private void shyNotification(String text) {
		// Get the static global NotificationManager object.
		NotificationManager nm = (NotificationManager) this
				.getSystemService(NOTIFICATION_SERVICE);

		int icon = R.drawable.icon;
		CharSequence tickerText = "Shy";
		long when = System.currentTimeMillis();

		Notification saludo = new Notification(icon, tickerText, when);

		Context context = getApplicationContext();
		CharSequence contentTitle = "Shy";
		CharSequence contentText = text;
		Intent notificationIntent = new Intent(this, Movelist.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);

		saludo.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
		final int HELLO_ID = 3;

		nm.notify(HELLO_ID, saludo);

	}

}
