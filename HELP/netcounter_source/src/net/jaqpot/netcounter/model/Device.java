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

package net.jaqpot.netcounter.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.jaqpot.netcounter.NetCounterApplication;
import net.jaqpot.netcounter.R;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

/**
 * 
 */
public abstract class Device {

	private static Device instance = null;

	private String[] mInterfaces = null;

	public synchronized static Device getDevice() {
		if (instance == null) {
			Log.i(Device.class.getName(), "Device: " + Build.DEVICE);
			// All the devices we know about.
			Device[] allDevices = { new DefaultDevice(), new GenericDevice(),
					new SamsungI7500Device(), new PulseDevice(), new DroidDevice() };
			// Iterates over all the devices and try to found the corresponding one.
			for (Device device : allDevices) {
				if (Arrays.asList(device.getNames()).contains(Build.DEVICE)) {
					instance = device;
					break;
				}
			}
			// Nothing found? Use the default device.
			if (instance == null) {
				instance = allDevices[0];
			}
		}
		return instance;
	}

	public abstract String[] getNames();

	public abstract String getCell();

	public abstract String getWiFi();

	public abstract String getBluetooth();

	public synchronized String[] getInterfaces() {
		if (mInterfaces == null) {
			List<String> tmp = new ArrayList<String>();
			if (getCell() != null) {
				tmp.add(getCell());
			}
			if (getWiFi() != null) {
				tmp.add(getWiFi());
			}
			if (getBluetooth() != null) {
				tmp.add(getBluetooth());
			}
			mInterfaces = tmp.toArray(new String[tmp.size()]);
		}
		return mInterfaces;
	}

	public String getPrettyName(String inter) {
		Resources r = NetCounterApplication.resources();
		if (getCell() != null && getCell().equals(inter)) {
			return r.getString(R.string.interfaceTypeCell);
		} else if (getWiFi() != null && getWiFi().equals(inter)) {
			return r.getString(R.string.interfaceTypeWifi);
		} else if (getBluetooth() != null && getBluetooth().equals(inter)) {
			return r.getString(R.string.interfaceTypeBluetooth);
		} else {
			return inter;
		}
	}

	public int getIcon(String inter) {
		if (getCell() != null && getCell().equals(inter)) {
			return R.drawable.cell;
		} else if (getBluetooth() != null && getBluetooth().equals(inter)) {
			return R.drawable.bluetooth;
		} else {
			return R.drawable.wifi;
		}
	}

}

/**
 * Generic device implementation corresponding to the emulator.
 */
class GenericDevice extends Device {

	@Override
	public String[] getNames() {
		return new String[] { "generic" };
	}

	@Override
	public String getBluetooth() {
		return null;
	}

	@Override
	public String getCell() {
		return null;
	}

	@Override
	public String getWiFi() {
		return "eth0";
	}

}

/**
 * Default device implementation corresponding to the HTC Dream and HTC Magic.
 */
class DefaultDevice extends Device {

	@Override
	public String[] getNames() {
		// TODO Get the device name of the HTC Magic.
		return new String[] { "dream" };
	}

	@Override
	public String getBluetooth() {
		return "bnep0";
	}

	@Override
	public String getCell() {
		return "rmnet0";
	}

	@Override
	public String getWiFi() {
		return "tiwlan0";
	}

}

/**
 * Device implementation for the Samsung I7500. Also works with the I5700 (Spica).
 */
class SamsungI7500Device extends Device {

	@Override
	public String[] getNames() {
		return new String[] { "GT-I7500", "spica" };
	}

	@Override
	public String getBluetooth() {
		return "bnep0";
	}

	@Override
	public String getCell() {
		return "pdp0";
	}

	@Override
	public String getWiFi() {
		return "eth0";
	}

}

/**
 * Device implementation for the T-Mobile Pulse (Huawei U8220). Also works for the Google Nexus One.
 */
class PulseDevice extends Device {

	@Override
	public String[] getNames() {
		return new String[] { "U8220", "passion" };
	}

	@Override
	public String getBluetooth() {
		return "bnep0";
	}

	@Override
	public String getCell() {
		return "rmnet0";
	}

	@Override
	public String getWiFi() {
		return "eth0";
	}

}

/**
 * Device implementation for the Motorola Droid.
 */
class DroidDevice extends Device {

	@Override
	public String[] getNames() {
		return new String[] { "sholes" };
	}

	@Override
	public String getBluetooth() {
		return "bnep0";
	}

	@Override
	public String getCell() {
		return "ppp0";
	}

	@Override
	public String getWiFi() {
		return "tiwlan0";
	}

}
