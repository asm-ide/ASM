package com.asm.block;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.view.MotionEvent;


public abstract class BlockElement
{
	private Theme mTheme;
	
	
	public BlockElement(Theme theme) {
		mTheme = theme;
	}
	
	
	public Theme getTheme() {
		return mTheme;
	}
	
	
	public abstract void draw(Canvas canvas, int x, int y1, int y2);
	
	public abstract void measure(PointF out);
	
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}
}
