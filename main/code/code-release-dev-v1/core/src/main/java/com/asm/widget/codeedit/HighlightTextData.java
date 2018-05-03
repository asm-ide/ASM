package com.asm.widget.codeedit;

import com.asm.text.TextData;
import com.asm.text.TextDraw;

import com.lhw.util.NonReferenceArrayList;

import android.os.Parcel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.ArrayList;


public class HighlightTextData extends TextData implements Serializable
{
	private Object mLock = new Object();
	private NonReferenceArrayList mHighlightCache;
	//int[] is just a pair
	private ArrayList<int[]> list = new ArrayList<int[]>();
	
	
	protected HighlightTextData() {
		super();
		
		init();
	}
	
	protected HighlightTextData(CharSequence initValue) {
		super(initValue);
		
		init();
	}
	
	protected HighlightTextData(char[] text) {
		super(text);
		
		init();
	}
	
	protected HighlightTextData(Parcel p) {
		super(p);
		
		char[] data = new char[p.readInt()];
		p.readCharArray(data);
		initHighlightCache(data);
	}
	
	private void init() {
		initHighlightCache(new char[0]);
	}
	
	private void initHighlightCache(char[] cache) {
		mHighlightCache = new NonReferenceArrayList(cache, 0);
		mHighlightCache.setListener(new NonReferenceArrayList.Listener() {
				@Override
				public Object copyOf(Object arr, int len) {
					return Arrays.copyOf((char[]) arr, len);
				}
			});
	}
	
	@Override
	protected void onInsert(int where, CharSequence text, int start, int end) {
		super.onInsert(where, text, start, end);
		
		int len = end - start;
		char[] arr = new char[len];
		Arrays.fill(arr, (char) -1);
		mHighlightCache.insert(where, arr, 0, len);
		list.add(new int[] {start, end});
	}
	
	@Override
	protected void onDelete(int start, int end) {
		super.onDelete(start, end);
		
		mHighlightCache.delete(start, end);
		list.add(new int[] {start, start});
	}

	@Override
	public void onReplace(int st, int en, CharSequence text, int start, int end) {
		super.onReplace(st, en, text, start, end);
		
		int len = end - start;
		char[] arr = new char[len];
		Arrays.fill(arr, (char) -1);
		mHighlightCache.delete(st, en);
		mHighlightCache.insert(st, arr, 0, len);
		list.add(new int[] {st, st + len});
	}
	
	public static HighlightTextData fromParcel(Parcel p, TextDraw parent) {
		return HighlightTextData.wrapFromDataStream(new HighlightTextData(p), parent);
	}
	
	public static HighlightTextData wrapFromDataStream(Object data, TextDraw parent) {
		return (HighlightTextData) TextData.wrapFromDataStream(data, parent);
	}
	
	@Override
	public void writeToParcel(Parcel p, int flags) {
		super.writeToParcel(p, flags);
		p.writeCharArray((char[]) mHighlightCache.get());
	}
}
