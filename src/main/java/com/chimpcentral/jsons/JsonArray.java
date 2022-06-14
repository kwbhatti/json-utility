package com.chimpcentral.jsons;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.response.Response;

public class JSONArray extends ArrayList<Object> implements Searchable, Mutable<JSONArray> {

	private static final long serialVersionUID = -6279472291597759549L;
	
	public JSONArray() {
	}
	
	public JSONArray(List<?> list) {
		this.addAll(list);
	}
	
	public JSONArray(String jsonString) throws IOException {
		this.addAll(JSONUtils.convertJSONStringToList(jsonString));
	}
	
	public JSONArray(File jsonFile) throws IOException {
		this.addAll(JSONUtils.convertJSONFileToList(jsonFile));
	}
	
	public JSONArray(Response response) throws IOException {
		this(response.asString());
	}
	
	JSONPath jsonPath(String jsonpath) {
		return new JSONPath(this, jsonpath);
	}
	
	JSONPath jsonPath(JSONObject jsonObject, String jsonpath) {
		return new JSONPath(jsonObject, jsonpath);
	}
	
	@Override
	public Object get(String jsonpath) {
		return jsonPath(jsonpath).getFromJSONArray();
	}
	
	@Override
	public boolean contains(String jsonpath) {
		String arrayIndexNode = JSONPathUtility.getFirstNode(jsonpath);
		JSONObject jsonObject = this.getAsJSONObject(arrayIndexNode);
		jsonpath = JSONPathUtility.removeFirstNode(jsonpath);
		return jsonPath(jsonObject, jsonpath).containsInJSONObject();
	}
	
	@Override
	public <V> JSONArray add(String jsonpath, V value) {
		String arrayIndexNode = JSONPathUtility.getFirstNode(jsonpath);
		JSONObject jsonObject = this.getAsJSONObject(arrayIndexNode);
		jsonpath = JSONPathUtility.removeFirstNode(jsonpath);
		jsonObject = jsonPath(jsonObject, jsonpath).addToJSONObject(value);
		int arrayIndex = JSONPathUtility.getArrayIndexFromArrayNodepath(arrayIndexNode);
		this.remove(arrayIndex);
		this.add(arrayIndex, jsonObject);
		return this;
	}
	
	@Override
	public JSONArray delete(String jsonpath) {
		String arrayIndexNode = JSONPathUtility.getFirstNode(jsonpath);
		JSONObject jsonObject = this.getAsJSONObject(arrayIndexNode);
		jsonpath = JSONPathUtility.removeFirstNode(jsonpath);
		jsonObject = jsonPath(jsonObject, jsonpath).deleteFromJSONObject();
		int arrayIndex = JSONPathUtility.getArrayIndexFromArrayNodepath(arrayIndexNode);
		this.remove(arrayIndex);
		this.add(arrayIndex, jsonObject);
		return this;
	}
	
	@Override
	public String toString() {
		try {
			return JSONUtils.convertListToJSONString(this);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Could not JSONArray to String + " + e.getStackTrace());
		}		
	}
	
}
