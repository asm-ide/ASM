package com.asm.widget.codeedit;

import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.text.Editable;


public class ScrollingEditTextInputConnection extends BaseInputConnection
{
	private ScrollingEditText mView;
	
	
	public ScrollingEditTextInputConnection(ScrollingEditText view, boolean mutatable) {
		super(view, mutatable);
		
	}
	
	@Override
	public Editable getEditable() {
		return mView.getText();
	}
}
