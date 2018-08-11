package com.asm.analysis;

import com.asm.language.LanguageInfo;

import com.lhw.util.TextUtils;

import java.util.Iterator;
import java.util.regex.MatchResult;


// ISSUE #1


/**
 * Very simple code analysis utility.
 * This implements Iterator, and next() returns the part of code.
 * CodePart includes three things: text, index, type. This seperate
 * the code, like " to ", // to \n, the variable name(a-z, A-Z, ..).
 * This also seperate all the operators, such as ; , . " = / ( ...
 * The type indicates what it is, like text(surrounded with ""/''/...), comment
 * (/* ... *\/, // ... \n), normal(plain, such as int, abc, class, ArrayList).
 */
public class CodeIterator implements Iterator<CodeIterator.CodePart>
{
	/**
	 * Being returned by {@code CodeIterator.next()}, this shows what each part is
	 * and what type is. 
	 */
	public static class CodePart {
		/** the text of this. */
		public CharSequence text;
		
		/** the location where this text located on whole text. */
		public int index;
		
		/** the type, normal, text, comment or etc. */
		public int type;
		
		
		/** public constructor. */
		public CodePart() {}
		
		
		CodePart type(int type) {
			this.type = type;
			return this;
		}
		
		CodePart index(int index) {
			this.index = index;
			return this;
		}
		
		CodePart text(CharSequence text) {
			this.text = text;
			return this;
		}
		
		CodePart text(CharSequence text, int start, int end) {
			return text(TextUtils.lightSubSequence(text, start, end));
		}
	}
	
	
	/** unknown type. something went wrong. */
	public static final int TYPE_UNKNOWN = -1;
	
	/**
	 * Just plain text formed by {@code LanguageInfo.getArg("varName")}.
	 * In java, "public", "int", "text"(just some variable), "ArrayList"
	 * can be this.
	 */
	public static final int TYPE_NORMAL = 0;
	
	/** just number. starts with 0-9. */
	public static final int TYPE_NUMBER = 1;
	
	/**
	 * Some characters like {}();,.="|&![]
	 * All seperators are seperated between all of their forward/back.
	 */
	public static final int TYPE_SEPERATOR = 2;
	
	/** text, in java, surrounded by something like "" ''. */
	public static final int TYPE_TEXT = 3;
	
	/** comment. in java, surrounded by //\n /**\/ */
	public static final int TYPE_COMMENT = 4;
	
	
	public static final int MAX_TYPE_VALUE = 4;
	
	/** attached code. */
	private CharSequence mCode;
	
	/** attached language informatipn. */
	private LanguageInfo mInfo;
	
	/** read starting position. */
	private int mIndex = 0;
	
	
	public CodeIterator() {}
	
	public CodeIterator(CharSequence code) {
		setCode(code);
	}
	
	public CodeIterator(CharSequence code, LanguageInfo info) {
		setCode(code);
		setInfo(info);
	}
	

	public void setCode(CharSequence code) {
		mCode = code;
	}

	public CharSequence getCode() {
		return mCode;
	}

	public void setInfo(LanguageInfo info) {
		this.mInfo = info;
	}

	public LanguageInfo getInfo() {
		return mInfo;
	}
	
	/**
	 * Set the current position.
	 * Do not put any position, because CodeIterator might
	 * cause fatal error if position is strange.
	 */
	public void move(int index) {
		mIndex = index;
	}
	
	public int index() {
		return mIndex;
	}
	
	@Override
	public boolean hasNext() {
		return mIndex < mCode.length();
	}

	@Override
	public CodePart next() {
		if(!hasNext()) throw new IllegalStateException("reached of end");
		
		
		CodePart part = new CodePart().index(mIndex);
		
		
		for(int i = 0; i <= MAX_TYPE_VALUE; i++) {
			MatchResult result = find(i);
			
			if(result != null) {
				return part.text(mCode, result.start(), result.end()).type(i);
			}
		}
		
		
		return null;
	}
	
	private MatchResult find(int type) {
		CodeFinder finder = mInfo.finder(type);
		
		if(finder == null) return null;
		
		if(finder.isMatchStart(mCode, mIndex)) {
			return finder.match(mCode, mIndex);
		} else {
			return null;
		}
	}
	
	
	@Override
	public void remove() {
		next();
	}
}
