package com.accenture.RegisterRooms;

import java.util.List;

import com.accenture.RegisterRooms.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
	private Button populatePositionDataButton, sendRoomButton,
			getWhereIAmButton;;
	private TextView ssidTextView;
	private EditText roomNameEditText;
	private ScanResult scanResult;
	private WifiPositionHandler wifiPositionHandler;
	private List<ScanResult> scanList;
	private RestClient restClient;
	private LocationHandler locationHandler;
	private ArrayAdapter<CharSequence> adapter;
	private Spinner spinner;
	private RegisterRoomsActivity registerRoomsActivity = this;

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
		getWhereIAmButton = (Button) findViewById(R.id.getWhereAmIButton);

		// Checks if wifi is enabled, and pops up an alertdialog if not, asking
		// to enable it
		wifiPositionHandler = new WifiPositionHandler(this);
		boolean wifiEnabled = wifiPositionHandler.isWifiEnabled();
		if (wifiEnabled != true) {
			wifiPositionHandler.setWifiState(true);
			AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
			alt_bld.setMessage("Applikasjonen avhenger av wifi. Skru på?")
					.setCancelable(false)
					.setPositiveButton("Ja",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									initiateComponents();
								}
							})
					.setNegativeButton("Nei",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									registerRoomsActivity.finish();
								}
							});
			AlertDialog alert = alt_bld.create();
			alert.setTitle("Registrering av punkt");
			alert.show();
		} else {
			initiateComponents();
		}
	}

	/**
	 * Initiates activity components / states.
	 */
	private void initiateComponents() {
		wifiPositionHandler.scanForSSID();

		// Getting locations from server
		Location[] locations = restClient.getAllLocations();
		if (locations == null) {
			printToScreen("Fikk ikke lokasjoner fra serveren");
			Log.e("Locationstest", "Kunne ikke hente lokasjoner fra server");
		} else {
			Log.e("Locationtest", "Hentet lokasjoner fra server");
			printToScreen("Hentet lokasjoner fra server");
			populateSpinner(locations);
		}

		// Sets listener on buttons
		sendRoomButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				sendSampleToServer();
			}
		});

		populatePositionDataButton
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						updateBSSID();
					}
				});

		getWhereIAmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				checkWhereIAm();
			}
		});
	}


	/**
	 * Figures out which location you are at, and toasts it to the screen.
	 */
	public void checkWhereIAm() {
		Location currentLocation = wifiPositionHandler.getPositionFromServer();
		if (currentLocation != null) {
			printToScreen(currentLocation.getlocationName() + " : "
					+ currentLocation.getLocationId());
		} else {
			Log.e(TAG, "getPositionFromServer returned null");
			printToScreen("No room found");
		}
	}

	/**
	 * Sends a sample to the server. Pops up a alert dialog which lets the user
	 * select which location name to be used
	 */
	public void sendSampleToServer() {
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
		alt_bld.setMessage("Hvilket lokasjonsnavn vil du bruke?")
				.setCancelable(false)
				.setPositiveButton("Opprett nytt",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								if (roomNameEditText.getText().toString() == ""
										|| roomNameEditText.getText()
												.toString() == "Opprett ny lokasjon") {
									printToScreen("Skriv inn romnavn");
								} else {
									String sample = getSample(roomNameEditText
											.getText().toString());
									if (sample != null) {
										boolean sent = restClient
												.sendCurrentLocation(sample);
										if (sent == true) {
											printToScreen("Punktet ble registrert");
										} else {
											printToScreen("Punktet ble ikke registrert");
										}
									} else {
										printToScreen("Fant ingen sample å sende, prøv igjen en gang eller tre!");
									}
								}
							}
						})
				.setNegativeButton("Fra liste",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								String locationName = spinner.getSelectedItem()
										.toString();
								String sample = getSample(locationName);
								if (sample != null) {
									boolean sent = restClient
											.sendCurrentLocation(sample);
									if (sent == true) {
										printToScreen("Punktet ble registrert");
									} else {
										printToScreen("Punktet ble ikke registrert");
									}
								} else {
									printToScreen("Fant ingen sample å sende, prøv igjen en gang eller tre!");
								}
							}
						});
		AlertDialog alert = alt_bld.create();
		alert.setTitle("Registrering av punkt");
		alert.show();
	}

	/**
	 * Populates the spinner with locations
	 * 
	 * @param locations
	 */
	public void populateSpinner(Location[] locations) {
		adapter = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		for (Location loc : locations) {
			adapter.add(loc.getlocationName());
			Log.e("Testing location array", loc.getlocationName());
		}

		spinner = (Spinner) findViewById(R.id.locationSpinner);
		spinner.setAdapter(adapter);
	}

	/**
	 * Takes a given location name as parameter and creates / returns a sample
	 * in json
	 * 
	 * @param location
	 * @return
	 */
	public String getSample(String location) {
		String jsonRoom = null;
		jsonRoom = wifiPositionHandler.registerSampleToJSON(location);
		return jsonRoom;
	}

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

	/**
	 * Toasts strings to the screen
	 * @param text string to be toasted
	 */
	public void printToScreen(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
	
}