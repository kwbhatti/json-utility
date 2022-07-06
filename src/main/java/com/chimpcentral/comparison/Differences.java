package com.chimpcentral.comparison;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.chimpcentral.comparison.JsonComparison.WhichValue;
import com.chimpcentral.data.Data;

public class Differences extends ArrayList<HashMap<WhichValue, Object>> {

	private static final long serialVersionUID = -7375114770693803872L;
	
	public enum ResultType {
		entriesMismatching,
		entiresMatching,
		entriesMissingFromLeft,
		entriesMissingFromRight
	}
	
	void add(String property, Object leftMapValue, Object rightMapValue, ResultType resultType) {
		boolean result = resultType == ResultType.entiresMatching ? true : false;
		HashMap<WhichValue, Object> valueMap = new HashMap<>();
		valueMap.put(WhichValue.jsonpath, property);
		valueMap.put(WhichValue.left, leftMapValue);
		valueMap.put(WhichValue.right, rightMapValue);
		valueMap.put(WhichValue.result, result);
		valueMap.put(WhichValue.resultType, resultType);
		this.add(valueMap);
	}
	
	public int getTotalCount() {
		return this.size();
	}
	
	private boolean getResult(Map<WhichValue, Object> difference) {
		return new Data(difference.get(WhichValue.result)).convert().toBoolean();
	}
	
	public int getFailedCount() {
		int count = 0;
		for (Map<WhichValue, Object> difference: this) {
			if (!getResult(difference)) count++;
		}
		return count;
	}
	
	public int getPassedCount() {
		int count = 0;
		for (Map<WhichValue, Object> difference: this) {
			if (getResult(difference)) count++;
		}
		return count;
	}
}
