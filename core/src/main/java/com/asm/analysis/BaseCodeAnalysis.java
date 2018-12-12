package com.asm.analysis;

import com.asm.language.Language;
import com.asm.language.HighlightArgs;


public class BaseCodeAnalysis extends CodeAnalysis
{
	private Language mLanguage;
	private CodeIterator mCodeIterator;
	
	
	public BaseCodeAnalysis(HighlightArgs args) {
		mLanguage = args.getLanguage();
		mCodeIterator = new CodeIterator();
		mCodeIterator.setInfo(mLanguage.getInfo());
	}
	
	
	@Override
	public boolean hasNext() {
		return mCodeIterator.hasNext();
	}

	@Override
	public CodePart next() {
		
		return null;
	}

	@Override
	public void remove() {
		
	}

	@Override
	public void setCode(CharSequence code) {
		
	}

	@Override
	public void move(int index) {
		
	}
	
}
