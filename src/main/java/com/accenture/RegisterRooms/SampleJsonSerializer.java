package com.accenture.RegisterRooms;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Custom GSON serializer, to have correct formatting of the JSON sample string
 * @author audun.sorheim, kristin astebol
 *
 */

public class SampleJsonSerializer implements JsonSerializer<RegisterSampleForRoom> {
	
	
    

	@Override
	public JsonElement serialize(RegisterSampleForRoom sample, Type type, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
        object.add("id", context.serialize(sample.getId()));
        object.add("locationId", context.serialize(sample.getLocationId()));
        object.add("locationName", context.serialize(sample.getLocationName()));
        object.add("signalList", context.serialize(sample.getSignalList()));

        return object;
	}

}
