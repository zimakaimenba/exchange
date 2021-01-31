package com.exchangeinfomanager.commonlib;

import com.google.gson.Gson;

public class SerialUtils {

	//___________________________________________________________________________________

	public static String serializeObject(Object o) {
	    Gson gson = new Gson();
	    String serializedObject = gson.toJson(o);
	    return serializedObject;
	}
	//___________________________________________________________________________________

	public static Object unserializeObject(String s, Object o){
	    Gson gson = new Gson();
	    Object object = gson.fromJson(s, o.getClass());
	    return object;
	}
	       //___________________________________________________________________________________
	public static Object cloneObject(Object o){
	    String s = serializeObject(o);
	    Object object = unserializeObject(s,o);
	    return object;
	}
}