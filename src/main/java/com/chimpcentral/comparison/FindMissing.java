package com.chimpcentral.comparison;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.chimpcentral.comparison.Differences.ResultType.*;

import com.chimpcentral.comparison.Differences.ResultType;
import com.chimpcentral.comparison.JsonComparison.WhichValue;
import com.chimpcentral.jsons.JSONObject;

class FindMissing extends Find {

	private JSONObject firstJsonObject = null;
	private JSONObject secondJsonObject = null;
	boolean compareDatatypes = false;
	private ResultType resultType = null;
	private Differences differences = null;
	
	FindMissing(JSONObject firstJsonObject, JSONObject secondJsonObject, boolean compareDatatypes, ResultType resultType) throws IOException {
		this.firstJsonObject = firstJsonObject;
		this.secondJsonObject = secondJsonObject;
		this.compareDatatypes = compareDatatypes;
		this.resultType = resultType;
		differences = new Differences();
		processMap(".");
	}
	
	void processMap(String jsonpath) throws IOException {
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
				if ((compareDatatypes && firstJsonObject.get(currentJsonpath) == null) || (!compareDatatypes && String.valueOf(firstJsonObject.get(currentJsonpath)).equals("null"))) {
					differences.add(currentJsonpath, firstJsonObject.get(currentJsonpath), secondJsonObject.get(currentJsonpath), ResultType.entiresMatching);
				}
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
		return list.stream().map(String::valueOf).collect(Collectors.toList());	
	}
	
	void processListOfNonJsons(String jsonpath) {
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
	
	void processListOfJsons(String jsonpath) {
		List<?> firstList = firstJsonObject.getAsList(jsonpath);
		List<?> secondList = secondJsonObject.getAsList(jsonpath);
		List<Map<Integer, Integer>> scoreMaps = new ArrayList<>();
		for (Object firstObject: firstList) {
			Map<Integer, Integer> scoreMap = new HashMap<>();
			Map<?, ?> firstObjectMap = (Map<?, ?>) firstObject;
			int secondMapIndex = 0;
			for (Object secondObject: secondList) {
				int score = 0;
				Map<?, ?> secondObjectMap = (Map<?, ?>) secondObject;
				List<?> secondObjectMapKeys = new ArrayList<>(secondObjectMap.keySet());
				for (Object firstObjectMapKey: firstObjectMap.keySet()) {
					if (!secondObjectMapKeys.contains(firstObjectMapKey)) {
						score--;
					} else {
						String key = String.valueOf(firstObjectMapKey);
						String firstObjectMapValue = String.valueOf(firstObjectMap.get(key));
						String secondObjectMapValue = String.valueOf(secondObjectMap.get(key));
						if (key.equals("id")) {
							if (firstObjectMapValue.equals(secondObjectMapValue)) score += 14;
							else score -= 14;
						} else if (key.contains("id") || key.equals("name")) {
							if (firstObjectMapValue.equals(secondObjectMapValue)) score += 13;
							else score -= 13;
						} else if (key.contains("name") || key.equals("type") || key.equals("code") || key.equals("cd")) {
							if (firstObjectMapValue.equals(secondObjectMapValue)) score += 12;
							else score -= 12;
						} else if (key.contains("type") || key.contains("code") || key.contains("cd")) {
							if (firstObjectMapValue.equals(secondObjectMapValue)) score += 11;
							else score -= 11;
						} else {
							if (firstObjectMapValue.equals(secondObjectMapValue)) score += 10;
							else score -= 10;
						}
					}
				}
				scoreMap.put(secondMapIndex, score);
				secondMapIndex++;
			}
			scoreMaps.add(scoreMap);
		}		
		int firstListIndex = 0;		
		Map<Integer, Integer> mappingMap = new HashMap<>();
		for (Map<Integer, Integer> scoreMap: scoreMaps) {
			int bestMatchScore = Collections.max(scoreMap.values());
			Integer bestMatchIndex = 0;
			for (Map.Entry<Integer, Integer> scoreEntry: scoreMap.entrySet()) {
				if (scoreEntry.getValue() == bestMatchScore) bestMatchIndex = scoreEntry.getKey();
			}
			Map<?, ?> firstObjectMap = (Map<?, ?>) firstList.get(firstListIndex);
			int totalScore = firstObjectMap.keySet().size() * 10;
			int matchPercentage = bestMatchScore * 100 / totalScore;			
			if (matchPercentage > 60) {
				mappingMap.put(firstListIndex, bestMatchIndex);
			}			
			firstListIndex++;
		}
		List<Object> firstListJsonObjects = new ArrayList<>();
		List<Object> secondListJsonObjects = new ArrayList<>();
		for (Map.Entry<Integer, Integer> mappingEntry: mappingMap.entrySet()) {
			firstListJsonObjects.add(firstJsonObject.get(jsonpath + "[" + mappingEntry.getKey() + "]"));
			secondListJsonObjects.add(secondJsonObject.get(jsonpath + "[" + mappingEntry.getValue() + "]"));
		}
		for (int i = 0; i < firstList.size(); i++) {
			String arrayPath = jsonpath.substring(0, jsonpath.length() - 1) + "[" + i + "]'";
			if (!mappingMap.keySet().contains(i)) {
				if (resultType == entriesMissingFromLeft) {
					differences.add(jsonpath, null, firstJsonObject.getAsJSONObject(arrayPath), entriesMissingFromLeft);
				} else if (resultType == entriesMissingFromRight) {
					differences.add(jsonpath, secondJsonObject.getAsJSONObject(arrayPath), null, entriesMissingFromRight);
				}
			}
		}
		for (int i = 0; i < secondList.size(); i++) {
			String arrayPath = jsonpath.substring(0, jsonpath.length() - 1) + "[" + i + "]'";
			if (!mappingMap.values().contains(i)) {
				if (resultType == ResultType.entriesMissingFromLeft) {
					differences.add(jsonpath, secondJsonObject.getAsJSONObject(arrayPath), null, entriesMissingFromRight);
				} else if (resultType == ResultType.entriesMissingFromRight) {
					differences.add(jsonpath, null, firstJsonObject.getAsJSONObject(arrayPath), entriesMissingFromLeft);
				}
			}
		}
		firstJsonObject.add(jsonpath, firstListJsonObjects);
		secondJsonObject.add(jsonpath, secondListJsonObjects);
	}
	
	void processList(String jsonpath) {
		Object fistObjectOfList = firstJsonObject.getAsList(jsonpath).get(0);
		boolean isListOfJsonObjects = objectIsMap(fistObjectOfList);
		if (!isListOfJsonObjects) processListOfNonJsons(jsonpath);
		else processListOfJsons(jsonpath);
	}
	
	
	
	private boolean findDuplicates(Object value, String jsonpath) {
		List<?> firstObjectList = firstJsonObject.getAsList(jsonpath);
		List<?> secondObjectList = secondJsonObject.getAsList(jsonpath);
		int firstObjectNumberOfReferences = findNumOfReferences(value, firstObjectList);
		int secondObjectNumberOfReferences = findNumOfReferences(value, secondObjectList);
		return firstObjectNumberOfReferences > secondObjectNumberOfReferences;
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
	
	public JSONObject getFirstUpdatedJsonObject() {
		return firstJsonObject;
	}
	
	public JSONObject getSecondUpdatedJsonObject() {
		return secondJsonObject;
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
