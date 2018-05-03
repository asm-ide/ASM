package com.asm.widget.codeedit;

import com.asm.Settings;
import com.asm.text.TextData;
import com.asm.text.TextDraw;
import com.asm.annotation.NonNull;
import com.asm.annotation.Nullable;

import android.support.v4.view.ViewCompat;

import android.view.View;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Scroller;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.text.TextWatcher;
import android.text.TextPaint;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.util.Log;
import android.os.Parcel;
import android.os.Parcelable;


/**
 * The EditText for fast perfomance, and customize styles.
 * You can customize view style by override this.
 * <code>onDrawLine(Canvas canvas, int index, int line, String text, float y);
 * onDrawCharactors(Canvas canvas, int index, int lines, int cols, float x, float y);
 */
public class ScrollingTextView extends View implements TextsDrawingInterface, GestureDetector.OnGestureListener, CharSequence
{
	/** used when if user pointer is move than this, will scrolled. */
	private static final float MAXSCROLLPOS = 3f;
	
	/** log tag */
	private static final String TAG = " ScrollingTextView";
	
	/** text data factory. */
	private TextData.Factory mFactory;
	
	/**
	 * text content.
	 * must not be null, but can be "".
	 */
	@NonNull TextData mData;
	
	/**
	 * the text drawing utils that determine TextPaint
	 * and help rendering texts.
	 */
	@NonNull TextDraw mDraw;
	
	// TEMP
	private TextPaint lineNumPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
	
	
	/** main scroller that scrolling text contents. */
	private Scroller mScroller;
	
	/** gesture detector for scroll */
	protected GestureDetector mGestureDetector;
	
	/** temp scroll position. not describe current scroll position */
	private float mScrollX, mScrollY, mDownX, mDownY;
	
	/** shows that scrolling is being started. */
//	private boolean isScrollStarted = false; //while end
//	
//	/**
//	 * shows scrolling way. True for scroll x, and
//	 * false for scrolling y.
//	 */
//	private boolean isScrollVertical;
//	
//	/** last x and y when touch down. */
//	private float lastx, lasty;
	
	
	public ScrollingTextView(Context context) {
		this(context, null);
	}
	
