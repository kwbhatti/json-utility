package com.chimpcentral.comparison;

import java.io.IOException;

import com.chimpcentral.comparison.Differences.ResultType;
import com.chimpcentral.jsons.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;

class Compare {

	JSONObject leftJsonObject = null;
	JSONObject rightJsonObject = null;
	
	boolean findMismatchingProperties = false;
	boolean findMatchingProperties = false;
	boolean findMissingPropertiesFromLeft = false;
	boolean findMissingPropertiesFromRight = false;
	boolean compareDatatypes = false;
	Differences difference = null;
	
	Compare(JSONObject leftJsonObject, JSONObject rightJsonObject, boolean compareDatatypes, Differences difference) {
		this.leftJsonObject = leftJsonObject;
		this.rightJsonObject = rightJsonObject;
		this.compareDatatypes = compareDatatypes;
		this.difference = difference;
	}
	
	public void setFindMismatchingProperties(ResultType resultType) {
		switch(resultType) {
		case entiresMatching:
			this.findMatchingProperties = true;
			break;
		case entriesMismatching:
			this.findMismatchingProperties = true;
			break;
		case entriesMissingFromLeft:
			this.findMissingPropertiesFromLeft = true;
			break;
		case entriesMissingFromRight:
			this.findMissingPropertiesFromRight = true;
			break;
		}
	}
	
	private void findMissingPropertiesFromRight() {
		FindMissing findMissingProperties = new FindMissing(leftJsonObject, rightJsonObject, compareDatatypes, ResultType.entriesMissingFromRight);
		leftJsonObject = findMissingProperties.getUpdatedJsonObject();
		Differences missingPropertiesFromRight = findMissingProperties.getMissingProperties();
		if (this.findMissingPropertiesFromRight) difference.addAll(missingPropertiesFromRight);

	}
	
	private void findMissingPropertiesFromLeft() {
		FindMissing findMissingProperties = new FindMissing(rightJsonObject, leftJsonObject, compareDatatypes, ResultType.entriesMissingFromLeft);
		rightJsonObject = findMissingProperties.getUpdatedJsonObject();
		Differences missingPropertiesFromLeft = findMissingProperties.getMissingProperties();
		if (this.findMissingPropertiesFromLeft) difference.addAll(missingPropertiesFromLeft);
	}
	
	private void findMismatchingProperties() {
		FindDifference matchProperties = new FindDifference(leftJsonObject, rightJsonObject, compareDatatypes);
		if (this.findMatchingProperties) difference.addAll(matchProperties.getMatchingProperties());
		if (this.findMismatchingProperties) difference.addAll(matchProperties.getMismatchingProperties());
	}
	
	public void findAll() throws JsonProcessingException, IOException {
		findMissingPropertiesFromLeft();
		findMissingPropertiesFromRight();
		if (this.findMatchingProperties || this.findMismatchingProperties) findMismatchingProperties();
	}
}