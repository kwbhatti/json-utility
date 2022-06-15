package com.chimpcentral;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.chimpcentral.jsons.JSONArray;
import com.chimpcentral.jsons.JSONObject;

public class JsonTests {

	private static final String USER_DIR = System.getProperty("user.dir");
	private static final String TEST_DIR = USER_DIR + "/src/test/resources/com/chimpcentral/JsonTests";
	private static final String EXAMPLE1_FILE_PATH = TEST_DIR + "/example1.json";
	private static final String EXAMPLE2_FILE_PATH = TEST_DIR + "/example2.json";
	private static final File EXAMPLE1_FILE = new File(EXAMPLE1_FILE_PATH);
	private static final File EXAMPLE2_FILE = new File(EXAMPLE2_FILE_PATH);
	
	@Test
	private void JSONObjectTest() {
		try {
			JSONObject jsonObject = new JSONObject(EXAMPLE1_FILE);
			Assert.assertTrue(jsonObject.getAsString("address.street1[3]").equals("203"));
			Assert.assertTrue(jsonObject.getAsString("'array.object.not.in.seq'.[1].prop22").equals("prop22value"));
			Assert.assertTrue(jsonObject.getAsList("phones").size() == 3);
			jsonObject.add("phones[]", "new phone");
			Assert.assertTrue(jsonObject.getAsList("phones").size() == 4);
			Assert.assertTrue(!jsonObject.contains("newnode1"));
			Assert.assertTrue(!jsonObject.contains("newnode1.newnode2.newnode3.newnode4"));
			jsonObject.add("newnode1.newnode2.newnode3.newnode4[]", "foo-bar");
			Assert.assertTrue(jsonObject.contains("newnode1"));
			Assert.assertTrue(jsonObject.contains("newnode1.newnode2.newnode3.newnode4"));
			Assert.assertTrue(jsonObject.get("newnode1.newnode2.newnode3.newnode4[0]").equals("foo-bar"));
			Assert.assertTrue(jsonObject.get("age").getClass().getSimpleName().equals("String"));
			Assert.assertTrue(jsonObject.getAsString("age").equals("38"));
			Assert.assertTrue(jsonObject.getAsInt("age") == 38);
			Assert.assertTrue(jsonObject.contains("array.arraytest[1].two"));
			jsonObject.delete("array.arraytest[1].two");
			Assert.assertTrue(!jsonObject.contains("array.arraytest[1].two"));
			Assert.assertTrue(jsonObject.getAsList("address.street1").size() == 6);
			jsonObject.delete("address.street1[0]");
			Assert.assertTrue(jsonObject.getAsList("address.street1").size() == 5);
			jsonObject.delete("address.street1");
			Assert.assertTrue(jsonObject.getAsList("street") == null);
			jsonObject.delete("address").add("address", new JSONObject().add("street", "111 Foo Dr").add("zip", 1111).add("isSingleFamily", true));
			Assert.assertTrue(jsonObject.get("address.street").equals("111 Foo Dr"));
			Assert.assertTrue(jsonObject.getAsBoolean("address.isSingleFamily") == true);
			System.out.println(jsonObject.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	private void JSONArrayTest() {
		try {
			JSONArray jsonArray = new JSONArray(EXAMPLE2_FILE);
			System.out.println(jsonArray);
			Assert.assertTrue(jsonArray.get("[0].node1.node2.node3.node4[0].node5[0].node6").equals("node6 value"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
