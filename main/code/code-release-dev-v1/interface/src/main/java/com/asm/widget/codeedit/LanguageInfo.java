package com.asm.widget.codeedit;


public interface LanguageInfo
{
	public String languageName();
	
	public String textQuotes();
	
	public char textEscaper();
	
	public Pair<String>[] comments();
	
	public String textSeperators();
	
	public boolean getArg(String name, boolean defaultValue);
	
	/**
	 * varNamesMore : more texts which can be a variable name (not first).
	 *				  excepts a~z, A~Z, 0~9, _
	 * TODO : varFirstNameMore
	 */
	public String getArg(String name, String defaultValue);
}
