package com.asm.widget.codeedit;

import android.os.*;
import java.util.*;


public class Words implements Parcelable
{
	//UNUSED
	/*public static enum Searcher
	{
		RANGE(new Finder() {
				@Override
				public void find(Words words, TextPart p)
				{
					
				}
			}),
		MATCH(new Finder() {
				@Override
				public void find(Words words, TextPart p)
				{
					int mode = words.getArg("mode", 0);
					
				}
			}),
		;
		
		
		public static interface Finder
		{
			public void find(Words words, TextPart p);
		}
		
		
		private Finder mFinder;
		
		private Searcher(Finder finder) {
			mFinder = finder;
		}
		
		public Searcher newSearcher(Finder finder)
	}*/
	
	public static interface Searcher
	{
		public void find(ArrayList<Integer> target, Words words, TextPart p);
	}
	
	public static class TextPart
	{
		public int p, s, le, ne;
		public CharSequence l, n;
		
		
		public TextPart(int p1, int p2, int p3, int p4, CharSequence p5, CharSequence p6) {
			p = p1; s = p2; le = p3; ne = p4; l = p5; n = p6;
		}
	}
	
	// TODO : more changeModes
	//public static final int FLAG_CHANGEMODE_INNEW = 0x00000001 << 30;
	public static final int FLAG_MASKED = ~ (0x00000000); //~ (FLAG_... | FLAG_...)
	
	
	private String mColorName;
	private ArrayList<Word> mWordList;
	private Searcher mSearchWay;
	private HashMap<String, Object> mArgs;
	
	
	public Words() {
		mArgs = new HashMap<String, Object>();
	}
	
	public Words(String colorName) {
		this();
		this.mColorName = colorName;
	}
	
	public void putArg(String name, Object value) {
		mArgs.put(name, value);
	}
	
	public Object getArg(String name) {
		return mArgs.get(name);
	}
	
	public Object getArg(String name, Object defaultValue) {
		if(mArgs.containsKey(name)) return mArgs.get(name);
		else return defaultValue;
	}
	
	public boolean hasArg(String name) {
		return mArgs.containsKey(name);
	}

	public void setSearchWay(Searcher searchWay) {
		this.mSearchWay = searchWay;
	}

	public Searcher getSearchWay() {
		return mSearchWay;
	}
	
	public void setColor(String colorName) {
		this.mColorName = colorName;
	}
	
	public String getColor() {
		return mColorName;
	}
	
	public List<Integer> indexes(String text, int start, int end) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		//mSearchWay.mFinder.find(mWordList, text, start, end);
		return list;
	}

	public Word getWord(int index) {
		return mWordList.get(index);
	}

	public int addWord(Word rule) {
		mWordList.add(rule);
		return mWordList.size() - 1;
	}

	public void removeWord(int index) {
		mWordList.remove(index);
	}

	public void removeWord(Word matches) {
		mWordList.remove(matches);
	}

	public int wordsCount() {
		return mWordList.size();
	}

	public ArrayList<Word> wordList() {
		return mWordList;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel p, int flags) {
		p.writeSerializable(mWordList);
	}

	public  Words fromParcel(Parcel p) {
		Words word = new Words();
		word.mWordList = (ArrayList<Word>) p.readSerializable();
		return word;
	}
}
