package com.asm.language.xml;

import com.asm.language.LanguageInfo;

import java.util.HashMap;

import com.asm.analysis.CodeFinder;


public class LoadedLanguageInfo implements LanguageInfo
{
	public String name;
	public HashMap<String, Object> args = new HashMap<>();
	public HashMap<Integer, CodeFinder> finders = new HashMap<>();
	
	
	@Override
	public String languageName() {
		return name;
	}
	
	@Override
	public CodeFinder finder(int type) {
		return finders.getOrDefault(type, null);
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
}
