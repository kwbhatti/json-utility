package com.chimpcentral.jsons;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.response.Response;

/**
 * JSONArray class allows the user to manipulate and retrieve information from a JSON Array
 * JSONArray could be created using one of the many constructors 
 * @author kbhatti
 */
public class JSONArray extends ArrayList<Object> implements Searchable, Mutable<JSONArray> {

	private static final long serialVersionUID = -6279472291597759549L;
	
	/**
	 * Constructor: creates an empty JSON Array
	 */
	public JSONArray() {
	}
	
	/**
	 * Constructor: creates a JSON Array from a provided list
	 * @param list
	 */
	public JSONArray(List<?> list) {
		this.addAll(list);
	}
	
	/**
	 * Constructor: created a JSON Array from a JSON as a String
	 * @param jsonString
	 * @throws IOException
	 */
	public JSONArray(String jsonString) throws IOException {
		this.addAll(JSONUtils.convertJSONStringToList(jsonString));
	}
	
	/**
	 * Constructor: creates a JSON Array from a File with JSON content
	 * @param jsonFile
	 * @throws IOException
	 */
	public JSONArray(File jsonFile) throws IOException {
		this.addAll(JSONUtils.convertJSONFileToList(jsonFile));
	}
	
	/**
	 * Constructor: creates a JSON Array from a io.restassured.response.Response
	 * @param response
	 * @throws IOException
	 */
	public JSONArray(Response response) throws IOException {
		this(response.asString());
	}
	
	JSONPath jsonPath(String jsonpath) {
		return new JSONPath(this, jsonpath);
	}
	
	JSONPath jsonPath(JSONObject jsonObject, String jsonpath) {
		return new JSONPath(jsonObject, jsonpath);
	}
	
	/**
	 * Retrieves the value from a JSON path from JSONArray
	 * Example JSONArray: JSONArray myJSONArray = new JSONArray({object goes here});
	 * <br>Example: myJSONArray.get("[0].node1.node2.node3.node4");
	 * <br>Example (nested array): myJSONArray.get("[0].node1.node2.nestedNode3[1].node4");
	 * <br>Example (dot in node): myJSONArray.get("[0].node1.'node.with.dot'.node3");
	 * @return value from the path as an Object
	 */
	@Override
	public Object get(String jsonpath) {
		return jsonPath(jsonpath).getFromJSONArray();
	}
	
	/**
	 * Checks if the JSONArray contains a JSON path
	 * Example JSONArray: JSONArray myJSONArray = new JSONArray({object goes here});
	 * <br>Example: myJSONArray.contains("[0].node1.node2.node3.node4");
	 * <br>Example (nested array): myJSONArray.contains("[0].node1.node2.nestedNode3[1].node4");
	 * <br>Example (dot in node): myJSONArray.contains("[0].node1.'node.with.dot'.node3");
	 * @return if JSONpath exists in JSONArray
	 */
	@Override
	public boolean contains(String jsonpath) {
		String arrayIndexNode = JSONPathUtility.getFirstNode(jsonpath);
		JSONObject jsonObject = this.getAsJSONObject(arrayIndexNode);
		jsonpath = JSONPathUtility.removeFirstNode(jsonpath);
		return jsonPath(jsonObject, jsonpath).containsInJSONObject();
	}
	
	/**
	 * Adds/Updates the JSONArray using the JSON path and its respective parents
	 * Returns JSON Array
	 * For example: if an element needs to be added to the following path: "node1.node2.node3.node4"
	 * <br>The JSONArray to add this path to does not exist node1, node2, node3, node4
	 * <br>It would recursively add all the four nodes to the JSONArray
	 * Example JSONArray: JSONArray myJSONArray = new JSONArray({object goes here});
	 * <br>Example: myJSONArray.add("[0].node1.node2.node3.node4", "some value");
	 * <br>Example (nested array): myJSONArray.contains("[0].node1.node2.nestedNode3[]");
	 * <br>In the above case the nested array node can only happen at the final child
	 * <br>Example (dot in node): myJSONArray.contains("[0].node1.'node.with.dot'.node3");
	 * @return object of this Class
	 */
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
	
	/**
	 * Deletes a JSON path from JSONArray
	 * Returns JSON Array
	 * Example JSONArray: JSONArray myJSONArray = new JSONArray({object goes here});
	 * <br>Example: myJSONArray.delete("[0].node1.node2.node3.node4");
	 * <br>Example (nested array): myJSONArray.delete("[0].node1.node2.nestedNode3[1].node4");
	 * <br>Example (dot in node): myJSONArray.delete("[0].node1.'node.with.dot'.node3");	
	 * @return object of this Class 
	 */
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
	
	/**
	 * Converts the JSONArray into a JSON String
	 * @return JSON String of this Class
	 */
	@Override
	public String toString() {
		try {
			return JSONUtils.convertListToJSONString(this);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Could not JSONArray to String + " + e.getStackTrace());
		}		
	}
	
}
