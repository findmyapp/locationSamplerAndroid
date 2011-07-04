package com.accenture.RegisterRooms;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class SampleJsonSerializer implements JsonSerializer<RegisterSampleForRoom> {
	
	
    

	@Override
	public JsonElement serialize(RegisterSampleForRoom sample, Type type, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
        object.add("id", context.serialize(sample.getId()));
        object.add("roomId", context.serialize(sample.getRoomId()));
        object.add("roomName", context.serialize(sample.getRoomName()));
        object.add("signalList", context.serialize(sample.getSignalList()));

        return object;
	}

}
