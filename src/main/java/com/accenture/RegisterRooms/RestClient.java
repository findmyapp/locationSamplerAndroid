package com.accenture.RegisterRooms;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.database.Cursor;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Klient for å kommunisere med serveren
 * En del kodeduplisering, bør refaktoreres 
 * @author audun.sorheim
 *
 */

public class RestClient {

	private String postSampleUrl = "http://findmyapp.net/findmyapp/location/sample"; // localhost																				// emulator
	private String getAllLocationsURL = "http://findmyapp.net/findmyapp/location/all";
	private String getWhereIAmURL = "http://findmyapp.net/findmyapp/location";
	private String TAG = "RESTCLIENT";
	private boolean sent;
	private Gson gson;

	public RestClient() {
		sent = false;
	}

	/**
	 * Sends the current location to the server
	 * @param json
	 * @return
	 */
	public boolean sendCurrentLocation(String json) {
		postSampleStream(json);
		Log.e(TAG, json);
		return sent;
	}

	/**
	 * Gets all stored locations from the server
	 * @return
	 */
	public Location[] getAllLocations() {
		Location[] locations = null;
		InputStream source = getAllLocationsStream();

		Gson gson = new Gson();
		if (source != null) {
			Log.e(getClass().getSimpleName(), "source is not null :) ");
			Reader reader = new InputStreamReader(source);

			JsonParser parser = new JsonParser();
			JsonElement jsonElement = parser.parse(reader);

			Log.e(getClass().getSimpleName() + "getAllLocations",
					"Array length: "
							+ gson.fromJson(
									jsonElement.getAsJsonObject().get(
											"locationList"), Location[].class).length);
			locations = new Location[gson.fromJson(jsonElement
					.getAsJsonObject().get("locationList"), Location[].class).length];
			locations = gson.fromJson(
					jsonElement.getAsJsonObject().get("locationList"),
					Location[].class);

			if (locations == null) {
				Log.e(getClass().getSimpleName() + "getAllLocations",
						"Cannot parse json to room :(");
			} else {
				Log.e(getClass().getSimpleName() + "getAllLocations",
						"Json parse was successful");
			}

		} else {
			Log.e(getClass().getSimpleName() + "getAllLocations",
					"source is null :(");
		}

		return locations;
	}

	/**
	 * Gets the location you are at
	 * @param json
	 * @return
	 */
	public Location getWhereIAm(String json) {

		Location location = null;
		// json = getSampleData(); //for testing
		InputStream source = getWhereIAmStream(json);

		Gson gson = new Gson();
		if (source != null) {
			Log.e(getClass().getSimpleName(), "source is not null :) ");
			Reader reader = new InputStreamReader(source);

			JsonParser parser = new JsonParser();
			JsonElement jsonElement = parser.parse(reader);
			location = gson.fromJson(jsonElement.getAsJsonObject().get("room"),
					Location.class);

			if (location == null) {
				Log.e(getClass().getSimpleName(),
						"Cannot parse json to location :(");
			}

		} else {
			Log.e(getClass().getSimpleName(), "source is null :(");
		}

		return location;
	}

//	public Room deserializeRoom(String jsonRoom) {
//		Room room = gson.fromJson(jsonRoom, Room.class);
//		return room;
//	}

	/**
	 * Stream used for posting samples
	 */
	private InputStream postSampleStream(String json) {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(postSampleUrl);

		StringEntity entity;
		try {
			entity = new StringEntity(json);
			request.setEntity(entity);
			request.setHeader("Accept", "application/json");
			request.setHeader("Content-type", "application/json");
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "Første catch");
			Log.e(getClass().getSimpleName(),
					"Could not create entity from string: " + json, e);
			e.printStackTrace();
		}
		try {

			HttpResponse getResponse = client.execute(request);
			final int statusCode = getResponse.getStatusLine().getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {
				Log.e(getClass().getSimpleName() + " , gamlemetoden!", "Error "
						+ statusCode + " for URL " + postSampleUrl);
				return null;
			} else {
				Log.e("RESTCLIENT", "ok");
				sent = true;
			}
			HttpEntity getResponseEntity = getResponse.getEntity();
			return getResponseEntity.getContent();
		} catch (IOException e) {
			request.abort();
			Log.e(getClass().getSimpleName(), "Error for URL " + postSampleUrl,
					e);
		}
		return null;
	}

	/**
	 * Stream used to get all locations
	 * @return
	 */
	public InputStream getAllLocationsStream() {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(getAllLocationsURL);

		request.setHeader("Accept", "application/json");
		request.setHeader("Content-type", "application/json");

		try {

			HttpResponse getResponse = client.execute(request);
			final int statusCode = getResponse.getStatusLine().getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {
				Log.e(getClass().getSimpleName() + " , nyemetoden!", "Error "
						+ statusCode + " for URL " + getAllLocationsURL);
				return null;
			} else {
				Log.e("RESTCLIENT", "ok");
				sent = true;
			}
			HttpEntity getResponseEntity = getResponse.getEntity();
			return getResponseEntity.getContent();
		} catch (IOException e) {
			request.abort();
			Log.e(getClass().getSimpleName(), "Error for URL " + getAllLocationsURL,
					e);
		}
		return null;
	}

	/**
	 * Stream used to get where you are
	 * @param json
	 * @return
	 */
	private InputStream getWhereIAmStream(String json) {

		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(getWhereIAmURL);

		StringEntity entity;
		try {
			entity = new StringEntity(json);
			request.setEntity(entity);
			request.setHeader("Accept", "application/json");
			request.setHeader("Content-type", "application/json");
		} catch (UnsupportedEncodingException e) {
			Log.e(getClass().getSimpleName(),
					"Could not create entity from string: " + json, e);
			e.printStackTrace();
		}

		try {

			HttpResponse getResponse = client.execute(request);
			final int statusCode = getResponse.getStatusLine().getStatusCode();
			Log.e(getClass().getSimpleName(), "Got back status code:"
					+ statusCode);
			if (statusCode != HttpStatus.SC_OK) {
				Log.e(getClass().getSimpleName(), "Error " + statusCode
						+ " for URL " + getWhereIAmURL);
				return null;
			}

			HttpEntity getResponseEntity = getResponse.getEntity();
			return getResponseEntity.getContent();

		} catch (IOException e) {
			request.abort();
			Log.e(getClass().getSimpleName(),
					"Error for URL " + getWhereIAmURL, e);
		}

		return null;

	}

}