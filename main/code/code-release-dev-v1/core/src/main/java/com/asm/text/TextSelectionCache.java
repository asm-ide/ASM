package com.asm.text;

import com.lhw.util.TextUtils;


public class TextSelectionCache extends TextSelection
{
	private PositionData mData = new PositionData();
	private TextDraw mDraw;
	
	
	public TextSelectionCache(int start, int end, TextDraw draw) {
		super(start, end);
		
		mDraw = draw;
	}
	
	@Override
	public void beforeTextChanged(CharSequence text, int start, int count, int after) {
		super.beforeTextChanged(text, start, count, after);
		
		mData.resolveRemovedDelta(text, start, count, mDraw);
	}
	
	@Override
	public void onTextChanged(CharSequence text, int start, int before, int count) {
		super.onTextChanged(text, start, before, count);
		
		mData.resolveDelta(text, start, count, mDraw);
	}
}
