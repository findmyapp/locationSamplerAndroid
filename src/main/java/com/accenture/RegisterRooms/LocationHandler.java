package com.accenture.RegisterRooms;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * LocationHandler uses the locationManager class to set how often the
 * application should scan for WIFI information.
 * 
 * The manager can also retrieve GPS / Speed / Navigation information if needed
 * for future development (...)
 * 
 * @author audun.sorheim
 * 
 */

public class LocationHandler {

	private Location currentLocation;
	private LocationManager locationManager;
	private Activity activity;

	public LocationHandler(Activity activity) {

		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) activity
				.getSystemService(Context.LOCATION_SERVICE);
		Log.e("LocationManager",
				"LM is not null ?:" + locationManager.equals(null));

		currentLocation = locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	}

	// Define a listener that responds to location updates
	LocationListener locationListener = new LocationListener() {
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onLocationChanged(Location location) {
			makeUseOfNewLocation(location);
		}
	};

	public Location getCurrentLocation() {
		return currentLocation;
	}

	/**
	 * Method to call when a new location is found
	 * @param location
	 */
	public void makeUseOfNewLocation(Location location) {
		if (location == null) {
			Log.e("Location", "Location is null");
		} else {
			currentLocation = location;
		}

	}

}
