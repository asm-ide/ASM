package com.asm.analysis.java;

import com.asm.analysis.CodeAnalysis;
import com.asm.analysis.CodeSuggest;
import com.asm.analysis.CodeIterator;
import com.lhw.util.TextUtils;
import com.asm.language.Language;


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
		Language lang = getLanguage();
		int type = part.type;
		
		if(mAnalysisDeeply) {
			// TODO : not support yet
		} else {
			switch(type) {
				case CodeIterator.TYPE_NORMAL:
					if(TextUtils.isUpper(text.charAt(0)) && TextUtils.containsLower(text))
						col = "java.class";
					else if(TextUtils.equalsIn(text, lang.getData("keywords", new CharSequence[] {})) != -1)
						col = "base.keyword";
					else if(TextUtils.equalsIn(text, lang.getData("types", new CharSequence[] {})) != -1)
						col = "base.type";
					else if(TextUtils.equalsIn(text, lang.getData("otherDatas", new CharSequence[] {})) != -1)
						col = "base.otherData";
					break;
					
				case CodeIterator.TYPE_NUMBER:
					col = "base.number";
					break;
					
				case CodeIterator.TYPE_SEPERATOR:
					if(TextUtils.equalsIn(text, lang.getData("operators", new CharSequence[] {})) != -1)
						col = "base.operator";
					else
						col = "base.math";
					break;
					
				case CodeIterator.TYPE_TEXT:
					col = "base.textQuote";
					break;
					
				case CodeIterator.TYPE_COMMENT:
					col = "base.comment";
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
