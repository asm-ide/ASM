package com.asm.block;

import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;


public class EditorView extends ViewGroup
{


    // States.
    private static final byte NONE = 0;
    private static final byte DRAG = 1;
    private static final byte ZOOM = 2;

    private byte mode = NONE;

    // Matrices used to move and zoom image.
    private Matrix matrix = new Matrix();
    private Matrix matrixInverse = new Matrix();
    private Matrix savedMatrix = new Matrix();

    // Parameters for zooming.
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private float[] lastEvent = null;
    private long lastDownTime = 0l;

    private float[] mDispatchTouchEventWorkingArray = new float[2];
    private float[] mOnTouchEventWorkingArray = new float[2];


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mDispatchTouchEventWorkingArray[0] = ev.getX();
        mDispatchTouchEventWorkingArray[1] = ev.getY();
        mDispatchTouchEventWorkingArray = screenPointsToScaledPoints(mDispatchTouchEventWorkingArray);
        ev.setLocation(mDispatchTouchEventWorkingArray[0], mDispatchTouchEventWorkingArray[1]);
        return super.dispatchTouchEvent(ev);
    }

    public EditorView(Context context) {
        super(context);
        init(context);
    }

    public EditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EditorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
		setOnDragListener(new myDragEventListener());
    }


    /**
     * Determine the space between the first two fingers
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private float[] scaledPointsToScreenPoints(float[] a) {
        matrix.mapPoints(a);
        return a;
    }

    private float[] screenPointsToScaledPoints(float[] a) {
        matrixInverse.mapPoints(a);
        return a;
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        float[] values = new float[9];
        matrix.getValues(values);
        canvas.save();
        canvas.translate(values[Matrix.MTRANS_X], values[Matrix.MTRANS_Y]);
        canvas.scale(values[Matrix.MSCALE_X], values[Matrix.MSCALE_Y]);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // handle touch events here
        mOnTouchEventWorkingArray[0] = event.getX();
        mOnTouchEventWorkingArray[1] = event.getY();

        mOnTouchEventWorkingArray = scaledPointsToScreenPoints(mOnTouchEventWorkingArray);

        event.setLocation(mOnTouchEventWorkingArray[0], mOnTouchEventWorkingArray[1]);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                mode = DRAG;
                lastEvent = null;
                long downTime = event.getDownTime();
                if (downTime - lastDownTime < 300l) {
                    float density = getResources().getDisplayMetrics().density;
                    if (Math.max(Math.abs(start.x - event.getX()), Math.abs(start.y - event.getY())) < 40.f * density) {
                        savedMatrix.set(matrix);
                        mid.set(event.getX(), event.getY());
                        mode = ZOOM;
                        lastEvent = new float[4];
                        lastEvent[0] = lastEvent[1] = event.getX();
                        lastEvent[2] = lastEvent[3] = event.getY();
                    }
                    lastDownTime = 0l;
                } else {
                    lastDownTime = downTime;
                }
                start.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                lastEvent = new float[4];
                lastEvent[0] = event.getX(0);
                lastEvent[1] = event.getX(1);
                lastEvent[2] = event.getY(0);
                lastEvent[3] = event.getY(1);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                lastEvent = null;
                break;
            case MotionEvent.ACTION_MOVE:
                final float density = getResources().getDisplayMetrics().density;
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    float dx = event.getX() - start.x;
                    float dy = event.getY() - start.y;
                    matrix.postTranslate(dx, dy);
                    matrix.invert(matrixInverse);
                    if (Math.max(Math.abs(start.x - event.getX()), Math.abs(start.y - event.getY())) > 20.f * density) {
                        lastDownTime = 0l;
                    }
                } else if (mode == ZOOM) {
                    if (event.getPointerCount() > 1) {
                        float newDist = spacing(event);
                        if (newDist > 10f * density) {
                            matrix.set(savedMatrix);
                            float scale = (newDist / oldDist);
                            matrix.postScale(scale, scale, mid.x, mid.y);
                            matrix.invert(matrixInverse);
                        }
                    } else {
                        matrix.set(savedMatrix);
                        float scale = event.getY() / start.y;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                        matrix.invert(matrixInverse);
                    }
                }
                break;
        }

        invalidate();
        return true;
    }

	
	
	
	
	public class myDragEventListener implements View.OnDragListener {

		// This is the method that the system calls when it dispatches a drag event to the
		// listener.
		public boolean onDrag(View v, DragEvent event) {

			// Defines a variable to store the action type for the incoming event
			final int action = event.getAction();

			// Handles each of the expected events
			switch(action) {

				case DragEvent.ACTION_DRAG_STARTED:

					if((event.getLocalState()) instanceof BlockView){
						return true;
					}else{
						return false;
					}
				case DragEvent.ACTION_DROP:

                    View view = (View) event.getLocalState();
                    Point p = getTouchPositionFromDragevent(v, event);

                    Log.d("DropEvent", "Action Drop : X = " + event.getX() + " Y = " + event.getY());

					//String data[] = ((String)event.getClipData().getItemAt(0).getText()).split(" ");
					float[] values = new float[9];
					matrix.getValues(values);
					
					float mx = view.getWidth() / 2;
					float my = view.getHeight() / 2;
					
					float[] location = screenPointsToScaledPoints(new float[]{p.x, p.y});
                    view.setX(location[0] -mx);
                    view.setY(location[1] - my);
                    view.setVisibility(View.VISIBLE);
			}

			return true;
		}

		public Point getTouchPositionFromDragevent(View item, DragEvent event){
			int pos[] = new int[2];
			item.getLocationOnScreen(pos);

			Rect rItem = new Rect();
			item.getGlobalVisibleRect(rItem);

			return new Point(rItem.left+ Math.round(event.getX()) - pos[0],
							 rItem.top + Math.round(event.getY()) - pos[1]);
		}

	};
	
	
	public MyDragShadowBuilder getDragShadowBuulder(View v){
		return new MyDragShadowBuilder(v);
	}
	
	private class MyDragShadowBuilder extends View.DragShadowBuilder {

		
		int width, height;
		// Defines the constructor for myDragShadowBuilder
		public MyDragShadowBuilder(View v) {
			// Stores the View parameter passed to myDragShadowBuilder.
			super(v);
		}

		// Defines a callback that sends the drag shadow dimensions and touch point back to the
		// system.
		@Override
		public void onProvideShadowMetrics (Point size, Point touch) {
			// Defines local variables

			float[] values = new float[9];
			matrix.getValues(values);
			
			width = (int)(getView().getWidth() * values[Matrix.MSCALE_X]);
			height = (int)(getView().getHeight() * values[Matrix.MSCALE_Y]);
			
			
			size.set(width, height);

			// Sets the touch point's position to be in the middle of the drag shadow
			touch.set(width / 2, height / 2);
		}

		// Defines a callback that draws the drag shadow in a Canvas that the system constructs
		// from the dimensions passed in onProvideShadowMetrics().
		@Override
		public void onDrawShadow(Canvas canvas){
			Bitmap bit = Bitmap.createBitmap(getView().getWidth(),getView().getHeight(),Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(bit);
			getView().draw(c);
			
			float[] values = new float[9];
			matrix.getValues(values);
			
			Matrix m = new Matrix();
			m.setScale(values[Matrix.MSCALE_X],values[Matrix.MSCALE_Y]);
			canvas.drawBitmap(bit,m,null);
		}
	}
	
}
