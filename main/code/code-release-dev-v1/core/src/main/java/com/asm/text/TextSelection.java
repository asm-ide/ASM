package com.asm.text;


public class TextSelection extends TextPointer
{
	private int mLen;
	
	
	public TextSelection() {
		super();
	}
	
	public TextSelection(int start, int end) {
		super(start);
		
		if(start > end)
			throw new StringIndexOutOfBoundsException("start > end");
		
		mLen = end - start;
	}
	
	@Override
	protected boolean onInsert(int index, int len) {
		if(super.onInsert(index, len)) {
			if(index < getPositionEnd())
				mLen += len;
			
			return true;
		}
		
		return false;
	}
	
	@Override
	protected boolean onDelete(int index, int len) {
		if(super.onDelete(index, len)) {
			// ab[cde(fgh]ijk)lmn
			//         ^
			//    inDeleteLen
			int inDeleteLen = getPositionEnd() - index;
			if(inDeleteLen > 0) {
				int outDeleteLen = index + len - getPositionEnd();
				if(outDeleteLen < 0)
					inDeleteLen += outDeleteLen;
			}
			
			return true;
		}
		
		return false;
	}

	@Override
	protected boolean onReplace(int index, int before, int after) {
		// I wanted to do like TextPointer, but...
		// My brain might be going to explode!
		boolean is = false;
		is = is || onDelete(index, before);
		is = is || onInsert(index, after);
		return is;
	}
	
	public int length() {
		return mLen;
	}
	
	public void setLength(int len) {
		mLen = len;
	}
	
	public int getPositionEnd() {
		return getPosition() + length();
	}
	
	public boolean intersects(int pos) {
		return getPosition() <= pos && pos <= getPositionEnd();
	}
	
	public boolean intersects(TextSelection other) {
		return getPosition() <= other.getPositionEnd() && getPositionEnd() >= other.getPosition();
	}
}
