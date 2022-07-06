package com.chimpcentral.comparison;

import java.io.IOException;

import com.chimpcentral.comparison.Differences.ResultType;
import com.chimpcentral.jsons.JSONObject;

public class JsonComparison {

	public enum WhichValue {
		jsonpath, left, right, result, resultType
	}
	
	JSONObject leftJsonObject = null;
	JSONObject rightJsonObject = null;
	Compare compare = null;
	Differences difference = new Differences();

	public JsonComparison(JSONObject leftJsonObject, JSONObject rightJsonObject, boolean compareDatatypes) {
		this.leftJsonObject = leftJsonObject;
		this.rightJsonObject = rightJsonObject;
		compare = new Compare(leftJsonObject, rightJsonObject, compareDatatypes, difference);
	}
	
	public FindProperties find() {
		return new FindProperties();
	}
	
	public class FindProperties {
		
		public Action mismatchingProperties() {
			compare.setFindMismatchingProperties(ResultType.entriesMismatching);
			return new Action();
		}
		
		public Action matchingProperties() {
			compare.setFindMismatchingProperties(ResultType.entiresMatching);
			return new Action();
		}
		
		public Action missingPropertiesFromLeft() {
			compare.setFindMismatchingProperties(ResultType.entriesMissingFromLeft);
			return new Action();
		}
		
		public Action missingPropertiesFromRight() {
			compare.setFindMismatchingProperties(ResultType.entriesMissingFromRight);
			return new Action();
		}
		
		public Action allProperties() {
			compare.setFindMismatchingProperties(ResultType.entriesMismatching);
			compare.setFindMismatchingProperties(ResultType.entiresMatching);
			compare.setFindMismatchingProperties(ResultType.entriesMissingFromLeft);
			compare.setFindMismatchingProperties(ResultType.entriesMissingFromRight);
			return new Action();
		}
	}
	
	public class Action {
		
		public FindProperties find() {
			return new FindProperties();
		}
		
		public Differences getDifferences() throws IOException {
			compare.findAll();
			return difference;
		}
	}
}