package com.chimpcentral.jsons;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtils {

	private static final TypeReference<?> MAP_TYPE_REFERENCE = new TypeReference<HashMap<Object, Object>>() {};
	private static final TypeReference<?> LIST_TYPE_REFERENCE = new TypeReference<List<Object>>() {};

	private JSONUtils() {
	}
	
	public static Map<Object, Object> convertJSONFileToHashMap(File jsonFile) throws IOException { 
	    return new ObjectMapper().readValue(jsonFile, MAP_TYPE_REFERENCE);
	} 
	
	public static Map<Object, Object> convertJSONStringToHashmap(String jsonString) throws IOException {
	    return new ObjectMapper().readValue(jsonString, MAP_TYPE_REFERENCE);
	}
	
	public static String convertMapToJSONString(Map<Object, Object> map) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(map);
	}
	
	public static List<Object> convertJSONFileToList(File file) throws IOException {  
	    return new ObjectMapper().readValue(file, LIST_TYPE_REFERENCE);
	} 
	
	public static List<Object> convertJSONStringToList(String jsonString) throws IOException {  
	    return new ObjectMapper().readValue(jsonString, LIST_TYPE_REFERENCE);
	}
	
	public static String convertListToJSONString(List<Object> list) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(list);
	}
}
