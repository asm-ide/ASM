package com.asm.analysis;

import java.util.regex.MatchResult;


public interface CodeFinder
{
	public boolean isMatchStart(CharSequence text, int index);
	
	public MatchResult match(CharSequence text, int index);
}
