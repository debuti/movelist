/*
 * Copyright (C) 2009 Cyril Jaquier
 *
 * This file is part of NetCounter.
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

package tk.ganian.keepitinbounds.services;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import tk.ganian.keepitinbounds.R;
import tk.ganian.keepitinbounds.activities.KeepItInbounds;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.CallLog.Calls;
import android.telephony.TelephonyManager;

/**
 * {@link KeepItInboundsService} is responsible of updating all the data,
 * showing it in the main activity and popping the notifications as needed
 */
public class KeepItInboundsService extends Service {

	// Constants
	private static final long INTERVAL = 1000;
	
    // Global classes
	/** SharedPreferences. */
	public static SharedPreferences preferences;

	// Objects needed
	TelephonyManager myTelephonyManager;
	ContentResolver myContentResolver;
	private Timer timer = new Timer();

	// Application global parameters
	private static String currentImei = null;

	// Times
	private static int cycleStartDay = 25;
	private static Date cycleFirstDay = null;

	// Limits
	private static long minutesLimit = 700;
	private static long bytes3GLimit = 524288000; // 500MB
	private static long bytesWifiLimit = 0;
	private static long smsLimit = 0;

	// Comsuption
	private static long minutesConsumed = 0;
	private static long bytes3GConsumed = 0;
	private static long bytesWifiConsumed = 0;
	private static long smsConsumed = 0;

	@Override
	public void onCreate() {
		super.onCreate();
		Context context = getApplicationContext();
		myTelephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		myContentResolver = getContentResolver();
		
		preferences = getSharedPreferences("default", 0);

	}

	@Override
	public void onStart(Intent intent, int startId) {

		super.onStart(intent, startId);

		timer.scheduleAtFixedRate(new TimerTask() {

			public void run() {

				setCurrentImei(myTelephonyManager.getDeviceId());

				// Calc cycle key dates
				Calendar rightNow = Calendar.getInstance();

				if (rightNow.get(Calendar.DAY_OF_MONTH) >= cycleStartDay) {
					// We are on the same month
					rightNow.set(Calendar.DATE, cycleStartDay);
					cycleFirstDay = rightNow.getTime();
				} else {
					// We are on the next month
					if (rightNow.get(Calendar.MONTH) == 1) {
						// And the next month is in a new year, happy new year!
						rightNow.set(Calendar.DATE, cycleStartDay);
						rightNow.set(Calendar.MONTH, 12);
						rightNow.set(Calendar.YEAR,
								rightNow.get(Calendar.YEAR) - 1);
						cycleFirstDay = rightNow.getTime();
					} else {
						// :) Happy new month! (Listen to Theory of a Deadman -
						// Wait for me)
						rightNow.set(Calendar.DATE, cycleStartDay);
						rightNow.set(Calendar.MONTH, rightNow
								.get(Calendar.MONTH) - 1);
						cycleFirstDay = rightNow.getTime();
					}
				}

				// Calc calls
				minutesConsumed = computeCalls();

				// Notify if its necessary
				if (minutesConsumed / minutesLimit > 1) {
					panicNotification("");

				} else if (minutesConsumed / minutesLimit > 0.9) {
					severeNotification("");

				} else if (minutesConsumed / minutesLimit > 0.8) {
					shyNotification("");

				}

			}

		}, 0, INTERVAL);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (timer != null) {
			timer.cancel();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	// Private methods
	private long computeCalls() {

		long output = 0;

		String[] projection = new String[] { Calls.TYPE, Calls.DURATION,
				Calls.DATE, Calls.NUMBER };

		Cursor myCursor = myContentResolver.query(Calls.CONTENT_URI,
				projection, Calls.DATE
						+ " >= "
						+ String.valueOf(KeepItInboundsService.cycleFirstDay
								.getTime()), null, Calls.DATE + " DESC");

		if (myCursor.moveToFirst()) {
			// Extracted data
			int callType;
			int callDuration = 0;

			// Colums index
			final int idType = myCursor.getColumnIndex(Calls.TYPE);
			final int idDuration = myCursor.getColumnIndex(Calls.DURATION);
			final int idDate = myCursor.getColumnIndex(Calls.DATE);
			final int idNumber = myCursor.getColumnIndex(Calls.NUMBER);

			do {
				callType = myCursor.getInt(idType);

				if (callType == Calls.OUTGOING_TYPE) {

					callDuration = myCursor.getInt(idDuration);
					// TODO añadir excluded numbers
					// TODO añadir planes de consumo
					output += Math.ceil(callDuration / 60);

				}
			} while (myCursor.moveToNext());
		}

		return output;
	}

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
		Intent notificationIntent = new Intent(this, KeepItInbounds.class);
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
		Intent notificationIntent = new Intent(this, KeepItInbounds.class);
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
		Intent notificationIntent = new Intent(this, KeepItInbounds.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);

		saludo.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
		final int HELLO_ID = 3;

		nm.notify(HELLO_ID, saludo);

	}

	// Getters and setters
	public static String getCurrentImei() {
		return currentImei;
	}

	public static void setCurrentImei(String currentImei) {
		KeepItInboundsService.currentImei = currentImei;
	}

	public static int getCycleStartDay() {
		return cycleStartDay;
	}

	public static void setCycleStartDay(int cycleStartDay) {
		KeepItInboundsService.cycleStartDay = cycleStartDay;
	}

	public static Date getCycleFirstDay() {
		return cycleFirstDay;
	}

	public static void setCycleFirstDay(Date firstDay) {
		KeepItInboundsService.cycleFirstDay = firstDay;
	}

	public static long getMinutesLimit() {
		return minutesLimit;
	}

	public static void setMinutesLimit(long minutesLimit) {
		KeepItInboundsService.minutesLimit = minutesLimit;
	}

	public static long getBytes3GLimit() {
		return bytes3GLimit;
	}

	public static void setBytes3GLimit(long bytes3gLimit) {
		bytes3GLimit = bytes3gLimit;
	}

	public static long getBytesWifiLimit() {
		return bytesWifiLimit;
	}

	public static void setBytesWifiLimit(long bytesWifiLimit) {
		KeepItInboundsService.bytesWifiLimit = bytesWifiLimit;
	}

	public static long getSmsLimit() {
		return smsLimit;
	}

	public static void setSmsLimit(long smsLimit) {
		KeepItInboundsService.smsLimit = smsLimit;
	}

	public static long getMinutesConsumed() {
		return minutesConsumed;
	}

	public static void setMinutesConsumed(long minutesConsumed) {
		KeepItInboundsService.minutesConsumed = minutesConsumed;
	}

	public static long getBytes3GConsumed() {
		return bytes3GConsumed;
	}

	public static void setBytes3GConsumed(long bytes3gConsumed) {
		bytes3GConsumed = bytes3gConsumed;
	}

	public static long getBytesWifiConsumed() {
		return bytesWifiConsumed;
	}

	public static void setBytesWifiConsumed(long bytesWifiConsumed) {
		KeepItInboundsService.bytesWifiConsumed = bytesWifiConsumed;
	}

	public static long getSmsConsumed() {
		return smsConsumed;
	}

	public static void setSmsConsumed(long smsConsumed) {
		KeepItInboundsService.smsConsumed = smsConsumed;
	}

}

