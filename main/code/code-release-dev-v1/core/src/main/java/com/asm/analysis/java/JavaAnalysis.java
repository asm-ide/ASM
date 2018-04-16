package com.asm.analysis.java;

import com.asm.analysis.CodeAnalysis;
import com.asm.analysis.CodeSuggest;
import com.asm.analysis.CodeIterator;
import com.lhw.util.TextUtils;


public class JavaAnalysis extends CodeAnalysis
{
	private static final boolean ISRELETIVEHIGHLIGHT = true;
	
	private CodeIterator mIterator;
	private CharSequence mCode;
	private boolean mAnalysisDeeply = false;
	
	
	public JavaAnalysis() {
		mIterator = new CodeIterator();
		
	}
	
	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public CodePart next() {
		CodeIterator.CodePart part = mIterator.next();
		CodePart newPart = new CodePart();
		CharSequence text = part.text;
		String col = "base.foreground";
		int type = part.type;
		
		if(mAnalysisDeeply) {
			// TODO : not support yet
		} else {
			switch(type) {
				case CodeIterator.TYPE_NORMAL:
					if(TextUtils.isUpper(text.charAt(0))) {
						col = "java.class";
					}
					break;
			}
		}
		
		return newPart.setText(text).setIndex(part.index).setColor(getStyle().getId(col));
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
	
	public void setIsAnalysisDeeply(boolean isDeeply) {
		mAnalysisDeeply = isDeeply;
	}
}
