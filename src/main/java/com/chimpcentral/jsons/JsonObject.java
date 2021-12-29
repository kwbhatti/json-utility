package com.chimpcentral.jsons;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.chimpcentral.comparison.JsonComparison;
import com.chimpcentral.data.Data;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.response.Response;

public class JsonObject extends HashMap<Object, Object> {

	private static final long serialVersionUID = 8203281341173702977L;
	
	public JsonObject() {
	}
	
	public JsonObject(File file) throws IOException {
		this.putAll(jsonStringToHashmap(file));
	}
	
	public JsonObject(String jsonString) throws IOException {
		this.putAll(jsonStringToHashmap(jsonString));
	}
	
	public JsonObject(HashMap<Object, Object> map) throws IOException {
		this(hashMapToJsonString(map));
	}
	
	public JsonObject(Response response) throws IOException {
		this(response.asString());
	}
	
	public static HashMap<Object, Object> jsonStringToHashmap(File file) throws IOException { 
	    ObjectMapper mapper = new ObjectMapper();
	    TypeReference<HashMap<Object,Object>> typeRef = new TypeReference<HashMap<Object,Object>>() {};
	    HashMap<Object,Object> map = mapper.readValue(file, typeRef);
	    return map;
	} 
	
	public static HashMap<Object, Object> jsonStringToHashmap(String jsonString) throws IOException {  
	    ObjectMapper mapper = new ObjectMapper(); 
	    TypeReference<HashMap<Object,Object>> typeRef = new TypeReference<HashMap<Object,Object>>() {};
	    HashMap<Object,Object> map = mapper.readValue(jsonString, typeRef); 
	    return map;
	}
	
	@Override
	public String toString() {
		return hashMapToJsonString(this);
	}