	public ScrollingTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public ScrollingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		this(context, attrs, defStyleAttr, 0);
	}
	
	public ScrollingTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		
		init();
		
		Settings.loadSettingIfNotLoaded(context);
		mDraw = TextDraw.create(this);
		mData = mFactory.newEditable("package com.lhw;\na\nb\npublic class Test\n{\n\tpublic int a = 0; \n}\n\n\n\n\n\n\nho"); //TODO: TEMP
		mData.setDraw(mDraw);
		setTextSize(Settings.textSize);
	}
	
	/**
	 * Initialize class.
	 * Make sure that this can be called when read from
	 * parcel and needs to re-initialize.
	 */
	protected void init() {
		Context context = getContext();
		
		mScroller = new Scroller(context);
		mFactory = new TextData.Factory();
		
		lineNumPaint.setColor(0x44000000);
		
		mGestureDetector = new GestureDetector(context, this);
	}
	
	/**
	 * add a text watcher listener.
	 */
	public void addOnTextChangedListener(TextWatcher l) {
		mData.addOnTextChangedListener(l);
	}
	
	/**
	 * remove a text watcher listener.
	 * returns true, when removing was successful.
	 */
	public boolean removeOnTextChangedListener(TextWatcher l) {
		return mData.removeOnTextChangedListener(l);
	}
	
	/**
	 * set the main text color.
	 */
	public void setTextColor(int textColor) {
		mDraw.getPaint().setColor(textColor);
	}
	
	/**
	 * return the main text color.
	 */
	public int getTextColor() {
		return mDraw.getPaint().getColor();
	}
	
	/**
	 * Set the text size in sp unit.
	 */
	public float setTextSize(float size) {
		return setTextSize(size, TypedValue.COMPLEX_UNIT_SP);
	}
	
	/**
	 * Set the text size.
	 */
	public float setTextSize(float size, int unit) {
		float realSize = convertUnit(size, unit);
		mDraw.setTextSize(realSize);
		lineNumPaint.setTextSize(realSize);
		return realSize;
	}
	
	/**
	 * Return the text size.
	 */
	public float getTextSize() {
		return mDraw.getTextSize();
	}
	
	/**
	 * Set the current text paint.
	 * This paint will used when drawing texts.
	 */
	public void setTextPaint(@NonNull TextPaint textPaint) {
		mDraw.setPaint(textPaint);
	}
	/**
	 * Get the current text paint.
	 */
	public @NonNull TextPaint getTextPaint() {
		return mDraw.getPaint();
	}
	
	/**
	 * Set the current text value.
	 * @param text new text value
	 */
	public void setText(@NonNull CharSequence text) {
		mData.setText(text);
		invalidate();
	}
	
	/**
	 * Set the current text value.
	 * @params data new text value
	 */
	public void setText(@NonNull TextData data) {
		mData.setText(data);
		invalidate();
	}
	
	/**
	 * Returns the current text value
	 * @return current text value
	 */
	public @NonNull TextData getText() {
		return mData;
	}
	
	/**
	 * Convince method than getText().charAt().
	 */
	public char charAt(int i) {
		return mData.charAt(i);
	}
	
	/**
	 * Convince method than getText().append().
	 */
	public TextData append(CharSequence text) {
		mData.append(text);
		return mData;
	}
	
	/**
	 * Convince method than getText().delete().
	 */
	public TextData delete(int start, int end) {
		mData.delete(start, end);
		return mData;
	}
	
	/**
	 * Convince method than getText().length().
	 */
	 
	public int length() {
		return mData.length();
	}
	
	/**
	 * Convince method than getText().substring().
	 */
	public String substring(int start, int end) {
		return mData.substring(start, end);
	}
	
	/**
	 * Convince method than getText().subSequence().
	 */
	public CharSequence subSequence(int start, int end) {
		return mData.subSequence(start, end);
	}
	
	/**
	 * Convince method than getText().indexOf().
	 */
	public int indexOf(String text) {
		return mData.indexOf(text);
	}
	
	/**
	 * Convince method than getText().indexOf().
	 */
	public int indexOf(String text, int fromIndex) {
		return mData.indexOf(text, fromIndex);
	}
	
	/**
	 * Convince method than getText().substring().
	 */
	public int indexOf(String text, int fromIndex, int endIndex) {
		return getText().indexOf(text, fromIndex, endIndex);
	}
	
	/**
	 * Set the TextDraw of this CodeEdit.
	 * @see com.asm.widget.codeedit.TextDraw
	 */
	public void setDraw(@NonNull TextDraw draw) {
		mDraw = draw;
	}
	
	/** return the TextDraw of this CodeEdit. */
	public @NonNull TextDraw getDraw(){
		return mDraw;
	}
	
	public void setTextFactory(TextData.Factory factory) {
		mFactory = factory;
	}
	
