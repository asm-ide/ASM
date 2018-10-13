package com.asm.block;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;

import java.util.ArrayList;

import static android.view.View.MeasureSpec.*;
import android.util.TypedValue;


public class BlockView extends View
{
	private static final int ELEMENTS_MARGIN = 5/*dp*/;
	
	private int mBlockColor = 0xffaaaaaa;
	private BlockElements mElements;
	
	
	public BlockView(Context context) {
		this(context, null);
	}

	public BlockView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BlockView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr); 
	}
	
	
	public BlockElements getElements() {
		return mElements;
	}
	
	public void setElements(BlockElements el) {
		mElements = el;
	}
	
	public void setColor(int color) {
		mBlockColor = color;
	}
	
	public int getColor() {
		return mBlockColor;
	}
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width;
		int height;
		
		Point point = new Point();
		mElements.measure(point,
				TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
						ELEMENTS_MARGIN,
						getResources().getDisplayMetrics()));
		
		
		switch(getMode(widthMeasureSpec)) {
			case UNSPECIFIED:
			case AT_MOST:
				width = point.x;
				break;
			
			case EXACTLY:
				width = getSize(widthMeasureSpec);
				break;
			
			default:
				throw new IllegalStateException();
		}
		
		switch(getMode(heightMeasureSpec)) {
			case UNSPECIFIED:
			case AT_MOST:
				height = point.y;
				break;
			
			case EXACTLY:
				height = getSize(widthMeasureSpec);
				break;
			
			default:
				throw new IllegalStateException();
		}
		
		setMeasuredDimension(width, height);
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		onDrawBlockBackground(canvas);
		onDrawBlockForeground(canvas);
	}
	
	protected void onDrawBlockBackground(Canvas canvas) {
		// 받아랏! 공비제이님
		// block color: mBlockColor
		// 여기서 블럭 배경을 그려주세요
		
	}
	
	protected void onDrawBlockForeground(Canvas canvas) {
		final int count = mElements.count();
		final float elementsMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ELEMENTS_MARGIN, getResources().getDisplayMetrics());
		float x = 0;
		PointF point = new PointF();
		
		for(int i = 0; i < count; i++) {
			BlockElement el = mElements.get(i);
			el.draw(canvas, (int) x, 0);
			
			el.measure(point);
			x += point.x;
			x += elementsMargin;
		}
	}
}
