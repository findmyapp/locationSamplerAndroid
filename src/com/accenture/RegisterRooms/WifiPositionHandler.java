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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Position handler that scans and retrieves position related wifi information
 * 
 * @author audun.sorheim, cecilie haugstvedt
 * 
 */

public class WifiPositionHandler  {

	public static final String CUSTOM_INTENT = "WifiFound";
	private Location currentLocation;
	private Activity registerRoomsActivity;
	private List<ScanResult> scanList;
	private boolean wifiFound;

	public WifiPositionHandler(RegisterRoomsActivity a) {
		this.registerRoomsActivity = a;
		scanList = null;
		wifiFound = false;
	}

	// Scans for Wifi BSSIDs / Signal Strength
	public void scanForSSID() {
		IntentFilter i = new IntentFilter();
		i.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		registerRoomsActivity.registerReceiver(new BroadcastReceiver() {
			public void onReceive(Context c, Intent i) {
				// Code to execute when SCAN_RESULTS_AVAILABLE_ACTION event
				// occurs
				WifiManager w = (WifiManager) c
						.getSystemService(Context.WIFI_SERVICE);
				scanList = w.getScanResults();
				wifiFound = true;
			
			}
		}, i);
		// Now you can call this and it should execute the broadcastReceiver's
		// onReceive()
		WifiManager wm = (WifiManager) registerRoomsActivity
				.getSystemService(Context.WIFI_SERVICE);
	}

	public List<ScanResult> getScanList() {
		return scanList;
	}

	public Location getLocation() {
		return currentLocation;
	}

	/**
	 * Finds the current Room you are in
	 * 
	 * @return
	 */
	public Room getPosition(String roomName) {
		Room dummy = new Room(3, "Dummy Rom");
//		RestClient restClient;

		if (scanList == null) {
			scanForSSID();
		}
		if (scanList != null) {
			String json = registerSampleToJSON(roomName);
//			restClient = new RestClient();
//			currentRoom = restClient.getRoom(json);
		}
		return dummy;
	}
	
	/**
	 * Converts a list of scanResults to a json string
	 * 
	 * @param results
	 *            The list of ScanResult
	 * @return json-"string", null if there are no elements in the list.
	 */
	public String registerSampleToJSON(String roomName) {
		Gson gson = new GsonBuilder().registerTypeAdapter(RegisterSampleForRoom.class, new SampleJsonSerializer()).create();
		String json = null;
		
		Signal[] signalArray = getSignalList();
		if (signalArray != null){		
			
			RegisterSampleForRoom sample = new RegisterSampleForRoom(roomName, signalArray);
			json = gson.toJson(sample);
			}	
		return json;
	}
	
	/**
	 * Converts the scanList to an array of Signal objects.
	 * @return an array of signals, null if the array is empty.
	 */
	public Signal[] getSignalList() {
		Signal [] signalList = null;
		if (scanList == null) {
			scanForSSID();
		}
		
		int listLength = scanList.size();
		signalList = new Signal[listLength];
		Signal signal = null;
		int counter = 0;
		for (ScanResult element : scanList) {
			signal = new Signal(element.BSSID, element.level);
			signalList[counter] = signal;
			counter++;
		
		}
		return signalList;

	}

	/**
	 * Returns true if wifi is found.
	 * @return
	 */
	public boolean getWifiStatus() {
		return wifiFound;
	}

}
