package com.asm.block;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import com.asm.block.elements.TextElement;


public class BlockView extends View
{

	public static final float SCALE = 1f;

	public static float ELEMENTS_MARGIN = 5 * SCALE/*dp*/;
	public static float START_MARGIN = 15 * SCALE;
	public static float END_MERGIN = 10 * SCALE;
	public static float TOPBOTTOM_MARGIN = 8 * SCALE;
	public static float CONNECT_H_MARGIN = 14 * SCALE;
	public static float CONNECT_W_MARGIN = 15 * SCALE;



	private Paint paint;
	private Paint borderPaint;
	private BlockElements mElements;

	//속성
	private int mBlockType = Type.TYPE_BOOL;
	private int mBlockColor = Color.parseColor("#4c97ff");
	private int id;

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
		id = BlocksData.getNewId();
		
		setOnLongClickListener(new View.OnLongClickListener() {

								   // Defines the one method for the interface, which is called when the View is long-clicked
								   public boolean onLongClick(View v) {

									   // Create a new ClipData.
									   // This is done in two steps to provide clarity. The convenience method
									   // ClipData.newPlainText() can create a plain text ClipData in one step.

									   // Create a new ClipData.Item from the ImageView object's tag
									   

									   // 태그 생성
									   //ClipData.Item item = new ClipData.Item(event.getX() + " " + event.getY());

									  // String[] mimeTypes = { ClipDescription.MIMETYPE_TEXT_PLAIN };
									   //ClipData data = new ClipData(String.valueOf(id), mimeTypes, item);



									   // Instantiates the drag shadow builder.
									   View.DragShadowBuilder myShadow = ((EditorView)getParent()).getDragShadowBuulder(v);

									   // Starts the drag
									   v.setVisibility(View.INVISIBLE);
									   v.startDrag(null,  // the data to be dragged
												   myShadow,  // the drag shadow builder
												   BlockView.this,      // no need to use local data
												   0          // flags (not currently used, set to 0)
												   );
									   return true;
								   }
							   });



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

	}


	public static void initMargin(Context context){
		ELEMENTS_MARGIN = TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				ELEMENTS_MARGIN,
				context.getResources().getDisplayMetrics());
		START_MARGIN = TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				START_MARGIN,
				context.getResources().getDisplayMetrics());
		END_MERGIN = TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				END_MERGIN,
				context.getResources().getDisplayMetrics());
		TOPBOTTOM_MARGIN = TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				TOPBOTTOM_MARGIN,
				context.getResources().getDisplayMetrics());
		CONNECT_H_MARGIN = TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				CONNECT_H_MARGIN,
				context.getResources().getDisplayMetrics());
		CONNECT_W_MARGIN = TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				CONNECT_W_MARGIN,
				context.getResources().getDisplayMetrics());
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
		el.superView = BlockView.this;
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
	
	/*
	public void resize(){
		int width;
		int height;

		Point point = new Point();
		mElements.measure(point, ELEMENTS_MARGIN);

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
	}*/

	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int count = mElements.count();
		//if(event.getAction() == MotionEvent.ACTION_UP){
			for(int i = 0; i < count; i ++){
				BlockElement e = mElements.get(i);
				if(e.isTouched(event.getX(), event.getY())){
					if(e.onTouchEvent(event)){
						super.onTouchEvent(event);
						return true;
					}

				}
			}
		//}
		
		super.onTouchEvent(event);
		return true;
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

		float x;// = START_MARGIN + CONNECT_W_MARGIN + ELEMENTS_MARGIN;
		float y;// = CONNECT_H_MARGIN + TOPBOTTOM_MARGIN;
		
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




	


	private static class MyDragShadowBuilder extends View.DragShadowBuilder {

		// The drag shadow image, defined as a drawable thing
		private static Drawable shadow;
		private int width, height;
		// Defines the constructor for myDragShadowBuilder
		public MyDragShadowBuilder(View v) {

			// Stores the View parameter passed to myDragShadowBuilder.
			super(v);

			// Creates a draggable image that will fill the Canvas provided by the system.
			shadow = new ColorDrawable(Color.LTGRAY);
		}

		// Defines a callback that sends the drag shadow dimensions and touch point back to the
		// system.
		@Override
		public void onProvideShadowMetrics (Point size, Point touch) {
			// Defines local variables


			// Sets the width of the shadow to half the width of the original View
			width = getView().getWidth() / 2;

			// Sets the height of the shadow to half the height of the original View
			height = getView().getHeight() / 2;

			// The drag shadow is a ColorDrawable. This sets its dimensions to be the same as the
			// Canvas that the system will provide. As a result, the drag shadow will fill the
			// Canvas.
			shadow.setBounds(0, 0, width, height);

			// Sets the size parameter's width and height values. These get back to the system
			// through the size parameter.
			size.set(width, height);

			// Sets the touch point's position to be in the middle of the drag shadow
			touch.set(width / 2, height / 2);
		}

		// Defines a callback that draws the drag shadow in a Canvas that the system constructs
		// from the dimensions passed in onProvideShadowMetrics().
		@Override
		public void onDrawShadow(Canvas canvas) {

			// Draws the ColorDrawable in the Canvas passed in from the system.
			shadow.draw(canvas);
		}
	}

}
