package com.asm.analysis;

import java.util.Iterator;

import com.asm.widget.codeedit.*;
import com.asm.language.*;


public abstract class CodeAnalysis implements Iterator<CodeAnalysis.CodePart>
{
	public static class CodePart
	{
		private CharSequence mText;
		private int mIndex;
		private short mColor;
		
		
		public CodePart() {}
		
		public CodePart(CharSequence text, int index, byte color) {
			mText = text;
			mIndex = index;
			mColor = color;
		}
		
		public CodePart setColor(short color){
			mColor = color;
			return this;
		}
		
		public short getColor() {
			return mColor;
		}
		
		
		public CodePart setText(CharSequence text) {
			mText = text;
			return this;
		}
		
		public CharSequence getText() {
			return mText;
		}
		
		public CodePart setIndex(int index) {
			mIndex = index;
			return this;
		}
		
		public int getIndex() {
			return mIndex;
		}
	}
	
	private HighlightArgs mArgs;
	
	
	public void initialize(HighlightArgs args) {
		mArgs = args;
	}
	
	public Language getLanguage() {
		return mArgs.getLanguage();
	}
	
	public LanguageInfo getInfo() {
		return mArgs.getInfo();
	}
	
	public CodeStyleInterface getStyle() {
		return mArgs.getStyle();
	}
	
	//TODO clean imports
	
	public abstract void setCode(CharSequence code);
	
	public abstract void move(int index);
}
