package com.chimpcentral.comparison;

import java.util.Iterator;
import java.util.List;

import com.chimpcentral.comparison.Differences.ResultType;
import com.chimpcentral.data.Data;
import com.chimpcentral.jsons.JSONObject;

class FindDifference extends Find {

	JSONObject firstJsonObject = null;
	JSONObject secondJsonObject = null;
	boolean compareDatatypes = false;
	Differences matchingProperties = null;
	Differences misMatchingProperties = null;

	public FindDifference(JSONObject leftJsonObject, JSONObject rightJsonObject, boolean compareDatatypes) {
		this.firstJsonObject = leftJsonObject;
		this.secondJsonObject = rightJsonObject;
		this.compareDatatypes = compareDatatypes;
		matchingProperties = new Differences();
		misMatchingProperties = new Differences();
		processMap(".");
	}
	
	void processMap(String jsonpath) {
		Iterator<Object> iterator = firstJsonObject.getAsObjectsMap(jsonpath).keySet().iterator();
		while (iterator.hasNext()) {
			String currentJsonpath = getCurrentJsonpath(jsonpath, iterator.next());
			if (objectIsMap(firstJsonObject.get(currentJsonpath))) {
				processMap(currentJsonpath);
			} else if (objectIsList(firstJsonObject.get(currentJsonpath))) {
				processList(currentJsonpath);
			} else {
				processValue(currentJsonpath);
			}
		}
	}
	
	void processValue(String jsonpath) {
		Object leftValue = firstJsonObject.get(jsonpath);
		Object rightValue = secondJsonObject.get(jsonpath);
		boolean result = new Data(leftValue).compare(rightValue, compareDatatypes);
		if (result) {
			matchingProperties.add(jsonpath, leftValue, rightValue, ResultType.entiresMatching);
		} else {
			misMatchingProperties.add(jsonpath, leftValue, rightValue, ResultType.entriesMismatching);
		}
	}
	
	void processList(String jsonpath) {
		List<?> list = firstJsonObject.getAsList(jsonpath);
		for (int i = 0; i < list.size(); i++) {
			String currentJsonpath = jsonpath.substring(0, jsonpath.length() -1) + "[" + i + "]'";
			if (objectIsMap(list.get(i))) {
				processMap(currentJsonpath);
			} else {
				processValue(currentJsonpath);
			}
		}
	}
	
	public Differences getMatchingProperties() {
		return matchingProperties;
	}
	
	public Differences getMismatchingProperties() {
		return misMatchingProperties;
	}
}
