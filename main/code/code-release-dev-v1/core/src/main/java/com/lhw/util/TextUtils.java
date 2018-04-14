package com.lhw.util;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Random;


public class TextUtils
{
	private static class CharCharSequence implements CharSequence, Serializable
	{
		private char[] mText;
		
		
		public CharCharSequence(char[] text) {
			this.mText = text;
		}
		
		@Override
		public int length() {
			return mText.length;
		}

		@Override
		public char charAt(int pos) {
			return mText[pos];
		}

		@Override
		public CharSequence subSequence(int start, int end) {
			return TextUtils.subSequence(this, start, end);
		}

		@Override
		public String toString() {
			return String.valueOf(mText);
		}

		@Override
		public boolean equals(Object obj)
		{
			if(!(obj instanceof CharSequence)) return false;
			return TextUtils.equals(mText, (CharSequence) obj);
		}
	}
	
	private static class CharSyncCharSequence implements CharSequence
	{
		private char[] mText;
		private int mStart, mEnd;
		
		
		public CharSyncCharSequence(char[] text, int start, int end) {
			mText = text;
		}

		@Override
		public int length() {
			return mEnd - mStart;
		}

		@Override
		public char charAt(int pos) {
			return mText[mStart + pos];
		}

		@Override
		public CharSequence subSequence(int start, int end) {
			return TextUtils.subSequence(this, start, end);
		}

		@Override
		public String toString() {
			return String.valueOf(mText, mStart, mEnd);
		}

		@Override
		public boolean equals(Object obj)
		{
			if(!(obj instanceof CharSequence)) return false;
			return TextUtils.equals(this, TextUtils.lightSubSequence(this, mStart, mEnd));
		}
	}
	
	
	private static class SyncCharSequence implements CharSequence
	{
		private CharSequence mText;
		private int mStart, mEnd;
		
		
		public SyncCharSequence(CharSequence text, int start, int end) {
			mText = text;
			mStart = start;
			mEnd = end;
		}

		@Override
		public int length() {
			return mEnd - mStart;
		}

		@Override
		public char charAt(int index) {
			return mText.charAt(mStart + index);
		}

