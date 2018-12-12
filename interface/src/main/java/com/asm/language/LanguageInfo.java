package com.asm.language;

import com.asm.analysis.CodeFinder;

import java.util.regex.Pattern;


public interface LanguageInfo
{
	public String languageName();
	
	/**
	 * @param type static member of {@code CodeIterator} or class def.b
	 */
	public CodeFinder finder(int type);
	
	public boolean getArg(String name, boolean defaultValue);
	
	public String getArg(String name, String defaultValue);
}
