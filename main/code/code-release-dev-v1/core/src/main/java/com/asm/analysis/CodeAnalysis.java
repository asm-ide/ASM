package com.asm.analysis;

import java.util.Iterator;


public abstract class CodeAnalysis implements Iterator<CodeAnalysis.CodePart>
{
	public static class CodePart
	{
		private CharSequence mText;
		private int mIndex;
		private int mType;
		private byte mColor;
		
		
		public CodePart() {}
		
		public CodePart(CharSequence text, int index, int type) {
			mText = text;
			mIndex = index;
			mType = type;
		}
		
		public CodePart setColor(byte color){
			mColor = color;
			return this;
		}

		public byte getColor() {
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
		
		public CodePart setType(int type) {
			mType = type;
			return this;
		}

		public int getType() {
			return mType;
		}
	}
	
	
	public abstract void setCode(CharSequence code);
	
	public abstract void move(int index);
	
	public abstract CodeSuggest getSuggest();
}
