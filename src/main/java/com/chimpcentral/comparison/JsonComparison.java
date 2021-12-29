package com.chimpcentral.comparison;

import java.io.IOException;

import com.chimpcentral.comparison.Differences.ResultType;
import com.chimpcentral.jsons.JsonObject;
import com.fasterxml.jackson.core.JsonProcessingException;

public class JsonComparison {

	public enum WhichValue {
		jsonpath, left, right, result, resultType
	}
	
	JsonObject leftJsonObject = null;
	JsonObject rightJsonObject = null;
	Compare compare = null;
	Differences difference = new Differences();

	public JsonComparison(JsonObject leftJsonObject, JsonObject rightJsonObject, boolean compareDatatypes) {
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
		
		public Differences getResult() throws JsonProcessingException, IOException {
			compare.findAll();
			return difference;
		}
	}
}