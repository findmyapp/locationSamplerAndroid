package com.accenture.RegisterRooms;

import java.util.List;

import android.net.wifi.ScanResult;

public class RegisterSampleForRoom {
	private String roomName;
	private int id;
	private int roomId;
	private Signal[] signalList;
	

	public RegisterSampleForRoom(String roomName, Signal[] signalArray) {
		id = -1;
		roomId = -1;
		this.roomName = roomName;
		signalList = signalArray;
	}

	public int getId() {
		return id;
	}

	public int getRoomId() {
		return roomId;
	}

	public String getRoomName() {
		return roomName;
	}
	
	public Signal[] getSignalList() {
		return signalList;
	}
	

}
