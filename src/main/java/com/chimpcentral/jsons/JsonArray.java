package com.chimpcentral.jsons;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.response.Response;


public class JsonArray extends ArrayList<JsonObject> {

	private static final long serialVersionUID = 4761119536327342144L;

	public JsonArray() {
	}
	
	public JsonArray(File file) throws IOException {
		List<JsonObject> list = jsonStringToHashmap(file);
		this.addAll(list);
	}
	
	public JsonArray(String jsonString) throws IOException {
		List<JsonObject> list = jsonStringToHashmap(jsonString);
		this.addAll(list);
	}
	
	public JsonArray(Response response) throws IOException {
		this(response.asString());
	}
	
	public List<JsonObject> jsonStringToHashmap(File file) throws IOException {  
	    ObjectMapper mapper = new ObjectMapper();
	    TypeReference<List<JsonObject>> typeRef = new TypeReference<List<JsonObject>>() {};
	    List<JsonObject> map = mapper.readValue(file, typeRef); 
	    return map;
	} 
	
	public List<JsonObject> jsonStringToHashmap(String jsonString) throws IOException {  
		ObjectMapper mapper = new ObjectMapper();
	    TypeReference<List<JsonObject>> typeRef = new TypeReference<List<JsonObject>>() {};
	    List<JsonObject> map = mapper.readValue(jsonString, typeRef); 
	    return map;
	}
	
	@Override
	public String toString() {
		ObjectMapper mapper = new ObjectMapper();
		String json = null;
		try {
			json = mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return json;
	}

	public ArrayList<Object> get(String path) {
		ArrayList<Object> values = new ArrayList<Object>();
		for(JsonObject jsonObject: this) {
			values.add(jsonObject.get(path));
		}
		return values;
	}
	
	public void prettyPrint() throws JsonProcessingException {
		System.out.println(getPrettyPrint());
	}
	
	public String getPrettyPrint() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
	}	
}