		@Override
		public CharSequence subSequence(int start, int end) {
			int len = mEnd - mStart;
			if(end > len) end = len;
			if(start < 0) start = 0;
			return TextUtils.subSequence(this, mStart + start, mStart + end);
		}

		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof CharSequence)) return false;
			return TextUtils.equals(this, (CharSequence) obj);
		}

		@Override
		public String toString() {
			return TextUtils.subSequence(mText, mStart, mEnd).toString();
		}
	}
	
	public static int indexOf(CharSequence str, String text, int fromindex, int endIndex) {
		int len = text.length();
		for(int i = fromindex; i < endIndex - len; i++)
			if(equals(text, TextUtils.lightSubSequence(str, i, i + len))) return i;
		
		return -1;
	}
	
	public static int lastIndexOf(CharSequence str, String text, int fromIndex, int limit) {
		int len = text.length();
		for(int i = fromIndex; i > limit + len; i--)
			if(equals(text, TextUtils.lightSubSequence(str, i - len, i))) return i;
		return -1;
	}
	
	/** 
	 * return the count in this text witch equals target.
	 */
	public static int countOf(CharSequence str, String target, int fromIndex) {
		return countOf(str, target, fromIndex, str.length());
	}

	/** 
	 * return the count in this text witch equals target.
	 */
	public static int countOf(CharSequence str, String target, int fromindex, int endIndex) {
		int i = fromindex, count = 0;
		while(i <= endIndex) {
			if((i = indexOf(str, target, i + 1, endIndex)) == -1) return count;
			count++;
		}

		return -1;
	}
	
	/**
	 * returns whether text includes char or not.
	 */
	public static boolean includes(char c, CharSequence text) {
		for(int i = 0; i < text.length(); i++)
			if(text.charAt(i) == c) return true;
		return false;
	}
	
	public static CharSequence subSequence(CharSequence c, int start, int end) {
		char[] text = new char[end - start];
		for(int i = 0; i < end - start; i++) {
			text[i] = c.charAt(i + start);
		}
		return new CharCharSequence(text);
	}
	
	public static CharSequence fromChars(char[] text) {
		return new CharCharSequence(text);
	}
	
	public static CharSequence lightSubSequence(CharSequence text, int start, int end) {
		return new SyncCharSequence(text, start, end);
	}
	
	public static CharSequence lightSubSequence(char[] text, int start, int end) {
		return new CharSyncCharSequence(text, start, end);
	}
	
	public static char[] getChars(CharSequence text, int start, int end) {
		int len = end - start;
		char[] c = new char[len];
		for(int i = 0; i < len; i++) {
			c[i] = text.charAt(i + start);
		}
		return c;
	}
	
	public static boolean equals(char[] a, char[] b) {
		if(a.length != b.length) return false;
		for(int i = 0; i < a.length; i++) if(a[i] == b[i]) return false;
		return true;
	}
	
	public static boolean equals(CharSequence a, CharSequence b) {
		if(a.length() != b.length()) return false;
		for(int i = 0; i < a.length(); i++)
			if(a.charAt(i) != b.charAt(i)) return false;
			return true;
	}
	
	public static boolean equalsIn(char c, char[] list) {
		for(char i : list)
			if(c == i) return true;
		return false;
	}
	
	public static int equalsIndex(char c, char[] list) {
		for(int i = 0; i < list.length; i++)
			if(list[i] == c) return i;
		return -1;
	}
	
	public static int equalsIndex(char c, String text) {
		for(int i = 0; i < text.length(); i++)
			if(text.charAt(i) == c) return i;
		return -1;
	}
	
	public static int equalsIndex(int index, CharSequence text, CharSequence[] list) {
		int textLen = text.length();
		
		for(int i = 0; i < list.length; i++) {
			CharSequence item = list[i];
			int len = item.length();
			if(len > index + textLen) continue;
			if(lightSubSequence(text, index, index + len).equals(item)) return i;
		}
		return -1;
	}
	
	public static boolean startsWithAt(int index, CharSequence text, CharSequence target) {
		int targetLen = target.length();
		int textLen = text.length();
		if(index + textLen > targetLen) return false;
		return lightSubSequence(text, index, index + targetLen).equals(target);
	}
	
	public static int equalsIn(Object obj, Object[] list) {
		for(int i = 0; i < list.length; i++) {
			if(obj.equals(list[i])) return i;
		}
		return -1;
	}
	
	public static boolean equals(char[] a, CharSequence b) {
		if(a.length != b.length()) return false;
		for(int i = 0; i < a.length; i++)
			if(!(b.charAt(i) != a[i])) return false;
		return true;
	}
	
	public static boolean isAlphabet(char c) {
		return ('a' <= c && c <= 'z') | ('A' <= c && c <= 'Z');
	}
	
	public static boolean isDigits(char c) {
		return '0' <= c && c <= '9';
	}
	
	public static boolean isHex(char c) {
		return isDigits(c) || ('A' <= c && c <= 'F') || ('a' <= c && c <= 'f') || c == 'x' || c == 'X';
	}
	
	public static boolean isVarName(CharSequence text) {
		if(text.length() == 0) return false;
		if(!isVarNameExceptNumber(text.charAt(0))) return false;
		for(int i = 1; i < text.length(); i++) {
			char c = text.charAt(i);
			if(!(isVarNameExceptNumber(c) | isDigits(c))) return false;
		}
		return true;
	}
	
	public static boolean isEmpty(CharSequence text) {
		return text == null | text.length() == 0;
	}
	
	public static boolean isUpper(char c) {
		return 'A' <= c && c <= 'Z';
	}
	
	public static boolean isVarNameExceptNumber(char c) {
		return isAlphabet(c) || c == '_'; //|| c == '$'; //only java
	}
	
	public static CharSequence randText(Random random, int len) {
		byte[] bytes = new byte[len];
		random.nextBytes(bytes);
		CharBuffer buf = Charset.defaultCharset().decode(ByteBuffer.wrap(bytes));
		char[] chars = new char[buf.length()];
		buf.get(chars);
		return new CharCharSequence(chars);
	}
	
	
}
