package com.chimpcentral;

import java.io.File;
import java.io.IOException;

import com.chimpcentral.jsons.JSONArray;
import com.chimpcentral.jsons.JSONObject;

public class JsonTests {

	public static void main(String[] args) throws IOException {
		String jsonstring = null;
		jsonstring = "{" + 
				"  \"new\" : [" + 
				"    \"new1\"," +
				"  	 \"videoFormat\"," + 
				"    \"language\"," + 
				"    [" +
				"    \"test1\","  +
				"    \"test2\""  +
				"    ]"+
				"  ]}";
		jsonstring = "{" + 
				"  \"new\" : {" + 
				"    \"new1\" : \"new.new1value\"" + 
				"  }," + 
				"  \"videoFormat\" : \"1080i 50\"," + 
				"  \"language\" : \"Albanian\"," + 
				"  \"creditStatus\" : \"None\"," + 
				"  \"tC.Type\" : \"EBU\"," + 
				"  \"segments\" : [ {" + 
				"    \"start\" : \"00:00:00:00\"," + 
				"    \"end\" : \"00:01:01:01\"" + 
				"  } ]," + 
				"  \"audioTracks\" : [ {" + 
				"    \"code\" : \"FML\"," + 
				"    \"language\" : \"Albanian\"" + 
				"  }, {" + 
				"    \"code\" : \"FMR\"," + 
				"    \"language\" : \"Albanian\"" + 
				"  } ]," + 
				"  \"onpremLocation\" : \"DNAP\"," + 
				"  \"caseIdentifier\" : \"DFA/874152C\"," + 
				"  \"deliverableType\" : \"Program Master File\"," + 
				"  \"workflowType\" : \"TXMD\"," + 
				"  \"email\" : \"Sandeep_Bhumana@qadci.com\"," + 
				"  \"originalFilename\" : \"25fps_small file_162573_008_1862317_2AUD.mov\"" + 
				"}" + 
				"";
		System.out.println("json string: " + jsonstring);
		JSONObject jsonStrObj = new JSONObject(jsonstring);
		System.out.println("json object: " + jsonStrObj);
		System.out.println("get inner item: " + jsonStrObj.get("new[3][0]"));
		Object value;
		value = jsonStrObj.get("segments[0].end");
		System.out.println(value);
		value = jsonStrObj.get("'new'.'new1'");
		System.out.println(value);
		value = jsonStrObj.get("'tC.Type'");
		System.out.println(value);
		value = jsonStrObj.get("segments[0].end");
		System.out.println(value);
		value = jsonStrObj.get("new.new1");
		System.out.println(value);
		value = jsonStrObj.get("tC.Type");
		System.out.println(value);
		jsonStrObj.add("new.new2", "new1.new2.value");
		System.out.println(jsonStrObj.get("new.new2"));
		jsonStrObj.add("'new'.'new2.part1'", "new.new2.par1.value");
		System.out.println(jsonStrObj.get("'new'.'new2.part1'"));
		System.out.println(jsonStrObj.containsKey("new.new1"));
		System.out.println(jsonStrObj.containsKey("new.new2"));
		System.out.println(jsonStrObj.containsKey("new.neww"));
		System.out.println(jsonStrObj.containsKey("'new'.'new2.part1'"));
		System.out.println(jsonStrObj.containsKey("'new'.'new2.part2'"));
		System.out.println(jsonStrObj.delete("workflowType"));
		jsonStrObj.delete("'new'.'new2.part1'");
		System.out.println(jsonStrObj.get("audioTracks"));
		jsonStrObj.add("nullvalue", new JSONObject());
		jsonStrObj.add("'nullvalue'.'null1.null1'", null);
//		jsonStrObj.prettyPrint();
		System.out.println(jsonStrObj.contains("'nullvalue'.'null1.null1'"));
		System.out.println(jsonStrObj.contains("'nullvalue'"));
		System.out.println(jsonStrObj.contains("notthere.ss"));
		System.out.println(jsonStrObj.contains("'notthere'"));
		
		

		
		String filePath = System.getProperty("user.dir")+"/src/main/resources/jsonArray.json";
		File file = new File(filePath);
		JSONArray jsonArray = new JSONArray(file);
//		jsonArray.prettyPrint();
		System.out.println(jsonArray.get("phone[1].value"));
	}
	
}
