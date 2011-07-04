package com.accenture.RegisterRooms;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity for position. Needs a position handler to have any use
 * 
 * @author audun.sorheim, cecilie haugstvedt, kristin astebol
 * 
 */

public class RegisterRoomsActivity extends Activity {

	private String TAG = "RegisterRoomActivity";
	private Button populatePositionDataButton, sendRoomButton;
	private TextView longitudeTextView, latitudeTextView, ssidTextView;
	private ScanResult scanResult;
	private Location currentLocation = null;
	private WifiPositionHandler wifiPositionHandler;
	private List<ScanResult> scanList;
	private RestClient restClient;
	private String jsonSample = null;
	private EditText roomNameEditText;
	private LocationHandler locationHandler;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		scanList = null;
		restClient = new RestClient();
		locationHandler = new LocationHandler(this);

		// Init GUI
		roomNameEditText = (EditText) findViewById(R.id.roomName);
		ssidTextView = (TextView) findViewById(R.id.bssid);
		populatePositionDataButton = (Button) findViewById(R.id.populateLocationButton);
		sendRoomButton = (Button) findViewById(R.id.getRoomButton);

		// Init Positionhandler and scan for ssid
		wifiPositionHandler = new WifiPositionHandler(this);
		wifiPositionHandler.scanForSSID();

		// Sets listener on buttons
		sendRoomButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.e(TAG, "Pushed send room");
				if (roomNameEditText.getText().toString() == ""
						|| roomNameEditText.getText().toString() == "Room name") {
					printToScreen("Enter a room name");
				} else {
					String sample = getSample();
					if (sample != null) {
						boolean sent = restClient.sendRoom(sample);
						if(sent == true) {
							printToScreen("Punktet ble registrert");
						}
						else {
							printToScreen("Punktet ble ikke registrert");
						}
						
					} else {
						printToScreen("No sample to send");
					}
				}
			}
		});
		populatePositionDataButton
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						Log.e("KNAPPTEST", "Tester populate knapp");
						updateBSSID();
					}

				});

	}

	public String getSample() {
		// AlertDialog.Builder alert = new AlertDialog.Builder(this);
		// alert.setTitle("Rom");
		// alert.setMessage("Skriv inn romnavn");

		// // Set an EditText view to get user input
		// final EditText input = new EditText(this);
		// alert.setView(input);

		// alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int whichButton) {
		// String value = input.getText().toString();
		// getSample(value);
		// }
		// });
		//
		// alert.setNegativeButton("Cancel",
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int whichButton) {
		// // Canceled.
		// }
		// });
		//
		// alert.show();
		String room = roomNameEditText.getText().toString();
		String jsonRoom = wifiPositionHandler.registerSampleToJSON(room);

		return jsonRoom;

	}

	private void getSample(String value) {

		jsonSample = wifiPositionHandler.registerSampleToJSON(value);
		// TODO sende jsonSample til sercer
		Log.e("testings", jsonSample);
	}

	// /**
	// * Gets latitude and longitude, and sets the non existing textviews.
	// */
	// public void updateLocation() {
	// if (wifiPositionHandler.getCurrentLocation() == null) {
	// Log.e("Location", "Location is null");
	// } else {
	// currentLocation = wifiPositionHandler.getCurrentLocation();
	// Log.e("NetworkAndroidActivity",
	// "Latitude: " + currentLocation.getLatitude()
	// + " Longitude: " + currentLocation.getLongitude());
	//
	// longitudeTextView.setText("" + currentLocation.getLongitude());
	// latitudeTextView.setText("" + currentLocation.getLatitude());
	// }
	// }

	/**
	 * Initializes a SSID search in the handler Update the (GUI) BSSID
	 * (MAC-address for the router)
	 */
	public void updateBSSID() {
		if (wifiPositionHandler.getScanList() == null) {
			Log.e("SCANRESULTS", "There are no scan results");
			wifiPositionHandler.scanForSSID();
		} else {
			scanList = wifiPositionHandler.getScanList();
			Log.e("SCANRESULTS", "Oh yeah");

			scanResult = scanList.get(0);

			Log.e("NetworkAndroidActivity", scanResult.BSSID + " "
					+ scanResult.level);
			ssidTextView.setText(scanResult.BSSID);
		}

	}

	public void printToScreen(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	// /**
	// * Deserialize a jsonSignal (string) to readable data in LogCat
	// *
	// * @param jsonSignal
	// */
	// public void readFromJSON(String jsonSignal) {
	// Gson gson = new Gson();
	// Signal[] signal = gson.fromJson(jsonSignal, Signal[].class);
	// for (int i = 0; i < signal.length; i++) {
	// Log.e("From JSON ",
	// signal[i].getBssid() + " " + signal[i].getSignalStrength());
	// }
	// }

	// /**
	// * Tests writeListToJSON and readFromJSON
	// */
	// public void testToFromJSON() {
	// String json = writeListToJSON(scanList);
	// readFromJSON(json);
	// Log.e("STRINGTEST", json);
	// }

	public void enableButtons() {
		sendRoomButton.setEnabled(true);
		populatePositionDataButton.setEnabled(true);
	}

}