	public static String hashMapToJsonString(HashMap<Object, Object> map) {
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = null;
		try {
			jsonString = mapper.writeValueAsString(map);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return jsonString;
	}
	
	public Object get(String jsonpath) {
		Object currentValue = this;
		String[] jsonnodes;
		if (jsonpath.startsWith("'")) {
			jsonpath = jsonpath.substring(1, jsonpath.length() - 1);
			jsonnodes = jsonpath.split("'\\.'");
		} else {
			jsonnodes = jsonpath.split("\\.");
		}
		for (int i = 0; i < jsonnodes.length; i++) {
			boolean isAnArray = jsonnodes[i].endsWith("]");
			if (isAnArray) {
				currentValue = getFromList(currentValue, jsonnodes[i]);
			} else {
				currentValue = getFromMap(currentValue, jsonnodes[i]);
			}
		}
		return currentValue;
	}
	
	public Data getAs(String path) {
		return new Data(get(path));
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<Object, Object> getAsHashMap(String path) {
		if (get(path) instanceof HashMap<?, ?>) {
			return (HashMap<Object, Object>) get(path);
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> getAsList(String path) {
		if (get(path) instanceof List<?>) {
			return (List<Object>) get(path);
		} else {
			return null;
		}
	}
	
	public static Object getFromMap(Object map, String node) {
		if (map instanceof HashMap<?, ?>) {
			return ((HashMap<?, ?>) map).get(node);
		} else {
			return null;
		}
	}
	
	public static Object getFromList(Object jsonpath, String node) {
		int indexInt = Integer.parseInt(node.split("\\[")[1].replace("]", ""));	
		Object list = getFromMap(jsonpath, node.split("\\[")[0]);
		if (list instanceof List<?>) {
			Object object = ((List<?>) list).get(indexInt);
			
			int i = 2; 
			while(i < node.split("\\[").length && node.split("\\[")[i] != null) {
				if (object instanceof List<?>) {
					indexInt = Integer.parseInt(node.split("\\[")[i].replace("]", ""));
					object = ((List<?>) object).get(indexInt);
					i += 1;
				} else {
					break;
				}
			}
			return object;
		} else {
			return null;
		}
	}

	public JsonObject add(String jsonpath, Object value) {
		String[] jsonnodes;
		if (jsonpath.startsWith("'")) {
			jsonpath = jsonpath.substring(1, jsonpath.length() - 1);
			jsonnodes = jsonpath.split("'\\.'");
		} else {
			jsonnodes = jsonpath.split("\\.");
		}
		String parentPath = "";
		for (int i = 0; i < jsonnodes.length -1; i++) {
			parentPath = parentPath + jsonnodes[i] + ".";
		}
		if (jsonnodes.length <= 1) {
			this.put(jsonpath, value);
		} else {
			this.getAsHashMap(parentPath).put(jsonnodes[jsonnodes.length -1], value);
		}
		return this;
	}
	
	public JsonObject addIfNotNull(Object object, Object value) {
		if (value != null) {
			add((String)object, value);
		}
		return this;
	}
	
	public JsonObject add(HashMap<Object, Object> pathValuesMap) {
		for (Map.Entry<Object, Object> entry: pathValuesMap.entrySet()) {
			add((String)entry.getKey(), entry.getValue());
		}
		return this;
	}
	
	public JsonObject add(File additionsJsonObjectFile) throws IOException {
		JsonObject additionsJsonObj = new JsonObject(additionsJsonObjectFile);
		for (Map.Entry<Object, Object> entry: additionsJsonObj.entrySet()) {
			this.add((String)entry.getKey(), entry.getValue());
		}
		return this;
	}
	
	public JsonObject deleteIfExists(String path) {
		try {
			delete(path);
		} catch (NullPointerException e) {
			System.out.println("path did not exist");
		}
		return this;
	}
	
	public JsonObject delete(String jsonpath) {
		String[] jsonnodes;
		if (jsonpath.startsWith("'")) {
			jsonpath = jsonpath.substring(1, jsonpath.length() - 1);
			jsonnodes = jsonpath.split("'\\.'");
		} else {
			jsonnodes = jsonpath.split("\\.");
		}
		String parentPath = "";
		for (int i = 0; i < jsonnodes.length -1; i++) {
			parentPath = parentPath + jsonnodes[i] + ".";
		}
		if (jsonnodes.length < 1) {
			this.clear();
		} else if (jsonnodes.length == 1) {
			this.remove(jsonpath);
		} else {
			this.getAsHashMap(parentPath).remove(jsonnodes[jsonnodes.length -1]);
		}
		return this;
	}

	public boolean contains(String jsonpath) {
		String parentJsonpath = ".";
		String childJsonpath = null;
		if (jsonpath.startsWith("'")) {
			int splitIndex = jsonpath.lastIndexOf("'.'");
			childJsonpath = jsonpath.replaceAll("'", "");
			if (splitIndex > 0) {
				parentJsonpath = jsonpath.substring(0, splitIndex + 1);
				childJsonpath = jsonpath.substring(splitIndex + 3, jsonpath.length() - 1);
			}
		} else {
			int splitIndex = jsonpath.lastIndexOf(".");
			childJsonpath = jsonpath;
			if (splitIndex > 0)
				parentJsonpath = jsonpath.substring(0, splitIndex);
				childJsonpath = jsonpath.substring(splitIndex + 1, jsonpath.length());

		}		
		HashMap<Object, Object> parentMap = getAsHashMap(parentJsonpath);
		if (parentMap != null && parentMap.containsKey(childJsonpath)) return true;
		return false;
	}
	
	public void prettyPrint() throws JsonProcessingException {
		System.out.println(getPrettyPrint());
	}
	
	public String getPrettyPrint() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
	}
	
	public JsonObject copy() {
		return (JsonObject) this.clone();
	}
	
	public String getEscapedJsonString() {
		String escapedJsonString = this.toString();
		escapedJsonString = escapedJsonString.replace("\\", "\\\\");
	    escapedJsonString = escapedJsonString.replace("\"", "\\\"");
	    escapedJsonString = escapedJsonString.replace("\b", "\\b");
	    escapedJsonString = escapedJsonString.replace("\f", "\\f");
	    escapedJsonString = escapedJsonString.replace("\n", "\\n");
	    escapedJsonString = escapedJsonString.replace("\r", "\\r");
	    escapedJsonString = escapedJsonString.replace("\t", "\\t");
		return escapedJsonString;
	}
	
	public boolean matches(String regex, String input) {
		Pattern pattern = Pattern.compile(regex);
	    Matcher matcher = pattern.matcher(input);
		boolean result =  matcher.find();
		return result;
	}
	
	public JsonComparison compareTo(JsonObject jsonObject, boolean compareDatatypes) {
		return new JsonComparison(this, jsonObject, compareDatatypes);
	}
}