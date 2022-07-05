package com.chimpcentral;

import java.io.File;
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.chimpcentral.jsons.JSONArray;
import com.chimpcentral.jsons.JSONObject;
import com.chimpcentral.jsons.JSONPathUtility;

public class JsonTests {

	private static final String USER_DIR = System.getProperty("user.dir");
	private static final String TEST_DIR = USER_DIR + "/src/test/resources/com/chimpcentral/JsonTests";
	private static final String EXAMPLE1_FILE_PATH = TEST_DIR + "/example1.json";
	private static final String EXAMPLE2_FILE_PATH = TEST_DIR + "/example2.json";
	private static final String REQUEST1_FILE_PATH = TEST_DIR + "/request1.json";
	private static final String REQUEST2_FILE_PATH = TEST_DIR + "/request2.json";
	private static final String REQUEST3_FILE_PATH = TEST_DIR + "/request3.json";
	private static final String REQUEST4_FILE_PATH = TEST_DIR + "/request4.json";
	private static final File EXAMPLE1_FILE = new File(EXAMPLE1_FILE_PATH);
	private static final File EXAMPLE2_FILE = new File(EXAMPLE2_FILE_PATH);
	private static final File REQUEST1_FILE = new File(REQUEST1_FILE_PATH);
	private static final File REQUEST2_FILE = new File(REQUEST2_FILE_PATH);
	private static final File REQUEST3_FILE = new File(REQUEST3_FILE_PATH);
	private static final File REQUEST4_FILE = new File(REQUEST4_FILE_PATH);
	
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
	
	@Test
	private void JSONTest() throws IOException {
		JSONObject jsonObject = new JSONObject(REQUEST1_FILE);
		jsonObject.delete("jsonchild1.jsonchild2.jsonchildp");
		System.out.println(jsonObject);
		Assert.assertTrue(jsonObject.getAsList("phones").size() == 3);
		System.out.println(jsonObject.get("phones"));
		jsonObject.add("phones[]", "new phone");
		System.out.println(jsonObject);
		Assert.assertTrue(jsonObject.getAsList("phones").size() == 4);
		System.out.println(jsonObject.get("addition"));
		System.out.println(jsonObject.get("arrayarray2[0].[0].obj1.prop2"));
		System.out.println(jsonObject.get("array2.arraytest[1].two"));
		System.out.println(jsonObject.get("phones[2].type"));
		System.out.println(jsonObject.get("address.street1[3]"));
		System.out.println(jsonObject.contains("address.street1"));
		System.out.println(jsonObject.contains("address.street2"));
		System.out.println(jsonObject.contains("somewrongvalue"));
		System.out.println(jsonObject.contains("arryobjectnotinseq[1].prop22"));
		System.out.println(jsonObject.contains("empty"));
		System.out.println(jsonObject.contains("array2.arraytest[1].two"));
		jsonObject.add("node1.node2.node3[]", new JSONArray()).add("node1.node2.node3[0]", new JSONObject().add("node4", "foo"));
		jsonObject.add("empty", "foo");
		jsonObject.add("phones[]", new JSONObject().add("type", "new type").add("number", 222222));
		jsonObject.delete("address.street1[0]");
		jsonObject.delete("address.addressnotinrequest2");
		jsonObject.delete("node1.node2");
		jsonObject.delete("arrayarray[0]");
		jsonObject.delete("array2.arraytest[0].one[1]");
		JSONObject newJsonObject = jsonObject.clone();
		jsonObject.containsKey("");
		System.out.println(newJsonObject);
		System.out.println(jsonObject);
	}
	
	@Test
	private void JSONPathUtilTest() {
		String firstnode = JSONPathUtility.getFirstNode("'so.me'.'so.me'.some.'some'.some.some");
		System.out.println(firstnode);
		System.out.println(JSONPathUtility.removeFirstNode("some.'so.me'.some.'some'.some.some"));
		System.out.println(JSONPathUtility.isLastNode("some"));
		System.out.println(JSONPathUtility.getNodes("some1.'so.me2'.some3.'so.me4[5]'.some5.some6"));
		System.out.println(JSONPathUtility.getNodeValueFromArrayNodepath("some[9]"));
		System.out.println(JSONPathUtility.getArrayIndexFromArrayNodepath("some[9]"));
		System.out.println(JSONPathUtility.getLastNode("blah.sdfs.'sfd'.fsf.sdf[0].so"));
		System.out.println(JSONPathUtility.removeLastNode("blah.'s.dfs[7]'.'sfd'.fsf.sdf[0].so"));
		System.out.println("nodes are " + JSONPathUtility.getNodes("'jsonchild1'.'jsonchild2'"));
	}
	
	@Test
	private void testnow() throws IOException {
		JSONObject jsonObject = new JSONObject(REQUEST4_FILE);
		jsonObject.add("'some.one'.'some.two'.'some.three'.'some.four'", "somepathvalue");
		Assert.assertTrue(jsonObject.contains("'some.one'.'some.two'.'some.three'.'some.four'"));
		Assert.assertTrue(jsonObject.get("'some.one'.'some.two'.'some.three'.'some.four'").equals("somepathvalue"));
		System.out.println(jsonObject);
	}
	
}
