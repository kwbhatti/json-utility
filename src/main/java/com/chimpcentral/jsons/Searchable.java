package com.chimpcentral.jsons;

import java.util.List;
import java.util.Map;

interface Searchable {

	Object get(String jsonpath);
	
	public boolean contains(String jsonpath);
	
	public default String getAsString(String jsonpath) {
		return String.valueOf(get(jsonpath));
	}
	
	public default boolean getAsBoolean(String jsonpath) {
		String valueStr = getAsString(jsonpath);
		if (valueStr.equals("true") || valueStr.equals("false")) return Boolean.valueOf(valueStr);
		throw new ClassCastException();
	}
	
	public default int getAsInt(String jsonpath) {
		return Integer.valueOf(getAsString(jsonpath));
	}
	
	public default double getAsDouble(String jsonpath) {
		return Double.valueOf(getAsString(jsonpath));
	}
	
	public default float getAsFloat(String jsonpath) {
		return Float.valueOf(getAsString(jsonpath));
	}
	
	public default long getAsLong(String jsonpath) {
		return Long.valueOf(getAsString(jsonpath));
	}
	
	public default short getAsShort(String jsonpath) {
		return Short.valueOf(getAsString(jsonpath));
	}
	
	public default JSONObject getAsJSONObject(String jsonpath) {
		return new JSONObject(getAsMap(jsonpath));
	}
	
	public default JSONArray getAsJSONArray(String jsonpath) {
		return new JSONArray(getAsList(jsonpath));
	}
	
	public default Map<?, ?> getAsMap(String jsonpath) {
		return (Map<?, ?>) get(jsonpath);
	}

	public default List<?> getAsList(String jsonpath) {
		return (List<?>) get(jsonpath);
	}
	
	@SuppressWarnings("unchecked")
	default Map<Object, Object> getAsObjectsMap(String jsonpath) {
		if (get(jsonpath) instanceof Map<?, ?>) return (Map<Object, Object>) get(jsonpath);
		else throw new IllegalArgumentException("");
	}
	
	@SuppressWarnings("unchecked")
	default List<Object> getAsObjectsList(String jsonpath) {
		if (get(jsonpath) instanceof List<?>) return (List<Object>) get(jsonpath);
		else throw new IllegalArgumentException("");
	}
}
