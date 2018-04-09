package com.asm.language;

import com.lhw.util.Pair;


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
	public Pair<String>[] comments() {
		return new Pair[] {
			//new Pair<String>("//", "\n"),
			//new Pair<String>("/*", "*/"),
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
