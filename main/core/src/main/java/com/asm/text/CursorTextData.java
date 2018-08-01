package com.asm.text;

import android.os.Parcel;


public class CursorTextData extends TextData
{
	private TextSelectionCache mSelection;
	
	
	protected CursorTextData() {
		super();
		init();
	}

	protected CursorTextData(CharSequence initValue) {
		super(initValue);
		init();
	}

	protected CursorTextData(char[] text) {
		super(text);
		init();
	}

	protected CursorTextData(Parcel p) {
		super(p);
		init();
	}
	
	private void init() {
		
	}
	
	private void initOnDraw() {
		
	}
	
	@Override
	public void writeToParcel(Parcel p, int flags) {
		super.writeToParcel(p, flags);
	}
	
	
	
}
