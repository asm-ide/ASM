package com.asm.analysis;

import com.lhw.util.TextUtils;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.MatchResult;


public class TextCodeFinder implements CodeFinder
{
	private char[] mFirst;
	private Pattern mPattern;
	
	
	public TextCodeFinder(char[] first, Pattern pattern) {
		mFirst = first;
		mPattern = pattern;
	}
	
	
	@Override
	public boolean isMatchStart(CharSequence text, int index) {
		return TextUtils.equalsIn(text.charAt(index), mFirst);
	}
	
	@Override
	public MatchResult match(CharSequence text, int index) {
		Matcher matcher = mPattern.matcher(text);
		if(!matcher.find(index + 1)) return null;
		
		return matcher.toMatchResult();
	}
}
