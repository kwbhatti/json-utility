package com.chimpcentral.jsons;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.response.Response;

public class JSONObject extends HashMap<Object, Object> implements Searchable, Mutable<JSONObject> {

	private static final long serialVersionUID = -6279472291597759549L;
	
	public JSONObject() {
	}
	
	public JSONObject(Map<?, ?> map) {
		this.putAll(map);
	}
	
	public JSONObject(Response response) throws IOException {
		this(response.asString());
	}
	
	public JSONObject(File jsonFile) throws IOException {
		this.putAll(JSONUtils.convertJSONFileToHashMap(jsonFile));
	}
	
	public JSONObject(String jsonString) throws IOException {
		this.putAll(JSONUtils.convertJSONStringToHashmap(jsonString));
	}
	
	JSONPath jsonPath(String jsonpath) {
		return new JSONPath(this, jsonpath);
	}
	
	@Override
	public Object get(String jsonpath) {
		return jsonPath(jsonpath).getFromJSONObject();
	}
	
	@Override
	public boolean contains(String jsonpath) {
		return jsonPath(jsonpath).containsInJSONObject();
	}
	
	@Override
	public <V> JSONObject add(String jsonpath, V value) {
		return jsonPath(jsonpath).addToJSONObject(value);
	}

	@Override
	public JSONObject delete(String jsonpath) {
		return jsonPath(jsonpath).deleteFromJSONObject();
	}
	
	@Override
	public String toString() {
		try {
			return JSONUtils.convertMapToJSONString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Could not JSONObject to String + " + e.getStackTrace());
		}		
	}	
	
}
