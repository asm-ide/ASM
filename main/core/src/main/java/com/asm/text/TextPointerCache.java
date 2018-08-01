package com.asm.text;


public class TextPointerCache extends TextPointer
{
	private PositionData mData;
	private TextDraw mDraw;
	
	
	public TextPointerCache(CharSequence text, int start, TextDraw draw) {
		super(start);
		
		mDraw = draw;
		mData = new PositionData();
		mData.resolveDelta(text, 0, start, draw);
	}
	
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
