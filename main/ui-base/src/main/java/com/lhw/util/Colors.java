package com.lhw.util;

import android.graphics.Color;


public class Colors
{
	public static int darken(int color, float how) {
		return Color.argb(Color.alpha(color),
						 (int) (Color.red(color) * how),
						 (int) (Color.green(color) * how),
						 (int) (Color.blue(color) * how));
	}
}
