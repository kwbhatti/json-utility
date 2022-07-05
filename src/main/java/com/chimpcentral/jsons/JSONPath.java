package com.chimpcentral.jsons;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class JSONPath {

	private JSONObject jsonObject = null;
	private JSONArray jsonArray = null;
	private String jsonpath = null;
	
	JSONPath(JSONObject jsonObject, String jsonpath) {
		this.jsonObject = jsonObject;
		this.jsonpath = jsonpath;
	}
	
	JSONPath(JSONArray jsonArray, String jsonpath) {
		this.jsonArray = jsonArray;
		this.jsonpath = jsonpath;
	}
	
	Object getFromJSONObject() {
		return get(this.jsonObject, this.jsonpath);
	}
	
	Object getFromJSONArray() {
		return get(this.jsonArray, this.jsonpath);
	}
	
	<T> JSONObject addToJSONObject(T value) {
		return add(this.jsonObject, this.jsonpath, value);
	}
	
	JSONObject deleteFromJSONObject() {
		return delete(this.jsonObject, this.jsonpath);
	}
	
	boolean containsInJSONObject() {
		return contains(jsonObject, this.jsonpath);
	}
	
	boolean isArray(Object value) {
		return value instanceof List<?>;
	}
	
	boolean isMap(Object value) {
		return value instanceof Map<?, ?>;
	}
	
	private List<?> convertToList(Object value) {
		if (value instanceof List<?>) return (List<?>) value;
		else throw new IllegalArgumentException("Value is not a list\n" + value);
	}
	
	private Map<?, ?> convertToMap(Object value) {
		if (value instanceof Map<?, ?>) return (Map<?, ?>) value;
		else throw new IllegalArgumentException("Value is not a list\n" + value);
	}
	
	private Object get(List<?> list, String jsonpath) {
		String nodepath = JSONPathUtility.getFirstNode(jsonpath);
		boolean isLastNodePath = JSONPathUtility.isLastNode(jsonpath);
		int nodeIndex = JSONPathUtility.getArrayIndexFromArrayNodepath(nodepath);
		Object nodeValue = list.get(nodeIndex);
		if (!isLastNodePath && isMap(nodeValue)) return get(convertToMap(nodeValue), JSONPathUtility.removeFirstNode(jsonpath));
		else if (!isLastNodePath && isArray(nodeValue)) return get(convertToList(nodeValue), JSONPathUtility.removeFirstNode(jsonpath));
		else return nodeValue;
	}
	
	private Object getNodeValue(Map<?, ?> map, String nodepath) {
		boolean isArrayNode = JSONPathUtility.isArrayNode(nodepath);
		Object nodeValue = null;
		if (isArrayNode) {
			int nodeIndex = JSONPathUtility.getArrayIndexFromArrayNodepath(nodepath);
			nodepath = JSONPathUtility.getNodeValueFromArrayNodepath(nodepath);
			if (map.get(nodepath) == null) return null;
			List<?> nodeValueAsList = convertToList(map.get(nodepath));
			nodeValue = nodeValueAsList.get(nodeIndex);
		} else nodeValue = map.get(nodepath);
		return nodeValue;
	}
	
	private Object get(Map<?, ?> map, String jsonpath) {
		if (jsonpath.equals("")) return map;
		if (jsonpath.startsWith(".")) jsonpath = jsonpath.replaceFirst("\\.", "");
		String nodepath = JSONPathUtility.getFirstNode(jsonpath);
		boolean isLastNode = JSONPathUtility.isLastNode(jsonpath);
		Object nodeValue = getNodeValue(map, nodepath);
		if (!isLastNode && isMap(nodeValue)) return get(convertToMap(nodeValue), JSONPathUtility.removeFirstNode(jsonpath));
		else if (!isLastNode && isArray(nodeValue)) {
			return get(convertToList(nodeValue), JSONPathUtility.removeFirstNode(jsonpath));
		} else return nodeValue;
	}
	
	private boolean contains(JSONObject jsonObject, String jsonpath) {
		boolean isParentNode = JSONPathUtility.isLastNode(jsonpath);
		if (isParentNode) {
			if (jsonpath.startsWith("'") && jsonpath.endsWith("'")) {
				jsonpath = jsonpath.substring(1, jsonpath.length() -1);
			}
			boolean doesRootNodeExists = jsonObject.containsKey(jsonpath);
			return doesRootNodeExists;
		}
		List<String> nodes = JSONPathUtility.getNodes(jsonpath);
		String searchPath = "'" + JSONPathUtility.getFirstNode(jsonpath) + "'";
		for (int i = 1; i < nodes.size(); i++) {
			searchPath = searchPath + ".'" + nodes.get(i) + "'";
			try {
				boolean doesPathExist = jsonObject.get(searchPath) != null;
				if (!doesPathExist) return false;
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}
	
	private boolean doesParentExist(JSONObject jsonObject, String jsonpath) {
		String parentPath = JSONPathUtility.removeLastNode(jsonpath);
		boolean isParentNodePathArray = JSONPathUtility.isArrayNode(parentPath);
		if (isParentNodePathArray) {
			String parentParentPath = JSONPathUtility.removeLastNode(parentPath);
			String parentNode = JSONPathUtility.getLastNode(parentPath);
			String parentNodeValue = JSONPathUtility.getNodeValueFromArrayNodepath(parentNode);
			return jsonObject.contains(parentParentPath + "." + parentNodeValue);
		}
		return jsonObject.contains(parentPath);
	}
	
	private boolean doesNodeIndexExist(String nodePath) {
		try {
			JSONPathUtility.getArrayIndexFromArrayNodepath(nodePath);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	private <V> JSONObject add(JSONObject jsonObject, String jsonpath, V value) {
		String parentPath = JSONPathUtility.removeLastNode(jsonpath);
		String nodepath = JSONPathUtility.getLastNode(jsonpath);
		boolean isParentNodePath = JSONPathUtility.isLastNode(jsonpath);
		if (!parentPath.equals("")) {
			boolean doesParentExist = doesParentExist(jsonObject, jsonpath);
			if (!doesParentExist) {
				String parentNodePath = JSONPathUtility.getLastNode(parentPath);
				boolean isParentNodePathArray = JSONPathUtility.isArrayNode(parentNodePath);
				Object parentNode = isParentNodePathArray ? new JSONArray() : new JSONObject();
				add(jsonObject, parentPath, parentNode);
				add(jsonObject, jsonpath, value);
			}
		}
		boolean isArrayNodePath = JSONPathUtility.isArrayNode(nodepath);
		if (isArrayNodePath) {
			String nodepathValue = JSONPathUtility.getNodeValueFromArrayNodepath(nodepath);
			boolean doesNodeExist = false;
			if (isParentNodePath) doesNodeExist = jsonObject.contains(nodepathValue);
			else doesNodeExist = jsonObject.contains(parentPath + "." + nodepathValue);
			if (!doesNodeExist) jsonObject.getAsObjectsMap(parentPath).put(nodepathValue, new ArrayList<>());
			else {
				if (doesNodeIndexExist(nodepath)) {
					int nodeIndex = JSONPathUtility.getArrayIndexFromArrayNodepath(nodepath);
					jsonObject.getAsObjectsList(parentPath + "." + nodepathValue).remove(nodeIndex);
					jsonObject.getAsObjectsList(parentPath + "." + nodepathValue).add(nodeIndex, value);
				} 
				else jsonObject.getAsObjectsList(parentPath + "." + nodepathValue).add(value);
			}
		} else jsonObject.getAsObjectsMap(parentPath).put(nodepath, value);
		return jsonObject;
	}
	
	private JSONObject delete(JSONObject jsonObject, String jsonpath) {
		String parentPath = JSONPathUtility.removeLastNode(jsonpath);
		String nodepath = JSONPathUtility.getLastNode(jsonpath);
		boolean isParentPathArray = JSONPathUtility.isArrayPath(parentPath);
		boolean isArrayNodePath = JSONPathUtility.isArrayNode(nodepath);
		if (isArrayNodePath) {
			jsonpath = parentPath + "." + JSONPathUtility.getNodeValueFromArrayNodepath(nodepath);
			int nodePathIndex = JSONPathUtility.getArrayIndexFromArrayNodepath(nodepath);
			if (!contains(jsonObject, jsonpath)) {
				return jsonObject;
			}
			if (isParentPathArray) {
				List<Object> nodeArray = jsonObject.getAsObjectsList(jsonpath);
				nodeArray.remove(nodePathIndex);
				add(jsonObject, jsonpath, nodeArray);
			} else jsonObject.getAsObjectsList(jsonpath).remove(nodePathIndex);
		} else {
			if (!contains(jsonObject, jsonpath)) return jsonObject;
			if (isParentPathArray) {
				JSONObject nodeValue = jsonObject.getAsJSONObject(parentPath);
				nodeValue.remove(nodepath);
				add(jsonObject, parentPath, nodeValue);
			} else jsonObject.getAsObjectsMap(parentPath).remove(nodepath);
		}
		return jsonObject;
	}
}
