package com.asm.language;

import java.util.HashMap;


/**
 * Global language storage for access language by name
 * and from xml access. Suggest all classes that implements
 * language register
 */
public class Languages
{
	/** languages */
	private static HashMap<String, Language> langs = new HashMap<String, Language>();
	
	
	/**
	 * Register the language.
	 * Suggest all classes that implements language register
	 * for xml using and global access.
	 * Language inflated from xml automatic registered.
	 */
	public static void register(String name, Language lang)
	{
		langs.put(name, lang);
	}
	
	/** get the language registered. */
	public static Language getLanguage(String name)
	{
		return langs.get(name);
	}
}
