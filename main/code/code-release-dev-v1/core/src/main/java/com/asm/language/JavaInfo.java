package com.asm.language;


public class JavaInfo implements LanguageInfo
{
	@Override
	public String languageName() {
		return "java";
	}

	@Override
	public String textQuotes() {
		return "\"\'";
	}

	@Override
	public char textEscaper() {
		return '\\';
	}

	@Override
	public String[] comments() {
		return new String[] {
			"//", "\n", // //comments
			"/*", "*/", // /* comments */
		};
	}

	@Override
	public String textSeperators() {
		return ";.,{}()[]:+-/*?<>&|!=^~";
	}

	@Override
	public boolean getArg(String name, boolean defaultValue) {
		return defaultValue;
	}

	@Override
	public String getArg(String name, String defaultValue) {
		return defaultValue;
	}
}
