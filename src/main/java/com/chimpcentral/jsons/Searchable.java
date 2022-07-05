package com.chimpcentral.jsons;

import java.util.List;
import java.util.Map;

interface Searchable {

	Object get(String jsonpath);
	
	public boolean contains(String jsonpath);
	
	/**
	 * Retrieves the value as a String from a JSON path from JSONObject/JSONArray
	 * @see com.chimpcentral.jsons.JSONObject#get(String)
	 * @see com.chimpcentral.jsons.JSONArray#get(String)
	 * @param jsonpath
	 * @return value from JSON Path as String
	 */
	public default String getAsString(String jsonpath) {
		return String.valueOf(get(jsonpath));
	}
	
	/**
	 * Retrieves the value as a boolean from a JSON path from JSONObject/JSONArray
	 * @see com.chimpcentral.jsons.JSONObject#get(String)
	 * @see com.chimpcentral.jsons.JSONArray#get(String)
	 * @param jsonpath
	 * @return value from JSON Path as boolean
	 */
	public default boolean getAsBoolean(String jsonpath) {
		String valueStr = getAsString(jsonpath);
		if (valueStr.equals("true") || valueStr.equals("false")) return Boolean.valueOf(valueStr);
		throw new ClassCastException();
	}
	
	/**
	 * Retrieves the value as an int from a JSON path from JSONObject/JSONArray
	 * @see com.chimpcentral.jsons.JSONObject#get(String)
	 * @see com.chimpcentral.jsons.JSONArray#get(String)
	 * @param jsonpath
	 * @return value from JSON Path as int
	 */
	public default int getAsInt(String jsonpath) {
		return Integer.valueOf(getAsString(jsonpath));
	}
	
	/**
	 * Retrieves the value as a double from a JSON path from JSONObject/JSONArray
	 * @see com.chimpcentral.jsons.JSONObject#get(String)
	 * @see com.chimpcentral.jsons.JSONArray#get(String)
	 * @param jsonpath
	 * @return value from JSON Path as double
	 */
	public default double getAsDouble(String jsonpath) {
		return Double.valueOf(getAsString(jsonpath));
	}
	
	/**
	 * Retrieves the value as a float from a JSON path from JSONObject/JSONArray
	 * @see com.chimpcentral.jsons.JSONObject#get(String)
	 * @see com.chimpcentral.jsons.JSONArray#get(String)
	 * @param jsonpath
	 * @return value from JSON Path as float
	 */
	public default float getAsFloat(String jsonpath) {
		return Float.valueOf(getAsString(jsonpath));
	}
	
	/**
	 * Retrieves the value as a long from a JSON path from JSONObject/JSONArray
	 * @see com.chimpcentral.jsons.JSONObject#get(String)
	 * @see com.chimpcentral.jsons.JSONArray#get(String)
	 * @param jsonpath
	 * @return value from JSON Path as long
	 */
	public default long getAsLong(String jsonpath) {
		return Long.valueOf(getAsString(jsonpath));
	}
	
	/**
	 * Retrieves the value as a short from a JSON path from JSONObject/JSONArray
	 * @see com.chimpcentral.jsons.JSONObject#get(String)
	 * @see com.chimpcentral.jsons.JSONArray#get(String)
	 * @param jsonpath
	 * @return value from JSON Path as short
	 */
	public default short getAsShort(String jsonpath) {
		return Short.valueOf(getAsString(jsonpath));
	}
	
	/**
	 * Retrieves the value as a JSONObject from a JSON path from JSONObject/JSONArray
	 * @see com.chimpcentral.jsons.JSONObject#get(String)
	 * @see com.chimpcentral.jsons.JSONArray#get(String)
	 * @param jsonpath
	 * @return value from JSON Path as JSONObject
	 */
	public default JSONObject getAsJSONObject(String jsonpath) {
		return new JSONObject(getAsMap(jsonpath));
	}
	
	/**
	 * Retrieves the value as a JSONArray from a JSON path from JSONObject/JSONArray
	 * @see com.chimpcentral.jsons.JSONObject#get(String)
	 * @see com.chimpcentral.jsons.JSONArray#get(String)
	 * @param jsonpath
	 * @return value from JSON Path as JSONArray
	 */
	public default JSONArray getAsJSONArray(String jsonpath) {
		return new JSONArray(getAsList(jsonpath));
	}
	
	/**
	 * Retrieves the value as a Map from a JSON path from JSONObject/JSONArray
	 * @see com.chimpcentral.jsons.JSONObject#get(String)
	 * @see com.chimpcentral.jsons.JSONArray#get(String)
	 * @param jsonpath
	 * @return value from JSON Path as Map<?, ?>
	 */
	public default Map<?, ?> getAsMap(String jsonpath) {
		return (Map<?, ?>) get(jsonpath);
	}

	/**
	 * Retrieves the value as a List from a JSON path from JSONObject/JSONArray
	 * @see com.chimpcentral.jsons.JSONObject#get(String)
	 * @see com.chimpcentral.jsons.JSONArray#get(String)
	 * @param jsonpath
	 * @return value from JSON Path as List<?>
	 */
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
