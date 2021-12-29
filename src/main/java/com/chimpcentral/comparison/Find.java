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
		if (object instanceof Map<?, ?>) return true;
		return false;
	}
	
	boolean objectIsList(Object object) {
		if (object instanceof List<?>) return true;
		return false;
	}
}
