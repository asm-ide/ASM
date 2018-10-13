package com.asm.block.elements;

import com.asm.block.BlockElement;
import com.asm.block.Theme;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;


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
	public void draw(Canvas canvas, int x, int y) {
		canvas.drawText(mText, x, y, mPaint);
	}
	
	@Override
	public void measure(PointF out) {
		out.set(mPaint.measureText(mText), mPaint.getTextSize());
	}
}
