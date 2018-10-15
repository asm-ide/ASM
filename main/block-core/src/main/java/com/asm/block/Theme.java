package com.asm.block;

import android.content.Context;
import android.util.TypedValue;
import android.graphics.*;


public class Theme
{
	public boolean isDark = false;
	public int foreground = Color.WHITE;
	public int secondForeground = Color.BLACK;
	public int foregroundInvert = 0xfff5f5f5;
	public float textSize;
	
	
	public Theme(Context context) {
		// TODO: textSize setting available
		textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16f, context.getResources().getDisplayMetrics());
	}
}
