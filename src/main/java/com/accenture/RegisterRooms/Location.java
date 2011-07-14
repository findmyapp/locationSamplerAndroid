package com.accenture.RegisterRooms;

/**
 * @author Cecilie Haugstvedt
 * @author Audun Sorheim
 */
public class Location {

	private int locationId;
	private String locationName;
	
	public Location() {}
	
	public Location(int locationId) {
		this.locationId = locationId;
	}

	public Location(int locationId, String locationName) {
		this.locationName = locationName;
		this.locationId = locationId;
	}
	public String getlocationName() {
		return locationName;
	}
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	public int getLocationId() {
		return locationId;
	}
	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}
}
	