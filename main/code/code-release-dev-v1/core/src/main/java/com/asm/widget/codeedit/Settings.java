package com.asm.widget.codeedit;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Global setting storage.
 * Should call loadSetting before using it.
 */
public final class Settings
{
	/** settings about {@code CodeStyle} */
	public static final class codestyle
	{
		/** when inflating xml */
		public static final String preferencePath = "code-style_pref";
		
		/** what seperates between language and color name */
		public static final String nameSeperator = ".";
		
		/** tab indent */
		public static String tabIndent = "\t";
	}
	
	
	/** the mode how to open preference. */
	public static final int prefMode = Context.MODE_PRIVATE;
	
	/** default text size */
	public static float textSize = 16f;
	
	/** main setting storage */
	private static SharedPreferences mainPrefs;
	
	/** whether setting is loaded */
	private static boolean isLoaded = false;
	
	
	/** settings is not constructable */
	private Settings()
	{}
	
	
	public static boolean isLoaded() {
		return isLoaded;
	}
	
	public static boolean loadSettingIfNotLoaded(Context context) {
		if(!isLoaded) {
			loadSetting(context);
			return true;
		}
		return false;
	}
	
	/** load settings */
	public static void loadSetting(Context context)
	{
		isLoaded = true;
		mainPrefs = context.getSharedPreferences("code-edit$setting", prefMode);
		textSize = mainPrefs.getFloat("textSize", textSize);
		codestyle.tabIndent = mainPrefs.getString("tabIndent", codestyle.tabIndent);
	}
	
	public static void clear() {
		mainPrefs = null;
	}
}
