package com.asm.text;

import android.text.TextWatcher;
import android.text.Editable;


/**
 * Simple text pointer class used on point the very point of the text.
 * You should know that this do not points text, but points place between texts.
 * @see TextPointers
 */
public class TextPointer implements TextWatcher
{
	private int mPosition;
	
	
	public TextPointer() {}
	
	public TextPointer(int position) {
		if(position < 0)
			throw new StringIndexOutOfBoundsException("position < 0");
		
		mPosition = position;
	}
	
	
	public int getPosition() {
		return mPosition;
	}
	
	public void setPosition(int position) {
		mPosition = position;
	}
	
	protected void onAct(int start, int before, int after) {
		switch(Action.getAction(before, after)) {
			case Action.ACTION_INSERT:
				onInsert(start, after);
				break;
				
			case Action.ACTION_DELETE:
				onDelete(start, before);
				break;
				
			case Action.ACTION_REPLACE:
				onReplace(start, before, after);
				break;
		}
	}
	
	protected boolean onInsert(int index, int len) {
		// abcINSERTEDdef|ghi
		if(mPosition < index) {
			// nothing
			return false;
		} else {
			mPosition += len;
		}
		return true;
	}
	
	protected boolean onDelete(int index, int len) {
		//abcjk|lmn
		if(mPosition < index) {
			// nothing
			return false;
		} else if(mPosition < len + index) {
			mPosition = index;
		} else {
			mPosition -= len;
		}
		return true;
	}
	
	protected boolean onReplace(int index, int before, int after) {
		// abcINSERTEDghij
		if(mPosition < index) {
			return false;
		} else if(mPosition < before + index) {
			mPosition = index + after;
		} else {
			mPosition += after - before;
		}
		return true;
	}
	
	@Override
	public void beforeTextChanged(CharSequence text, int start, int count, int after) {
		
	}
	
	@Override
	public void onTextChanged(CharSequence text, int start, int before, int count) {
		onAct(start, before, count);
	}
	
	@Override
	public void afterTextChanged(Editable text) {
		
	}
}
