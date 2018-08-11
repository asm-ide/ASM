package com.asm.text;


public class TextSelectionCache extends TextSelection
{
	private PositionData mData;
	private PositionData mData2;
	private TextDraw mDraw;
	
	
	public TextSelectionCache(CharSequence text, int start, int end, TextDraw draw) {
		super(start, end);
		
		mDraw = draw;
		mData = new PositionData();
		mData.resolveDelta(text, 0, start, draw);
		PositionData delta = new PositionData();
		delta.resolveDelta(text, start, end - start, draw);
		mData2 = mData.add(delta);
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
	
	public int getLineEnd() {
		return mData2.getLine();
	}

	public int getColEnd() {
		return mData2.getCol();
	}

	public float getXEnd() {
		return mData2.getX();
	}

	public float getYEnd() {
		return mData2.getY();
	}
	
	@Override
	public void beforeTextChanged(CharSequence text, int start, int count, int after) {
		super.beforeTextChanged(text, start, count, after);
		if(count != 0) {
			PositionData delta = new PositionData();
			delta.resolveDelta(text, start, count, mDraw);
			mData = mData.sub(delta, text, mDraw);
			mData2 = mData2.sub(delta, text, mDraw);
		}
	}
	
	@Override
	public void onTextChanged(CharSequence text, int start, int before, int count) {
		super.onTextChanged(text, start, before, count);
		if(count != 0) {
			PositionData delta = new PositionData();
			delta.resolveDelta(text, start, count, mDraw);
			mData = mData.add(delta);
			mData2 = mData2.add(delta);
		}
	}
}
