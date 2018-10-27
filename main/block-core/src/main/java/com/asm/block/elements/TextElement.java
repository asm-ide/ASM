package com.asm.block.elements;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.asm.block.BlockElement;
import com.asm.block.Theme;


public class TextElement extends BlockElement
{
	private String mText;
	private Paint mPaint;
	
	
	public TextElement(String text, Theme theme) {
		super(theme);
		
		mText = text;
		
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(theme.foreground);
		mPaint.setTextSize(theme.textSize);
	}
	
	public String getText() {
		return mText;
	}
	
	public void setText(String text) {
		mText = text;
	}
	
	
	@Override
	public void onDraw(Canvas canvas, int x, int y1, int y2) {
		int yPos = (int) ((y1+y2)/2 - ((mPaint.descent() + mPaint.ascent()) / 2)) ;


		canvas.drawText(mText, x, yPos, mPaint);
	}
	
	@Override
	public void onMeasure(PointF out) {
		out.set(mPaint.measureText(mText), mPaint.getTextSize());
	}
}
