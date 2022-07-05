package com.chimpcentral.jsons;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class JSONPathUtility {

	private static final String DOT = ".";
	private static final String QUOTE = "'";
	
	private JSONPathUtility() {	
	}
	
	public static String getFirstNode(String jsonpath) {
		if (jsonpath.startsWith(QUOTE)) {
			jsonpath = jsonpath.replaceFirst(QUOTE, "");
			return jsonpath.split(Pattern.quote(QUOTE))[0];
		}
		else return jsonpath.split(Pattern.quote(DOT))[0];
	}
	
	public static String getLastNode(String jsonpath) {
		List<String> nodes = getNodes(jsonpath);
		return nodes.get(nodes.size() -1);
	}
	
	public static String removeFirstNode(String jsonpath) {
		if (jsonpath.startsWith(QUOTE)) {
			jsonpath = jsonpath.replaceFirst(Pattern.quote(QUOTE + getFirstNode(jsonpath) + QUOTE), "");
		} else jsonpath = jsonpath.replaceFirst(Pattern.quote(getFirstNode(jsonpath)), "");
		return jsonpath.replaceFirst(Pattern.quote(DOT), "");
	}
	
	public static boolean isLastNode(String jsonpath) {
		return removeFirstNode(jsonpath).equals("");
	}
	
	public static List<String> getNodes(String jsonpath) {
		List<String> nodes = new ArrayList<>();
		while (!isLastNode(jsonpath)) {
			nodes.add(getFirstNode(jsonpath));
			jsonpath = removeFirstNode(jsonpath);
		}
		nodes.add(getFirstNode(jsonpath));
		return nodes;
	}
	
	public static String removeNode(String jsonpath, int index) {
		StringBuilder updatedPath = new StringBuilder();
		List<String> nodes = getNodes(jsonpath);
		for (int i = 0; i < nodes.size(); i++) {
			String node = nodes.get(i);
			if (nodes.get(i).contains(".")) node = "'" + node + "'";
			if (i != index) updatedPath.append(node).append(".");
		}
		return updatedPath.substring(0, updatedPath.length() -1);
	}
	
	public static String removeLastNode(String jsonpath) {
		if (getNodes(jsonpath).size() == 1) return "";
		return removeNode(jsonpath, getNodes(jsonpath).size() -1);
	}
	
	public static boolean isArrayNode(String jsonnode) {
		return (jsonnode.contains("[") && jsonnode.contains("]"));
	}
	
	public static boolean isArrayPath(String jsonpath) {
		String finalNode = getLastNode(jsonpath);
		return (finalNode.contains("[") && finalNode.contains("]"));
	}
	
	public static String getNodeValueFromArrayNodepath(String nodepath) {
		return nodepath.substring(0, nodepath.indexOf("["));
	}
	
	public static int getArrayIndexFromArrayNodepath(String nodepath) {
		return Integer.valueOf(nodepath.substring(nodepath.indexOf("[") + 1, nodepath.indexOf("]")));
	}
	
	public static String getPathWithoutLastArrary(String jsonpath) {
		String jsonpathWithoutLastNode = removeLastNode(jsonpath);
		String finalNodePath = getLastNode(jsonpath);
		String finalNodePathValue = getNodeValueFromArrayNodepath(finalNodePath);
		return jsonpathWithoutLastNode + "." + finalNodePathValue;
	}
	
	public static int getLastArrayIndex(String jsonpath) {
		String finalNodePath = getLastNode(jsonpath);
		return getArrayIndexFromArrayNodepath(finalNodePath);
	}
	
}
