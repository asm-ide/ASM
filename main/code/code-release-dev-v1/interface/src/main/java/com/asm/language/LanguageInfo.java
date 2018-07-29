package com.asm.language;


public interface LanguageInfo
{
	public String languageName();
	
	public String textQuotes();
	
	public char textEscaper();
	
	public String[] comments();
	
	public String textSeperators();
	
	public boolean getArg(String name, boolean defaultValue);
	
	/**
	 * varName : a RegExp string. defines what is s variable name.
	 *			 ex) [[a-z][A-z]\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF_\$][[a-z][A-Z][0-9]_\$]*
	 * number  : defines what is a number.
	 * 			 ex) ([+-]?[0-9]+(.[0-9])?(e)?|0\x[[0-9][a-f][A-F]])
	 */
	public String getArg(String name, String defaultValue);
}
