package com.asm.block;

import android.graphics.Point;
import android.graphics.PointF;


public abstract class BlockElements
{
	private Theme mTheme;
	
	
	public BlockElements(Theme theme) {
		mTheme = theme;
	}
	
	
	public Theme getTheme() {
		return mTheme;
	}
	
	
	public abstract BlockElement get(int index);
	
	public abstract int count();
	
	public void measure(Point out, float margin) {
		final int count = count();
		float x = 0;
		float y = 0;
		PointF temp = new PointF();
		
		for(int i = 0; i < count; i++) {
			get(i).measure(temp);
			x += temp.x;
			x += margin;
			y += temp.y;
		}
		
		out.set((int) (x - margin), (int) (y));
	}
}
