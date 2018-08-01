package com.asm.widget;

import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.util.AttributeSet;
import android.support.v7.widget.RecyclerView;


public class FilePicker extends ViewGroup
{
	private RecyclerView list;
	
	
	public FilePicker(Context context) {
		this(context, null);
	}
	
	public FilePicker(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public FilePicker(Context context, AttributeSet attrs, int defStyleAttr) {
		this(context, attrs, defStyleAttr, 0);
	}
	
	public FilePicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		
		
	}

	@Override
	protected void onLayout(boolean updated, int l, int t, int r, int b)
	{
		
	}
}
