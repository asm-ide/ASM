package com.asm.analysis;

import com.asm.language.LanguageInfo;

import com.lhw.util.TextUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
			return text(text.subSequence(start, end));
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
	
	
	/** attached code. */
	private CharSequence mCode;
	
	/** attached language informatipn. */
	private LanguageInfo mInfo;
	
	/** read starting position. */
	private int mIndex = 0;
	
	/** RegEx and provide varname search. */
	private Pattern mVarName;
	
	/** last found position cache. */
	private int mVarNameStart;
	
	
	public CodeIterator() {}
	
	public CodeIterator(CharSequence code) {
		mCode = code;
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
		
		String varName = info.getArg("varName", null);
		if(varName != null) {
			mVarName = Pattern.compile(varName);
		}
		mVarNameStart = -1;
	}

	public LanguageInfo getInfo() {
		return mInfo;
	}
	
	/**
	 * Set the current position.
	 * Do not put 
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
		
		LanguageInfo info = mInfo;
		final CharSequence text = mCode;
		int startIndex = mIndex;
		
		CodePart part = new CodePart().index(mIndex);
		
		char cur = text.charAt(mIndex);
		if(mVarNameStart < startIndex) {
			Matcher varMatcher = mVarName.matcher(text);
			varMatcher.find();
			mVarNameStart = varMatcher.start();
		}
		
		if(mVarNameStart == startIndex) {
			//variable _temp, abc, a123, ... (common)
			while(hasNext()) {
				char c = text.charAt(mIndex);
				if(!(TextUtils.isVarNameExceptNumber(c) || TextUtils.isDigits(c) || TextUtils.includes(c, info.getArg("varNamesMore", "")))) break;
				mIndex++;
			}
			
			return part.type(TYPE_NORMAL).text(text, startIndex, mIndex++);
		} else if(TextUtils.isDigits(cur)) {
			//number 19, 0xabc, 09 ... (java)
			while(hasNext()) {
				char c = text.charAt(mIndex);
				if(!(TextUtils.isHex(c))) break;
				mIndex++;
			}
			
			return part.type(TYPE_NUMBER).text(text, startIndex, mIndex++);
		} else {
			int i1 = TextUtils.equalsIndex(cur, info.textQuotes());
			if(i1 == -1) { //not var,num,text
				int i2 = -1;
				String[] comments = info.comments();
				{
					for(int i = 0; i < comments.length; i += 2) {
						int end = startIndex + comments[i].length();
						if(end > text.length()) continue;
						CharSequence c = text.subSequence(startIndex, end);
						if(comments[i].contentEquals(c)) i2 = i;
					}
				}
				
				if(i2 == -1) {
					//not var,num,text,note
					if(TextUtils.includes(cur, info.textSeperators())) {
						return part.type(TYPE_SEPERATOR).text(text, startIndex, mIndex++);
					} else {
						mIndex++;
						return part.type(TYPE_UNKNOWN);
					}
				} else {
					//note //text\n or /*lines*/ (java)
					String ends = comments[i2 + 1];
					
					while(hasNext()) {
						if(TextUtils.startsWithAt(mIndex, mCode, ends))
							break;
						mIndex++;
					}
					
					return part.type(TYPE_COMMENT).text(mCode, startIndex, mIndex++);
				}
			} else {
				//text "text" or 'c' (java)
				final char textEscaper = info.textEscaper();
				final char quote = info.textQuotes().charAt(i1);
				boolean lastEscaper = false;
				boolean isNewlineBreakText = info.getArg("isNewlineBreakText", false);
				
				while(hasNext()) {
					if(lastEscaper) {
						
					} else {
						char c = text.charAt(mIndex);
						if(lastEscaper) {
							lastEscaper = false;
							continue;
						} else if(c == textEscaper) {
							lastEscaper = true;
							continue;
						} else if(c == quote) break;
						else if(c == '\n') break;
					}
					mIndex++;
				}
				
				return part.type(TYPE_TEXT).text(text, startIndex, mIndex++);
			}
		}
	}
	
	@Override
	public void remove() {
		next();
	}
}
