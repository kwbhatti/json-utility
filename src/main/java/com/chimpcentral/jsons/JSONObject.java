package com.chimpcentral.jsons;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.response.Response;

/**
 * JSONObject class allows the user to manipulate and retrieve information from a JSON Object
 * JSONArray could be created using one of the many constructors 
 * @author kbhatti
 */
public class JSONObject extends HashMap<Object, Object> implements Searchable, Mutable<JSONObject> {

	private static final long serialVersionUID = -6279472291597759549L;
	
	/**
	 * Constructor: creates an empty JSON Object
	 */
	public JSONObject() {
	}
	
	/**
	 * Constructor: creates a JSON Object from a provided map
	 * @param map
	 */
	public JSONObject(Map<?, ?> map) {
		this.putAll(map);
	}
	
	/**
	 * Constructor: creates a JSON Object from a io.restassured.response.Response
	 * @param response
	 * @throws IOException
	 */
	public JSONObject(Response response) throws IOException {
		this(response.asString());
	}
	
	/**
	 * Constructor: creates a JSON Object from a File with JSON content
	 * @param jsonFile
	 * @throws IOException
	 */
	public JSONObject(File jsonFile) throws IOException {
		this.putAll(JSONUtils.convertJSONFileToHashMap(jsonFile));
	}
	
	/**
	 * Constructor: created a JSON Object from a JSON as a String
	 * @param jsonString
	 * @throws IOException
	 */
	public JSONObject(String jsonString) throws IOException {
		this.putAll(JSONUtils.convertJSONStringToHashmap(jsonString));
	}
	
	JSONPath jsonPath(String jsonpath) {
		return new JSONPath(this, jsonpath);
	}
	
	/**
	 * Retrieves the value from a JSON path from JSONObject
	 * Example JSONObject: JSONObject myJSONObject = new JSONObject({object goes here});
	 * <br>Example: myJSONObject.get("node1.node2.node3.node4");
	 * <br>Example (nested array): myJSONObject.get("node1.node2.nestedNode3[1].node4");
	 * <br>Example (dot in node): myJSONObject.get("node1.'node.with.dot'.node3");
	 */
	@Override
	public Object get(String jsonpath) {
		return jsonPath(jsonpath).getFromJSONObject();
	}
	
	/**
	 * Checks if the JSONObject contains a JSON path
	 * Example JSONObject: JSONObject myJSONObject = new JSONObject({object goes here});
	 * <br>Example: myJSONObject.contains("node1.node2.node3.node4");
	 * <br>Example (nested array): myJSONObject.contains("node1.node2.nestedNode3[1].node4");
	 * <br>Example (dot in node): myJSONObject.contains("node1.'node.with.dot'.node3");
	 */
	@Override
	public boolean contains(String jsonpath) {
		return jsonPath(jsonpath).containsInJSONObject();
	}
	
	/**
	 * Adds/Updates the JSONObject using the JSON path and its respective parents
	 * Returns JSON Object
	 * For example: if an element needs to be added to the following path: "node1.node2.node3.node4"
	 * <br>The JSONObject to add this path to does not exist node1, node2, node3, node4
	 * <br>It would recursively add all the four nodes to the JSONObject
	 * Example JSONObject: JSONObject myJSONObject = new JSONObject({object goes here});
	 * <br>Example: myJSONObject.add("node1.node2.node3.node4", "some value");
	 * <br>Example (nested array): myJSONObject.contains("node1.node2.nestedNode3[]");
	 * <br>In the above case the nested array node can only happen at the final child
	 * <br>Example (dot in node): myJSONObject.contains("node1.'node.with.dot'.node3");
	 */
	@Override
	public <V> JSONObject add(String jsonpath, V value) {
		return jsonPath(jsonpath).addToJSONObject(value);
	}

	/**
	 * Deletes a JSON path from JSONObject
	 * Returns JSON Object
	 * Example JSONObject: JSONObject myJSONObject = new JSONObject({object goes here});
	 * <br>Example: myJSONObject.delete("node1.node2.node3.node4");
	 * <br>Example (nested array): myJSONObject.delete("node1.node2.nestedNode3[1].node4");
	 * <br>Example (dot in node): myJSONObject.delete("node1.'node.with.dot'.node3");	 
	 */
	@Override
	public JSONObject delete(String jsonpath) {
		return jsonPath(jsonpath).deleteFromJSONObject();
	}
	
	/**
	 * Converts the JSONObject into a JSON String
	 */
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
