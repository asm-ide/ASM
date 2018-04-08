package com.asm.analysis.java;

import com.asm.analysis.CodeAnalysis;
import com.asm.analysis.CodeSuggest;


public class JavaAnalysis extends CodeAnalysis
{
	private static final CodeSuggest SUGGEST
		= new JavaSuggest();
	
	
	private int mIndex = 0;
	
	
	public JavaAnalysis() {
		
	}
	
	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public CodePart next() {
		return null;
	}

	@Override
	public void remove() {
		next();
	}

	@Override
	public void setCode(CharSequence code) {
		
	}

	@Override
	public void move(int index) {
		mIndex = index;
	}

	@Override
	public CodeSuggest getSuggest() {
		return SUGGEST;
	}
	
}
