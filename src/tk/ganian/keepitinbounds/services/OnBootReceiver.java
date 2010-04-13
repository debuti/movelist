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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Broadcast receiver that get launched after boot up. It delays the initial
 * callback to the service in order to speed up boot.
 */
public class OnBootReceiver extends BroadcastReceiver {

	//TODO Replace the services system with a alarm-driven system
	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(context, KeepItInboundsService.class));
	}
}


//http://kfb-android.blogspot.com/2009/04/registering-for-timetick-after-reboot.html