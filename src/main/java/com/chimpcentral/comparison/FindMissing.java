package com.chimpcentral.comparison;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.chimpcentral.comparison.Differences.ResultType;
import com.chimpcentral.comparison.JsonComparison.WhichValue;
import com.chimpcentral.jsons.JSONObject;

class FindMissing extends Find {

	private JSONObject firstJsonObject = null;
	private JSONObject secondJsonObject = null;
	boolean compareDatatypes = false;
	private ResultType resultType = null;
	private Differences differences = null;
	
	FindMissing(JSONObject firstJsonObject, JSONObject secondJsonObject, boolean compareDatatypes, ResultType resultType) {
		this.firstJsonObject = firstJsonObject;
		this.secondJsonObject = secondJsonObject;
		this.compareDatatypes = compareDatatypes;
		this.resultType = resultType;
		differences = new Differences();
		processMap(".");
	}
	
	void processMap(String jsonpath) {
		Iterator<Object> iterator = firstJsonObject.getAsObjectsMap(jsonpath).keySet().iterator();
		while (iterator.hasNext()) {
			String currentJsonpath = getCurrentJsonpath(jsonpath, iterator.next());
			if (!this.compareDatatypes) {
				if (firstJsonObject.get(currentJsonpath) != null && firstJsonObject.get(currentJsonpath).equals("null")) {
					firstJsonObject.add(currentJsonpath, null);
				}
				if (secondJsonObject.contains(currentJsonpath) && secondJsonObject.get(currentJsonpath) != null && secondJsonObject.get(currentJsonpath).equals("null")) {
					secondJsonObject.add(currentJsonpath, null);
				}
			}
			if (!secondJsonObject.contains(currentJsonpath) && firstJsonObject.get(currentJsonpath) != null) {
				differences.add(currentJsonpath, firstJsonObject.get(currentJsonpath), secondJsonObject.get(currentJsonpath), resultType);
				iterator.remove();
			} else if (!secondJsonObject.contains(currentJsonpath)) {
				if (compareDatatypes && firstJsonObject.get(currentJsonpath) == null) {
					differences.add(currentJsonpath, firstJsonObject.get(currentJsonpath), secondJsonObject.get(currentJsonpath), ResultType.entiresMatching);
				} else if (!compareDatatypes && String.valueOf(firstJsonObject.get(currentJsonpath)).equals("null")) {
					differences.add(currentJsonpath, firstJsonObject.get(currentJsonpath), secondJsonObject.get(currentJsonpath), ResultType.entiresMatching);
				}
			} else if (secondJsonObject.get(currentJsonpath) == null) {
			} else if (!secondJsonObject.get(currentJsonpath).equals(firstJsonObject.get(currentJsonpath))) {
				if (objectIsMap(firstJsonObject.get(currentJsonpath))) {
					processMap(currentJsonpath);
				} else if (objectIsList(firstJsonObject.get(currentJsonpath))) {
					processList(currentJsonpath);
				}
			}
		}
	}
	
	public <T> List<String> convertListToListOfStrings(List<T> list) {
		return list.stream().map(e -> String.valueOf(e)).collect(Collectors.toList());	
	}
	
	void processList(String jsonpath) {
		firstJsonObject.add(jsonpath, sortList(firstJsonObject.getAsList(jsonpath)));
		secondJsonObject.add(jsonpath, sortList(secondJsonObject.getAsList(jsonpath)));
		Iterator<?> iterator = firstJsonObject.getAsList(jsonpath).iterator();
		List<?> secondJsonObjectList = secondJsonObject.getAsList(jsonpath);
		while (iterator.hasNext()) {
			Object value = iterator.next();
			boolean doesValueExistsInSecondJsonObject = false;
			if(compareDatatypes) doesValueExistsInSecondJsonObject = secondJsonObjectList.contains(value);
			else {
				doesValueExistsInSecondJsonObject = convertListToListOfStrings(secondJsonObjectList).contains(String.valueOf(value));
			}
			boolean doesValueHaveDuplicates = findDuplicates(value, jsonpath);
			if (!doesValueExistsInSecondJsonObject || doesValueHaveDuplicates) {
				differences.add(jsonpath, value, null, resultType);
				iterator.remove();
			}
		}
	}
	
	private boolean findDuplicates(Object value, String jsonpath) {
		List<?> firstObjectList = firstJsonObject.getAsList(jsonpath);
		List<?> secondObjectList = secondJsonObject.getAsList(jsonpath);
		int firstObjectNumberOfReferences = findNumOfReferences(value, firstObjectList);
		int secondObjectNumberOfReferences = findNumOfReferences(value, secondObjectList);
		if(firstObjectNumberOfReferences > secondObjectNumberOfReferences) {
			return true;
		}
		return false;
	}
	
	private int findNumOfReferences(Object value, List<?> list) {
		int numOfReferences = 0;
		for(Object item: list) {
			if(item.equals(value)) {
				numOfReferences += 1;
			}
		}
		return numOfReferences;
	}
	
	private List<?> sortList(List<?> list) {
		Collections.sort(list, (object1, object2) -> object1.toString().compareTo(object2.toString()));
		return list;
	}
	
	public JSONObject getUpdatedJsonObject() {
		return firstJsonObject;
	}
	
	public Differences getMissingProperties() {
		if (resultType == ResultType.entriesMissingFromLeft) {
			for (Map<WhichValue, Object> difference: this.differences) {
				Object leftValue = difference.get(WhichValue.right);
				Object rightValue = difference.get(WhichValue.left);
				difference.put(WhichValue.left, leftValue);
				difference.put(WhichValue.right, rightValue);
			}
		}
		return differences;
	}
}
