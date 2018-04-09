package com.asm.analysis.java;

import com.asm.analysis.CodeAnalysis;
import com.asm.analysis.CodeSuggest;
import com.asm.analysis.CodeIterator;


public class JavaAnalysis extends CodeAnalysis
{
	private CodeIterator mIterator;
	private CharSequence mCode;
	
	
	public JavaAnalysis() {
		mIterator = new CodeIterator();
		
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
		mCode = code;
		mIterator.setCode(code);
	}

	@Override
	public void move(int index) {
		mIterator.move(index);
	}
}
