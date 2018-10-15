package com.asm.block;

import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;
import com.asm.block.elements.*;


public class BlockView extends View
{

	public static final float SCALE = 1f;

	public float ELEMENTS_MARGIN = 15 * SCALE/*dp*/;
	public float START_MARGIN = 15 * SCALE;
	public float END_MERGIN = 10 * SCALE;
	public float TOPBOTTOM_MARGIN = 10 * SCALE;
	public float CONNECT_H_MARGIN = 14 * SCALE;
	public float CONNECT_W_MARGIN = 15 * SCALE;


	private Paint paint;
	private Paint borderPaint;
	private BlockElements mElements;

	//속성
	private int mBlockType = Type.TYPE_BOOL;
	private int mBlockColor = Color.parseColor("#4c97ff");


	public static class Type
	{
		//Block Type
		public static final int TYPE_DEFUALT = 0;
		public static final int TYPE_END = 1;

		public static final int TYPE_NUMBER = 2;
		public static final int TYPE_BOOL = 3;
		public static final int TYPE_STRING = 4;
	}


	public BlockView(Context context)
	{
		this(context, null);
	}

	public BlockView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public BlockView(final Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr); 
		initPaint();
		setElements(new BlockElements(new Theme(context)){
				public int count()
				{
					return 1;
				}
				public BlockElement get(int index)
				{
					if (index == 0)
						return new TextElement("This is a Element Block", new Theme(context));
					else
						return new TextElement("Next Element", new Theme(context));
				}
			});
		ELEMENTS_MARGIN = TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_DIP,
			ELEMENTS_MARGIN, 
			getResources().getDisplayMetrics());
		START_MARGIN = TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_DIP,
			START_MARGIN, 
			getResources().getDisplayMetrics());
		END_MERGIN = TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_DIP,
			END_MERGIN, 
			getResources().getDisplayMetrics());
		TOPBOTTOM_MARGIN = TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_DIP,
			TOPBOTTOM_MARGIN, 
			getResources().getDisplayMetrics());
		CONNECT_H_MARGIN = TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_DIP,
			CONNECT_H_MARGIN, 
			getResources().getDisplayMetrics());
		CONNECT_W_MARGIN = TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_DIP,
			CONNECT_W_MARGIN, 
			getResources().getDisplayMetrics());
	}

	private void initPaint()
	{
		paint = new Paint();
		paint.setColor(mBlockColor);
		paint.setStrokeWidth(0);
		paint.setStyle(Paint.Style.FILL);

		borderPaint = new Paint();
		borderPaint.setColor(darker(mBlockColor, 0.8f));
		borderPaint.setStyle(Paint.Style.STROKE);
		borderPaint.setStrokeWidth(4);
	}

	public BlockElements getElements()
	{
		return mElements;
	}

	public void setElements(BlockElements el)
	{
		mElements = el;
	}

	public void setColor(int color)
	{
		mBlockColor = color;
		initPaint();
	}

	public int getColor()
	{
		return mBlockColor;
	}

	public void setType(int type)
	{
		mBlockType = type;
	}

	public int getType()
	{
		return mBlockType;
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int width;
		int height;

		Point point = new Point();
		mElements.measure(point,
						  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
													ELEMENTS_MARGIN,
													getResources().getDisplayMetrics()));


		switch (MeasureSpec.getMode(widthMeasureSpec))
		{
			case MeasureSpec.UNSPECIFIED:
			case MeasureSpec.AT_MOST:
				width = point.x;
				break;

			case MeasureSpec.EXACTLY:
				width = MeasureSpec.getSize(widthMeasureSpec);
				break;

			default:
				throw new IllegalStateException();
		}

		switch (MeasureSpec.getMode(heightMeasureSpec))
		{
			case MeasureSpec.UNSPECIFIED:

				height = point.y;
				break;
			case MeasureSpec.AT_MOST:

				height = point.y;
				break;

			case MeasureSpec.EXACTLY:
				height = MeasureSpec.getSize(heightMeasureSpec);
				break;

			default:
				throw new IllegalStateException();
		}

		if (mBlockType == Type.TYPE_DEFUALT || mBlockType == Type.TYPE_END)
		{
			setMeasuredDimension((int)(width + START_MARGIN + END_MERGIN + CONNECT_W_MARGIN + ELEMENTS_MARGIN * 2), 
								 (int)(height + TOPBOTTOM_MARGIN * 2 + CONNECT_H_MARGIN));
		}
		else
		{
			setMeasuredDimension((int)(width + START_MARGIN * 2 + ELEMENTS_MARGIN * 2), 
								 (int)(height + TOPBOTTOM_MARGIN * 2));
		}
	}
	
	public void resize(){
		int width;
		int height;

		Point point = new Point();
		mElements.measure(point,
						  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
													ELEMENTS_MARGIN,
													getResources().getDisplayMetrics()));

		width = point.x;
		height = point.y;
		
		if (mBlockType == Type.TYPE_DEFUALT || mBlockType == Type.TYPE_END)
		{
			setMeasuredDimension((int)(width + START_MARGIN + END_MERGIN + CONNECT_W_MARGIN + ELEMENTS_MARGIN * 2), 
								 (int)(height + TOPBOTTOM_MARGIN * 2 + CONNECT_H_MARGIN));
		}
		else
		{
			setMeasuredDimension((int)(width + START_MARGIN * 2 + ELEMENTS_MARGIN * 2), 
								 (int)(height + TOPBOTTOM_MARGIN * 2));
		}
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		onDrawBlockBackground(canvas);
		onDrawBlockForeground(canvas);
	}

	protected void onDrawBlockBackground(Canvas canvas)
	{
		switch (mBlockType)
		{
			case Type.TYPE_DEFUALT : 
				drawDefualtBlock(canvas);
				break;
			case Type.TYPE_END :
				drawEndBlock(canvas);
				break;
			case Type.TYPE_BOOL :
				drawBoolBlock(canvas);
				break;
			case Type.TYPE_NUMBER :
				drawNumberBlock(canvas);
				break;
			case Type.TYPE_STRING :
				drawStringBlock(canvas);
				break;
		}
	}


	private void drawNumberBlock(Canvas canvas)
	{
		Path path = new Path();
		path.moveTo(START_MARGIN+ELEMENTS_MARGIN, 0);
		path.cubicTo(START_MARGIN+ELEMENTS_MARGIN,0,0,canvas.getHeight()/2,START_MARGIN + ELEMENTS_MARGIN,canvas.getHeight());
		//path.arcTo(new RectF(0,0,START_MARGIN * 2, canvas.getHeight()), 90,180,true);
		path.lineTo(canvas.getWidth()-START_MARGIN-ELEMENTS_MARGIN, canvas.getHeight());
		path.cubicTo(canvas.getWidth()-START_MARGIN-ELEMENTS_MARGIN,canvas.getHeight(),canvas.getWidth(),canvas.getHeight()/2,canvas.getWidth()-START_MARGIN-ELEMENTS_MARGIN,0);
		//path.arcTo(new RectF(canvas.getWidth(), canvas.getHeight(),canvas.getWidth() - START_MARGIN *2 , 0 ),90, -180,true);
		path.lineTo(START_MARGIN+ELEMENTS_MARGIN, 0);
		
		drawPathWithBorder(canvas, path);
	}
	private void drawStringBlock(Canvas canvas)
	{
		Path path = new Path();
		path.moveTo(0, 0);
		path.lineTo(0, canvas.getHeight());
		path.lineTo(canvas.getWidth(), canvas.getHeight());
		path.lineTo(canvas.getWidth(), 0);
		path.lineTo(0, 0);

		drawPathWithBorder(canvas, path);
	}
	private void drawBoolBlock(Canvas canvas)
	{
		Path path = new Path();
		float margin = START_MARGIN;
		
		path.moveTo(margin, 0);
		path.lineTo(0, canvas.getHeight()/2);
		path.lineTo(margin, canvas.getHeight());
		path.lineTo(canvas.getWidth()-margin, canvas.getHeight());
		path.lineTo(canvas.getWidth(), canvas.getHeight()/2);
		path.lineTo(canvas.getWidth()-margin,0);
		path.lineTo(margin,0);

		drawPathWithBorder(canvas, path);
	}
	private void drawDefualtBlock(Canvas canvas)
	{
		Path path = new Path();
		path.moveTo(0, CONNECT_H_MARGIN);
		path.lineTo(0, canvas.getHeight());
		path.lineTo(START_MARGIN, canvas.getHeight());
		path.lineTo(START_MARGIN + CONNECT_W_MARGIN / 2, canvas.getHeight() - CONNECT_H_MARGIN);
		path.lineTo(START_MARGIN + CONNECT_W_MARGIN, canvas.getHeight());
		path.lineTo(canvas.getWidth(), canvas.getHeight());
		path.lineTo(canvas.getWidth(), CONNECT_H_MARGIN);
		path.lineTo(START_MARGIN + CONNECT_W_MARGIN, CONNECT_H_MARGIN);
		path.lineTo(START_MARGIN + CONNECT_W_MARGIN / 2, 0);
		path.lineTo(START_MARGIN, CONNECT_H_MARGIN);
		path.lineTo(0, CONNECT_H_MARGIN);

		drawPathWithBorder(canvas, path);
	}
	private void drawEndBlock(Canvas canvas)
	{
		Path path = new Path();
		path.moveTo(0, CONNECT_H_MARGIN);
		path.lineTo(0, canvas.getHeight());
		path.lineTo(canvas.getWidth(), canvas.getHeight());
		path.lineTo(canvas.getWidth(), CONNECT_H_MARGIN);
		path.lineTo(START_MARGIN + CONNECT_W_MARGIN, CONNECT_H_MARGIN);
		path.lineTo(START_MARGIN + CONNECT_W_MARGIN / 2, 0);
		path.lineTo(START_MARGIN, CONNECT_H_MARGIN);
		path.lineTo(0, CONNECT_H_MARGIN);

		drawPathWithBorder(canvas, path);
	}


	protected void onDrawBlockForeground(Canvas canvas)
	{
		final int count = mElements.count();

		float x = START_MARGIN + CONNECT_W_MARGIN + ELEMENTS_MARGIN;
		float y = CONNECT_H_MARGIN + TOPBOTTOM_MARGIN;
		
		if (mBlockType == Type.TYPE_DEFUALT || mBlockType == Type.TYPE_END)
		{
			x = START_MARGIN + CONNECT_W_MARGIN + ELEMENTS_MARGIN;
			y = CONNECT_H_MARGIN + TOPBOTTOM_MARGIN;
		}
		else
		{
			x = START_MARGIN + ELEMENTS_MARGIN;
			y = TOPBOTTOM_MARGIN;
		}
		PointF point = new PointF();

		for (int i = 0; i < count; i++)
		{
			BlockElement el = mElements.get(i);
			el.draw(canvas, (int) x, (int)(y), (int)(canvas.getHeight() - TOPBOTTOM_MARGIN));

			el.measure(point);
			x += point.x;
			x += ELEMENTS_MARGIN;
		}
	}

	private void drawPathWithBorder(Canvas canvas, Path path)
	{
		canvas.drawPath(path, paint);
		canvas.drawPath(path, borderPaint);
	}

	public static int darker(int color, float factor)
	{
		int a = Color.alpha(color);
		int r = Color.red(color);
		int g = Color.green(color);
		int b = Color.blue(color);

		return Color.argb(a,
						  Math.max((int)(r * factor), 0),
						  Math.max((int)(g * factor), 0),
						  Math.max((int)(b * factor), 0));
	}
}
