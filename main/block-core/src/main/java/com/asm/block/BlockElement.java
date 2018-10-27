package com.asm.block;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;


public abstract class BlockElement
{
	private Theme mTheme;
	public BlockElements superElements;

	public int x, y1, y2;
	public float measuredX, measuredY;

	public boolean isBlockAttachable = false;
	public boolean isAttached = false;
	public BlockView attachedView = null;

	public BlockElement(Theme theme) {
		mTheme = theme;
	}
	
	
	public Theme getTheme() {
		return mTheme;
	}

	public void draw(Canvas canvas, int x, int y1, int y2){
		this.x = x;
		this.y1 = y1;
		this.y2 = y2;

		onDraw(canvas, x, y1,y2);
	}

	public void measure(PointF out){
		PointF point = new PointF();
		onMeasure(point);
		this.measuredX = point.x;
		this.measuredY = point.y;
		out.set(point);
		Log.d("BlockElement", "x : " + measuredX + " y : " + measuredY);
	}

	public abstract void onDraw(Canvas canvas, int x, int y1, int y2);
	
	public abstract void onMeasure(PointF out);
	
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}

	public boolean isTouched(float x, float y){
	    boolean checkX = x > this.x && x < (this.x + this.measuredX);
	    boolean checkY = y > (y1 + y2) / 2 - (this.measuredY / 2) && y < (y1 + y2) / 2 + (this.measuredY / 2);

		Log.d("BlockElement",    "x : " + x + " y : " + y + " measuredX : " + this.measuredX + " measuredY : " + this.measuredY + " checkX : " + checkX + " checkY : " + checkY);

		return checkX && checkY;
	}

}
