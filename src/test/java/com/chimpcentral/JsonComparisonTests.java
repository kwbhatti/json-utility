//package com.chimpcentral;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Map;
//
//import com.chimpcentral.comparison.Differences;
//import com.chimpcentral.comparison.JsonComparison.WhichValue;
//import com.chimpcentral.jsons.JsonObject;
//import com.fasterxml.jackson.core.JsonProcessingException;
//
//public class JsonComparisonTests {
//
//	public static void main(String[] args) throws JsonProcessingException, IOException {
//		String testDataDir = System.getProperty("user.dir") + "/src/test/resources/com/chimpcentral";
//		JsonObject leftJsonObj = new JsonObject(new File(testDataDir + "/map1.json"));	
//		JsonObject rightJsonObj = new JsonObject(new File(testDataDir + "/map2.json"));
//		Differences differences = leftJsonObj.compareTo(rightJsonObj, false)
//									.find().allProperties()
//									.getResult();
//		
//		for (Map<WhichValue, Object> property: differences) {
//			String jsonpath = (String) property.get(WhichValue.jsonpath);
//			Object leftMapValue = property.get(WhichValue.left);
//			Object rightMapValue = property.get(WhichValue.right);
//			String whichValue = property.get(WhichValue.resultType).toString();
//			System.out.println("*************************************************");
//			System.out.println(
//					"Property: " + jsonpath
//					+ "\nleftMapValue: " + leftMapValue
//					+ "\nrightMapValue: " + rightMapValue
//					+ "\nwhichValue: " + whichValue
//					);
//		}
//	}
//}
