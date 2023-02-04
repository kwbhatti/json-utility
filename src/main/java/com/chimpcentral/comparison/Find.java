package com.chimpcentral.comparison;

import java.util.List;
import java.util.Map;

abstract class Find {

	String getCurrentJsonpath(String parentJsonpath, Object childKey) {
		String currentJsonpath = null;
		String childJsonpath = "'" + (String) childKey + "'";
		if (parentJsonpath.equals(".")) {
			currentJsonpath = childJsonpath;
		} else {
			currentJsonpath = parentJsonpath + "." + childJsonpath;
		}
		return currentJsonpath;
	}
	
	boolean objectIsMap(Object object) {
		return object instanceof Map<?, ?>;
	}
	
	boolean objectIsList(Object object) {
		return object instanceof List<?>;
	}
}
