package com.asm.widget.codeedit;


/**
 * Code utilities for CodeEdit.
 */
public class CodeUtils
{
	/**
	 * Convert tab indent to customized style.
	 */
	public static String convertIndent(String text, String indent) {
		//TODO: ADD SETTING / CONVERT BLANK LINE WITH / WITHOUT INDENT
		StringBuilder str = new StringBuilder();
		String[] lines = text.split("\n");
		for(String line : lines) {
			str.append(forwardTrim(line, new String[]{" ", "\t"}));
		}
		return str.toString();
	}
	
	private static String forwardTrim(String text, String[] by) {
		int forward = 0;
		while(forward <= text.length()) {
			boolean matches = false;
			for(String item : by) {
				if(text.substring(forward, forward + item.length() - 1) == item){ matches = true; break; }
			}
			if(matches) forward++;
			else break;
		}
		if(forward == text.length()) return "";
		return text.substring(forward);
	}
}
