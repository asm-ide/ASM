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
			throw new IllegalArgumentException(e);
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
			case "byte":
				value = Byte.parseByte(content);
				break;
			case "short":
				value = Short.parseShort(content);
				break;
			case "strings":
				value = parseList(content);
				break;

			default:
				throw new IllegalArgumentException("unknown type: " + type);
		}
		return value;
	}

	public static String objectToString(Object data) {
		String value;
		if(data instanceof Integer)
			value = Integer.toString(data);
		else if(data instanceof Character)
			value = String.valueOf(new char[] {data});
		else if(data instanceof Float)
			value = Float.toString(data);
		else if(data instanceof Double)
			value = Double.toString(data);
		else if(data instanceof Long)
			value = Long.toString(data);
		else if(data instanceof Byte)
			value = Byte.toString(data);
		else if(data instanceof Short)
			value = Short.toString(data);
		else if(data instanceof Boolean)
			value = Boolean.toString(data);
		else if(data instanceof String)
			value = (String) data;
		else if(data instanceof String[])
			value = TypeUtils.stringArrayToString((Object[]) data);
		else throw new IllegalArgumentException("unknown type " + data.getClass().getSimpleName());

		return value;
	}

	public static String stringArrayToString(Object[] datas) {
		try {
			JSONArray arr = new JSONArray(datas);
			String text = arr.toString();
			return text.substring(1, text.length());
		} catch(JSONException e) {
			throw new IllegalStateException();
		}
	}

	@SuppressWarnings("unsafe")
	public static <T> T getObjectByClass(String data, Class<T> type) {
		Object value;
		if(type == Integer.TYPE)
			value = Integer.parseInt(data);
		else if(type == Character.TYPE) {
			if(data.length() != 1)
				throw new IllegalArgumentException("type is char but several texts");
			value = data.charAt(0);
		}
		else if(type == String.class)
			value = data;
		else if(type == Float.TYPE)
			value = Float.parseFloat(data);
		else if(type == Double.TYPE)
			value = Double.parseDouble(data);
		else if(type == Long.TYPE)
			value = Long.parseLong(data);
		else if(type == Boolean.TYPE)
			value = Boolean.parseBoolean(data);
		else if(type == Byte.TYPE)
			value = Byte.parseByte(data);
		else if(type == Short.TYPE)
			value = Short.parseShort(data);
		else if(type == String[].class)
			value = parseList(data);
		else throw new IllegalArgumentException("unknown type: " + type.getSimpleName());
		return (T) value;
	}

	@SuppressWarnings("unsafe")
	public static <T> T[] castArrays(Object[] arr, T[] newArr) {
		for(int i = 0; i < newArr.length; i++) {
			newArr[i] = (T) arr[i];
		}
		return newArr;
	}
}
