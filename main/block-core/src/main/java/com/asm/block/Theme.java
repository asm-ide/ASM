package com.asm.block;

import android.content.Context;
import android.util.TypedValue;


public class Theme
{
	public boolean isDark = false;
	public int foreground = 0xff070707;
	public int foregroundInvert = 0xfff5f5f5;
	public float textSize;
	
	
	public Theme(Context context) {
		// TODO: textSize setting available
		textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16f, context.getResources().getDisplayMetrics());
	}
}
