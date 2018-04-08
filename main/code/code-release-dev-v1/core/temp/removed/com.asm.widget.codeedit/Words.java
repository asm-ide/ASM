package com.asm.widget.codeedit;

import java.util.*;
import android.os.*;
import java.io.*;


public class Words implements Parcelable
{
	private String colorName;
	private ArrayList<Word> wordList;
	
	
	public Words() {}
	
	public Words(String colorName) {
		this.colorName = colorName;
	}
	
	public void setColor(String colorName) { this.colorName = colorName; }
	
	public String getColor() { return colorName; }
	
	public List<Integer> indexes(CharSequence text) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		String str = text.toString();
		for(int i = 0; i < wordList.size(); i++) {
			
		}
		return list;
	}
	
	public Word getWord(int index) { return wordList.get(index); }
	
	public int addWord(Word rule) { wordList.add(rule); return wordList.size() - 1; }
	
	public void removeWord(int index) { wordList.remove(index); }
	
	public void removeWord(Word matches) { wordList.remove(matches); }
	
	public int wordsCount() { return wordList.size(); }
	
	public ArrayList<Word> wordList() { return wordList; }

	@Override
	public int describeContents() { return 0; }
	
	@Override
	public void writeToParcel(Parcel p, int flags) {
		p.writeSerializable(wordList);
	}
	
	public static Words fromParcel(Parcel p) {
		Words words = new Words();
		words.wordList = (ArrayList<Word>) p.readSerializable();
		return words;
	}
}