//	/** {@hide} */
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		switch(event.getAction()) {
//			case MotionEvent.ACTION_DOWN:
//				lastx = event.getX();
//				lasty = event.getY();
//				isScrollStarted = false;
//				break;
//				
//			case MotionEvent.ACTION_MOVE:
//				if(isScrollStarted) {
//					scroller.computeScrollOffset();
//					int curx = scroller.getCurrX();
//					int cury = scroller.getCurrY();
//					int finalx, finaly;
//					if(isScrollVertical) {
//						finalx = (int) (cury + event.getX() - lastx);
//						finaly = cury;
//					} else {
//						finalx = curx;
//						finaly = (int) (cury + event.getY() - lastx);
//					}
//					if(finalx < 0) finalx = 0;
//					else if(finalx > getScrollableWidth() - getWidth()) finalx = getScrollableWidth() - getWidth();
//					if(finaly < 0) finaly = 0;
//					else if(finaly > getScrollableHeight() - getHeight()) finaly = getScrollableHeight() - getHeight();
//					
//					scroller.forceFinished(true);
//					Log.d(TAG, "scroll v=" + isScrollVertical + " finalx=" + finalx + ", finaly=" + finaly);
//					invalidate();
//				} else {
//					if(Math.abs(lastx - event.getX()) > MAXSCROLLPOS) {
//						//start x scroll
//						isScrollStarted = true;
//						isScrollVertical = true;
//						//recall to compute scroll
//						return onTouchEvent(event);
//					}
//					else if(Math.abs(lasty - event.getY()) > MAXSCROLLPOS) {
//						//start y scroll
//						isScrollStarted = true;
//						isScrollVertical = false;
//						//recall to compute scroll
//						return onTouchEvent(event);
//					}
//				}
//		}
//		return true;
//	}
	
	public boolean onTouchEvent(MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}
	
	/** {@hide} */
	@Override
	public boolean onDown(MotionEvent e) {
		mScroller.forceFinished(true);
		mScrollX = mScroller.getCurrX();
		mScrollY = mScroller.getCurrY();
		mDownX = e.getX();
		mDownY = e.getY();
		ViewCompat.postInvalidateOnAnimation(this);
		return false;
	}
	
	/** {@hide} */
	@Override
	public void onShowPress(MotionEvent e) {
		
	}
	
	/** {@hide} */
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
	
	/** {@hide} */
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//		scroller.forceFinished(true);
//		scroller.setFinalX((int) (scrollX - distanceX));
//		scroller.setFinalY((int) (scrollY - distanceY));
//		Log.d("scroll", (scrollX - distanceX) + "," + (scrollY - distanceY));
//		ViewCompat.postInvalidateOnAnimation(this);
		mScroller.setFinalX((int) (mScrollX + e2.getX() - mDownX));
		mScroller.setFinalY((int) (mScrollY + e2.getY() - mDownY));
		mScroller.abortAnimation();
		invalidate();
		//ViewCompat.postInvalidateOnAnimation(this);
		return false;
	}
	
	/** {@hide} */
	@Override
	public void onLongPress(MotionEvent e1) {
		//setBackgroundColor(0x22ffff00);
	}
	
	/** {@hide} */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		mScroller.forceFinished(true);
		mScroller.fling(
			(int) (mScrollX - (e1.getX() - mDownX)),
			(int) (mScrollY - (e1.getY() - mDownY)),
			(int) velocityX,
			(int) velocityY,
			0, 0, getScrollableWidth(), getScrollableHeight());
		
		ViewCompat.postInvalidateOnAnimation(this);
		return true;
	}
	
	/**
	 * Returns the each lines height.
	 */
	public float getLineHeight() {
		return mDraw.getTextSize();
	}
	
	/**
	 * Return the scrollable width.
	 */
	public int getScrollableWidth() {
		return 2000; // TODO
	} //return Math.max(data.getScrollableWidth(), getWidth()); }
	
	/**
	 * Teturn the scrollable height.
	 */
	public int getScrollableHeight() {
		return 3000; // TODO
	}//Math.max(data.getScrollableHeight(), getHeight()); }
	
	/**
	 * Return position data.
	 * @see com.asm.widget.codeedit.TextData.PositionData
	 */
	public TextData.PositionData getPositionToX(int position, int startLine, int startPos) {
		return mData.getPositionData(position, startLine, startPos);
	}
	
	public Scroller getScroller() {
		return mScroller;
	}
	
	/** must widthMode is exactly */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int width = 0, height = 0;
		
		switch(widthMode) {
			case MeasureSpec.EXACTLY: width = widthSize; break;
			case MeasureSpec.AT_MOST: width = Math.max(widthSize, mData.getScrollableWidth()); break;
			case MeasureSpec.UNSPECIFIED: width = Math.max((int) convertUnit(20, TypedValue.COMPLEX_UNIT_SP), mData.getScrollableWidth()); break;
		}
		
		switch(heightMode) {
			case MeasureSpec.EXACTLY: height = heightSize; break;
			case MeasureSpec.AT_MOST: height = Math.max(heightSize, mData.getScrollableHeight()); break;
			case MeasureSpec.UNSPECIFIED: height = Math.max((int) convertUnit(20, TypedValue.COMPLEX_UNIT_SP), mData.getScrollableHeight());
		}
		setMeasuredDimension(width, height);
	}
	
