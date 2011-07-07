package com.accenture.RegisterRooms;

import java.util.List;

import android.net.wifi.ScanResult;

public class RegisterSampleForRoom {
	private String locationName;
	private int id;
	private int locationId;
	private Signal[] signalList;
	

	public RegisterSampleForRoom(String roomName, Signal[] signalArray) {
		id = -1;
		locationId = -1;
		this.locationName = roomName;
		signalList = signalArray;
	}

	public int getId() {
		return id;
	}

	public int getLocationId() {
		return locationId;
	}

	public String getLocationName() {
		return locationName;
	}
	
	public Signal[] getSignalList() {
		return signalList;
	}
	

}
