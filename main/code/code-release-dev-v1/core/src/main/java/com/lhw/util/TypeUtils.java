package com.lhw.util;

import org.json.JSONArray;
import org.json.JSONException;


public class TypeUtils
{
	public static Object[] parseList(String text) {
		try {
			JSONArray jsonArr = new JSONArray("[" + text + "]");
			Object[] arr = new Object[jsonArr.length()];
			for(int i = 0; i < arr.length; i++) {
				arr[i] = jsonArr.get(i);
			}
			return arr;
		} catch(JSONException e) {
			throw new IllegalArgumentException("wrong data: " + e.getMessage());
		}
	}
	
	public static Object getObjectFromString(String content, String type) {
		Object value;
		switch(type) {
			case "string":
				value = content;
				break;
			case "char":
				if(content.length() != 1)
					throw new IllegalArgumentException("type is char but several texts");
				value = content.charAt(0);
				break;
			case "integer":
				value = Integer.parseInt(content);
				break;
			case "float":
				value = Float.parseFloat(content);
				break;
			case "double":
				value = Double.parseDouble(content);
				break;
			case "long":
				value = Long.parseLong(content);
				break;
			case "boolean":
				value = Boolean.parseBoolean(content);
				break;
			case "strings":
				value = parseList(content);
				break;
			default:
				throw new IllegalArgumentException("unknown type: " + type);
		}
		return value;
	}
}