//	/** {@hide} */
//	@Override
//	public void computeScroll()
//	{
//		super.computeScroll();
//		if(scroller.computeScrollOffset()) {
//			scrollTo(scroller.getCurrX(), scroller.getCurrY());
//			ViewCompat.postInvalidateOnAnimation(this);
//		}
//	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
	}
	
	/**
	 * Used when save the instance state of this view.
	 */
	private static class SavedState extends BaseSavedState
	{
		TextData data;
		TextDraw draw;
		int scrollX, scrollY;
		
		
		public SavedState(Parcelable superState) {
			super(superState);
		}
		
		public SavedState(Parcel p, ClassLoader loader) {
			super(p, loader);
			
			data = p.readParcelable(loader);
			draw = p.readParcelable(loader);
			scrollX = p.readInt();
			scrollY = p.readInt();
		}
		
		@Override
		public void writeToParcel(Parcel p, int flags) {
			super.writeToParcel(p, flags);
			p.writeParcelable(data, 0);
			p.writeParcelable(draw, 0);
			p.writeInt(scrollX);
			p.writeInt(scrollY);
		}
		
		public static final Parcelable.ClassLoaderCreator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() {
			@Override
			public SavedState createFromParcel(Parcel p) {
				throw new IllegalStateException("use createFromParcel(Parcel, ClassLoader)");
			}

			@Override
			public SavedState createFromParcel(Parcel p, ClassLoader loader) {
				return new SavedState(p, loader);
			}
			
			@Override
			public SavedState[] newArray(int l) {
				return new SavedState[l];
			}
		};
	}
	
	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState state = new SavedState(superState);
		state.data = mData;
		state.draw = mDraw;
		mScroller.computeScrollOffset();
		state.scrollX = mScroller.getCurrX();
		state.scrollY = mScroller.getCurrY();
		return state;
	}
	
	@Override
	protected void onRestoreInstanceState(Parcelable p) {
		SavedState state = (SavedState) p;
		super.onRestoreInstanceState(state.getSuperState());
		init();
		
		mData = state.data;
		mDraw = state.draw;
		mScroller.setFinalX(state.scrollX);
		mScroller.setFinalY(state.scrollY);
		mScroller.abortAnimation();
	}
	
	/**
	 * When called at drawing.
	 * You can make enough ui for not extending onDraw,
	 * instead, extends @link{onDrawLine()} or @link{onDrawCharacter()}.
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Log.d(TAG, "length " + mData.length());
		boolean animating = mScroller.computeScrollOffset();
		//computeScroll();
		//canvas.drawColor(0x1fff0000);
		canvas.translate(mScroller.getCurrX(), mScroller.getCurrY());
		//canvas.drawRect(-50, -50, 50, 50, draw.getPaint());
		TextPaint paint = mDraw.getPaint();
		float lineMargin = mDraw.getLineMargin();
		float lineSpacing = mDraw.getLineSpacing();
		
		float showsStartY = mScroller.getCurrY();
		int showsStartLines = Math.max((int) (showsStartY / lineSpacing), 0);
		int showsEndLines = mData.lines(); // TODO //Math.min((int) (showsStartY + getHeight() / lineSpacing) + 1, data.lines());
		
		int showsStart = mData.getLinePosition(showsStartLines);
		int _showsEnd = mData.getLinePosition(showsEndLines, showsStart, showsStartLines);
		//Log.d(TAG, data.indexOf("\n", _showsEnd) + "," + data.indexOf("\n", Math.max(0, _showsEnd - 1)) + "," + data.length());
		
		int showsEnd = _showsEnd;
		if(showsEnd == -1) showsEnd = mData.length();
		//else showsEnd--;
		int startIndex = showsStart, endIndex = mData.indexOf("\n", showsStart);
		
		int i = showsStartLines;
		float y = showsStartY % lineSpacing - lineSpacing;
		Log.d(".", "draw" + showsStart + " to " + showsEnd + ", line" + showsStartLines + " to" + showsEndLines);
		
		//TEMP
		canvas.translate(100, -y); // TODO
		
		while(endIndex != -1){
			//Log.d(TAG, "before " + startIndex + "~" + endIndex);
			//TODO
			//Log.d(TAG, startIndex + "~" + endIndex + ", y=" + y);
			
			if(y >= mScroller.getCurrY()) {
				if(y > getHeight() + mScroller.getCurrY()) break;
				//endIndex = getText().indexOf("\n", startIndex + 1, showsEnd);
				onDrawLine(canvas, //canvas
						   startIndex, //index
						   i, //line
						   mData.substring(startIndex, endIndex), //text
						   y); //yes! it's y!!!
			}
			y += paint.getTextSize() + lineMargin;
			//canvas.drawText("l", 0, i * draw.getLineSpacing() - draw.getLineMargin(), paint);
			i++;
			
			startIndex = endIndex + 1;
			
			endIndex = mData.indexOf("\n", startIndex + 1, showsEnd);
		}
		//if(endIndex != -1) endIndex = mData.length();
		onDrawLine(canvas, startIndex, i, getText().substring(startIndex, showsEnd), y);
		
		//canvas.drawText("e", 20, i * draw.getLineSpacing() - draw.getLineMargin(), paint);
		
		if(animating) ViewCompat.postInvalidateOnAnimation(this);
	}
	
	/**
	 * Called when draw each lines.
	 */
	public void onDrawLine(Canvas canvas, int index, int line, String text, float y) {
		Log.d(TAG, "drawLine index " + index + ", lines = " + line + ", y = " + y + "\t\t" + text);
		canvas.drawText(Integer.toString(line + 1), -100, y, lineNumPaint);
		float curx = 0;
		for(int i = 0; i < text.length(); i++) {
			curx += onDrawCharacter(canvas, //canvas
								    index + i, //index
									line, //line
									i, //col
									curx, //x
									y); //as you know
		}
	}
	
	/**
	 * Called when draw each character
	 */
	public float onDrawCharacter(Canvas canvas, int index, int line, int col, float x, float y) {
		TextPaint paint = mDraw.getPaint();
		//Log.d(TAG, "drawChar x=" + x + ", y=" + y);
		switch(mData.charAt(index)) {
			case '\t':
				final float spaceWidth = paint.measureText(" ");
				final float tabWidth = spaceWidth * 4;
				final float tabsForNowDivider = x / tabWidth; 
				int tabsForNow = (int) (tabsForNowDivider);
				if(x % tabWidth == 0) {
					tabsForNow += tabWidth;
				} else {
					tabsForNow += tabWidth - tabsForNowDivider;
				}
				return tabsForNow;
				
			
		}
		float textx = paint.measureText(String.valueOf(new char[]{charAt(index)}));
		
		canvas.drawText(new char[]{getText().charAt(index)}, 0, 1, x, y, paint);
		//Log.d(TAG, "canvas.drawText(new char[]{" + getText().charAt(index) + "}, 0, 1, " + x + ", " + y + ", " + paint);
		return textx;
	}
	
	/** temp */
	private float convertUnit(float data, int unit) {
		return TypedValue.applyDimension(unit, data, getResources().getDisplayMetrics());
	}
}
