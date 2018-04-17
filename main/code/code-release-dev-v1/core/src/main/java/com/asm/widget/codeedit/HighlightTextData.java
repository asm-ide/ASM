package com.asm.widget.codeedit;

import com.asm.util.NonReferenceArrayList;
import com.asm.text.TextData;
import com.asm.text.TextDraw;

import android.os.Parcel;

import java.io.Serializable;
import java.util.Arrays;


public class HighlightTextData extends TextData implements Serializable
{
	private NonReferenceArrayList mHighlightCache;
	
	
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
	
	private void init() {
		initHighlightCache(new short[0]);
	}
	
	private void initHighlightCache(short[] cache) {
		mHighlightCache = new NonReferenceArrayList(cache, 0);
	}
	
	@Override
	protected void onInsert(int where, CharSequence text, int start, int end) {
		super.onInsert(where, text, start, end);
		
		int len = end - start;
		short[] arr = new short[len];
		Arrays.fill(arr, (short) -1);
		mHighlightCache.insert(where, arr, 0, len);
	}
	
	public static HighlightTextData fromParcel(Parcel p, TextDraw parent) {
		return fromParcel(new HighlightTextData(), p, parent);
	}
	
	public static HighlightTextData fromParcel(TextData d, Parcel p, TextDraw parent) {
		HighlightTextData data = (HighlightTextData) TextData.fromParcel(d, p, parent);
		short[] arr = null;
		char[] tmpArr = null;
		p.readCharArray(tmpArr);
		data.initHighlightCache(arr);
		return data;
	}
	
	public static HighlightTextData wrapSerializable(Object data, TextDraw parent) {
		return (HighlightTextData) TextData.wrapSerializable(data, parent);
	}
	
	protected class HighlightTextDataParcel extends TextDataParcel
	{
		@Override
		public void writeToParcel(Parcel p, int flags) {
			super.writeToParcel(p, flags);
			p.writeByteArray((byte[]) mHighlightCache.get());
		}
	}
}
