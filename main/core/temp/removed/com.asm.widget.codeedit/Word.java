package com.asm.widget.codeedit;

import java.util.*;
import android.text.*;


public class Word
{
	public static class Matches
	{
		public int start;
		public int end;
		
		
		public Matches() {}
		
		public Matches(int start, int end) { this.start = start; this.end = end; }
		
		public int length() { return end - start; }
	}
	
	static interface MatchEach
	{
		public Matches matches(CharSequence target, int index) throws Exception;
	}
	
	public static class WordItem
	{
		public static final int MODE_MATCHES = 0x0;
		public static final int MODE_VARNAME = 0x1;
		public static final int MODE_START = 0x2;
		public static final int MODE_END = 0x3;
		public static final int MODE_FLAG_NOT = 0x1 << 31;
		/** {@hide} */
		public static final int MODE_FLAGS = MODE_FLAG_NOT; // | MODE_FLAG_...
		
		public static final int POSITION_BEFORE = 0;
		public static final int POSITION_EQUALS = 1;
		public static final int POSITION_AFTER = 2;
		
		public static final int VARTYPE_NONE = 0;
		public static final int VARTYPE_VAR = 1;
		public static final int VARTYPE_CLASS = 2;
		
		public int mode = MODE_MATCHES;
		public int position = POSITION_EQUALS;
		public int offset = 0;
		public String[] texts;
		
		public WordItem() {}
		
		public WordItem(int mode, int position, String[] texts) {
			this.mode = mode;
			this.position = position;
			this.texts = texts;
		}
		
		public ArrayList<Matches> matches(CharSequence target, int start, int end){
			ArrayList<Matches> result; // = new ArrayList<Matches>();
			switch(mode & ~MODE_FLAGS) {
				case MODE_MATCHES:
					switch(position) {
						case POSITION_BEFORE: result = endsWithAll(texts, target); break;
						case POSITION_EQUALS: result = equalsAll(texts, target); break;
						case POSITION_AFTER: result = startsWithAll(texts, target); break;
						default: throw new IllegalStateException("wrong position");
					}
					break;
					
				case MODE_VARNAME:
					String text = target.subSequence(start, end).toString();
					result = new ArrayList<Matches>();
					String[] texts = ;
					break;
					
				default: throw new IllegalStateException("wrong mode");
			}
			return result;
		}
		
		private static boolean isAdd(int flag, boolean org) {
			boolean rs = org;
			//apply flags
			if((flag & MODE_FLAG_NOT) == MODE_FLAG_NOT) rs = !rs;
			return rs;
		}
		
		//private static String[] splits(
		
		private static ArrayList<Matches> matchAll(CharSequence text, MatchEach matcher, int mode) {
			ArrayList<Matches> list = new ArrayList<Matches>();
			try {
				for(int i = 0; i < text.length(); i++) {
					Matches matches = matcher.matches(text, i);
					if(matches != null) list.add(matches);
				}
			} catch(Exception e) {}
			return list;
		}
		
		private static ArrayList<Matches> matchInverseAll(CharSequence text, MatchEach matcher, int mode) {
			ArrayList<Matches> list = new ArrayList<Matches>();
			try {
				for(int i = text.length() - 1; i >= 0; i++) {
					Matches matches = matcher.matches(text, i);
					if(matches != null) list.add(matches);
				}
			} catch(Exception e) {}
			return list;
		}
		
		private ArrayList<Matches> startsWithAll(final CharSequence[] texts, CharSequence target) {
			return matchAll(target, new MatchEach() {
					@Override
					public Matches matches(CharSequence target, int index) throws IllegalStateException
					{
						return startsWith(texts, index, target);
					}
				}, mode);
		}
		
		private Matches startsWith(CharSequence[] texts, int start, CharSequence target) {
			for(CharSequence text : texts)
				if(isAdd(mode, text.length() < start + target.length() && text.subSequence(start, start + text.length() - 1) == target)) return new Matches(start, start + text.length());
			return null;
		}
		
		private ArrayList<Matches> endsWithAll(final CharSequence[] texts, CharSequence target) {
			return matchInverseAll(target, new MatchEach() {
					@Override
					public Matches matches(CharSequence target, int index) throws IllegalStateException
					{
						return endsWith(texts, index, target);
					}
				}, mode);
		}
		
		private Matches endsWith(CharSequence[] texts, int end, CharSequence target) {
			for(CharSequence text : texts)
				if(text.length() < end && isAdd(mode, text.subSequence(end - text.length() + 1, end) == target)) return new Matches(end - text.length(), end);
			return null;
		}
		
		private int varType(CharSequence text) {
			int result = VARTYPE_VAR;
			char first = text.charAt(0);
			if(isAlphabetB(first)) result = VARTYPE_CLASS;
			else if(!(isAlphabetS(first) || first == '_')) return VARTYPE_NONE;
			for(int i = 1; i < text.length(); i++) {
				char c = text.charAt(i);
				if(!(isAlphabet(c) || isNumber(c) || c == '_')) return VARTYPE_NONE;
			}
			return result;
		}
		
		private ArrayList<Matches> equalsAll(final CharSequence[] texts, CharSequence target) {
			return matchAll(target, new MatchEach() {
					@Override
					public Matches matches(CharSequence target, int index) throws Exception
					{
						return equalsEach(texts, index, target);
					}
				}, mode);
		}
		
		private Matches equalsEach(CharSequence[] texts, int position, CharSequence target) {
			for(CharSequence text : texts)
				if(text.length() + position >= target.length() && isAdd(mode, text.subSequence(position, position + text.length() - 1) == target)) return new Matches(position, position + text.length());
			return null;
		}
		
		private static boolean equalsIn(CharSequence text, char target) {
			for(int i = 0; i < text.length(); i++) if(text.charAt(i) == target) return true;
			return false;
		}
		
		private static boolean isAlphabet(char c) { return isAlphabetB(c) || isAlphabetS(c); }
		
		private static boolean isAlphabetB(char c) { return 'A' <= c && c <= 'Z'; }
		
		private static boolean isAlphabetS(char c) { return 'a' <= c && c <= 'z'; }
		
		private static boolean isNumber(char c) { return '0' <= c && c <= '9'; }
	}
	
	private ArrayList<WordItem> list = new ArrayList<WordItem>();
	
	
	public Word() {}
	
	public void addItem(WordItem item) { list.add(item); }
	public void removeItem(WordItem item) { list.remove(item); }
	public WordItem getItem(int index) { return list.get(index); }
	public void clearItems() { list.clear(); }
	
	public boolean check(CharSequence text, int start, int end) {
		boolean okay = true;
//		if(beforeWords != null && !endsWith(beforeWords, start, text)
//			|| notBeforeWords != null && endsWith(notAfterWords, start, text)
//			|| afterWords != null && ! startsWith(afterWords, end, text)
//			|| notAfterWords != null && startsWith(notAfterWords, end, text)
//		) okay = false;
		return okay;
	}
	
	
}
