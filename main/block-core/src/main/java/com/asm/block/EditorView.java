package com.asm.block;

import android.view.ViewGroup;
import android.content.Context;
import android.util.AttributeSet;


public class EditorView extends ViewGroup
{
	public EditorView(Context context) {
		this(context, null);
	}
	
	public EditorView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public EditorView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr); 
	}
	
	
	@Override
	protected void onLayout(boolean updated, int l, int t, int r, int b) {
		
	}
}
