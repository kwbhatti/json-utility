package com.chimpcentral.jsons;

interface Mutable<T> {

	public <V> T add(String jsonpath, V value);
	
	public T delete(String jsonpath);

}
