package com.accenture.RegisterRooms;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class RestClient {

	private String url = "http://findmyapp.net/findmyapp/position/sample/"; // localhost on emulator
	private Gson gson;
	private String TAG = "RESTCLIENT";
	private boolean sent;

	public RestClient() {
		sent = false;
	}

	private InputStream retrieveStream(String json) {
		Log.e(TAG, "Først!");
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(url);
		Log.e(TAG, "Før Stringrequest");

		StringEntity entity;
		try {
			Log.e(TAG, "Inne i første try");
			entity = new StringEntity(json);
			request.setEntity(entity);
			request.setHeader("Accept", "application/json");
			request.setHeader("Content-type", "application/json");
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "Første catch");
			Log.e(getClass().getSimpleName(), "Could not create entity from string: " + json, e);
			e.printStackTrace();
		}

		try {

			HttpResponse getResponse = client.execute(request);
			final int statusCode = getResponse.getStatusLine().getStatusCode();

			if (statusCode != HttpStatus.SC_OK) { 
				Log.e(getClass().getSimpleName(), 
						"Error " + statusCode + " for URL " + url); 
				return null;
			}
			else {
				Log.e("RESTCLIENT", "ok");
				sent = true;
			}
			HttpEntity getResponseEntity = getResponse.getEntity();
			return getResponseEntity.getContent();

		} 
		catch (IOException e) {
			request.abort();
			Log.e(getClass().getSimpleName(), "Error for URL " + url, e);
		}

		return null;

	}

	public boolean sendRoom(String json){
		retrieveStream(json);
		return sent;		
	}
	
	public Room getRoom(String json){
		
		Room room = null;
		json = getSampleData(); //for testing
		InputStream source = retrieveStream(json);

		Gson gson = new Gson();
		if (source != null){
			Log.e(getClass().getSimpleName(), "source is not null :) ");
			Reader reader = new InputStreamReader(source);
			
			JsonParser parser = new JsonParser();
			JsonElement jsonElement = parser.parse(reader);
			room = gson.fromJson(jsonElement.getAsJsonObject().get("room"), Room.class);
			
			if(room == null) {
				Log.e(getClass().getSimpleName(), "Cannot parse json to room :(");
			}
			
		} else {
			Log.e(getClass().getSimpleName(), "source is null :(");
		}

		return room;
	}
	
	public Room deserializeRoom(String jsonRoom) {
		Room room = gson.fromJson(jsonRoom, Room.class);
		return room;
	}
	
	private String getSampleData(){
		Gson gson = new Gson();
		Signal[] protocolArray = new Signal[2];
		protocolArray[0] = new Signal("Strossa",-80);
		protocolArray[1] = new Signal("Storsalen",-20);
		String json = gson.toJson(protocolArray);
		return json;
	}
	

}
