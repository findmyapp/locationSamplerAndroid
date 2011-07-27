package com.accenture.RegisterRooms;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Position handler that scans and retrieves position related wifi information
 * Sets wifi
 * 
 * @author audun.sorheim, cecilie haugstvedt
 * 
 */

public class WifiPositionHandler {

	public static final String CUSTOM_INTENT = "WifiFound";
	private Activity registerRoomsActivity;
	private List<ScanResult> scanList;
	private RestClient restClient;

	public WifiPositionHandler(RegisterRoomsActivity a) {
		this.registerRoomsActivity = a;
		scanList = null;
		restClient = new RestClient();
	}

	// Scans for Wifi BSSIDs / Signal Strength
	public void scanForSSID() {
		IntentFilter i = new IntentFilter();
		i.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		registerRoomsActivity.registerReceiver(new BroadcastReceiver() {
			public void onReceive(Context c, Intent i) {
				WifiManager w = (WifiManager) c
						.getSystemService(Context.WIFI_SERVICE);
				scanList = w.getScanResults();
			}
		}, i);
	}

	public List<ScanResult> getScanList() {
		return scanList;
	}

	/**
	 * Finds the current Room you are in
	 * 
	 * @return
	 */
	public com.accenture.RegisterRooms.Location getPositionFromServer() {
		com.accenture.RegisterRooms.Location location = null;
		if (scanList == null) {
			scanForSSID();
		}
		if (scanList != null) {
			String json = writeListToJSON(scanList);
			Log.e("WifiPositionHandler", "Json String: " + json);
			location = restClient.getWhereIAm(json);
		}

		return location;
	}

	/**
	 * Converts a list of scanResults to a json string
	 * 
	 * @param results
	 *            The list of ScanResult
	 * @return json-"string"
	 */
	public String writeListToJSON(List<ScanResult> results) {
		Gson gson = new Gson();
		if (scanList == null) {
			scanForSSID();
		}
		Signal[] protocolArray = new Signal[scanList.size()];
		int counter = 0;
		for (ScanResult result : scanList) {
			Signal protocol = new Signal(result.BSSID, result.level);
			protocolArray[counter] = protocol;
			counter++;
		}
		String json = gson.toJson(protocolArray);
		return json;
	}

	/**
	 * Converts a list of scanResults to a json string
	 * 
	 * @param results
	 *            The list of ScanResult
	 * @return json-"string", null if there are no elements in the list.
	 */
	public String registerSampleToJSON(String roomName) {
		Gson gson = new GsonBuilder().registerTypeAdapter(
				RegisterSampleForRoom.class, new SampleJsonSerializer())
				.create();
		String json = null;

		Signal[] signalArray = getSignalList();
		if (signalArray != null) {

			RegisterSampleForRoom sample = new RegisterSampleForRoom(roomName,
					signalArray);
			json = gson.toJson(sample);
		}
		return json;
	}

	/**
	 * Converts the scanList to an array of Signal objects.
	 * 
	 * @return an array of signals, null if the array is empty.
	 */
	public Signal[] getSignalList() {
		Signal[] signalList = null;
		if (scanList == null) {
			scanForSSID();
		} else {

			int listLength = scanList.size();
			signalList = new Signal[listLength];
			Signal signal = null;
			int counter = 0;
			for (ScanResult element : scanList) {
				signal = new Signal(element.BSSID, element.level);
				signalList[counter] = signal;
				counter++;
			}
		}
		return signalList;

	}
	
	/**
	 * Checks if wifi is enabled or not
	 * @return whether wifi is enabled or not
	 */
	public boolean isWifiEnabled() {
		WifiManager manager = (WifiManager) registerRoomsActivity
				.getSystemService(Context.WIFI_SERVICE);
		return manager.isWifiEnabled();
	}

	/**
	 * Sets wifi enabled or disabled based on input
	 * @param state wifi state enabled or disabled
	 */
	public void setWifiState(boolean state) {
		WifiManager manager = (WifiManager) registerRoomsActivity
				.getSystemService(Context.WIFI_SERVICE);
		manager.setWifiEnabled(state);
	}

}
