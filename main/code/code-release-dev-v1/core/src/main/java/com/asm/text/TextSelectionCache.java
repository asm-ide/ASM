package com.asm.text;

import com.lhw.util.TextUtils;


public class TextSelectionCache extends TextSelection
{
	private PositionData mData;
	private PositionData mData2;
	private TextDraw mDraw;
	
	
	public TextSelectionCache(CharSequence text, int start, int end, TextDraw draw) {
		super(start, end);
		
		mDraw = draw;
		mData = new PositionData();
		mData.resolveDelta(text, start, end - start, draw);
		mData2 = null;
	}
	
	public int getLine() {
		return mData.getLine();
	}
	
	public int getCol() {
		return mData.getCol();
	}
	
	public float getX() {
		return mData.getX();
	}
	
	public float getY() {
		return mData.getY();
	}
	
	public int get
	
	@Override
	public void beforeTextChanged(CharSequence text, int start, int count, int after) {
		super.beforeTextChanged(text, start, count, after);
		if(count != 0)
			mData.resolveRemovedDelta(text, start, count, mDraw);
	}
	
	@Override
	public void onTextChanged(CharSequence text, int start, int before, int count) {
		super.onTextChanged(text, start, before, count);
		if(count != 0)
			mData.resolveDelta(text, start, count, mDraw);
	}
}
