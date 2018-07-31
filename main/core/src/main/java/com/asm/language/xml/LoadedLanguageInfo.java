package com.asm.language.xml;

import com.asm.language.LanguageInfo;
import java.lang.reflect.Field;
import java.util.HashMap;
import com.lhw.util.TypeUtils;


public class LoadedLanguageInfo implements LanguageInfo
{
	public String name;
	public String textQuotes = "\"\'";
	public char textEscaper = '\\';
	public String[] comments;
	public String textSeperators = ";.,{}()[]:+-/*?<>&|!=^~";
	public HashMap<String, Object> args = new HashMap<String, Object>();
	
	
	@Override
	public String languageName() {
		return name;
	}
	
	@Override
	public String textQuotes() {
		return textQuotes;
	}
	
	@Override
	public char textEscaper() {
		return textEscaper;
	}
	
	@Override
	public String[] comments() {
		return comments;
	}
	
	@Override
	public String textSeperators() {
		return textSeperators;
	}
	
	@Override
	public boolean getArg(String name, boolean defaultValue) {
		if(args.containsKey(name)) return (boolean) args.get(name);
		return defaultValue;
	}
	
	@Override
	public String getArg(String name, String defaultValue) {
		if(args.containsKey(name)) return (String) args.get(name);
		return defaultValue;
	}
	
	public void put(String name, String content) {
		switch(name) {
			case "textQuotes":
				textQuotes = content;
				break;
			case "textEscaper":
				if(content.length() != 1)
					throw new IllegalArgumentException("content len is not 1");
				textEscaper = content.charAt(0);
				break;
			case "comments":
				comments = (String[]) TypeUtils.parseList(content);
				break;
			case "textSeperators":
				textSeperators = content;
				break;
		}
	}
	
	public void putArg(String name, String content, String type) {
		args.put(name, TypeUtils.getObjectFromString(content, type));
	}
}
