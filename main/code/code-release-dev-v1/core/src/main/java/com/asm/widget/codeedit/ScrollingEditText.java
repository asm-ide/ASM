package com.asm.widget.codeedit;

import com.asm.widget.R;
import com.asm.text.TextData;

import android.view.View;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Scroller;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.inputmethod.*;


/**
 * Flexible text editing view for android.
 * You can customize almost anything.
 */
public class ScrollingEditText extends ScrollingTextView
{
	/**
	 * this shows that cursor is currently invisible.
	 * @see com.asm.widget.codeedit.ScrollingEditText#getCursorMode()
	 */
	public static final int CURSOR_INVISIBLE = 0;
	
	/**
	 * cursor is single count.
	 * You can get it by getCursorPosition().
	 * @see com.asm.widget.codeedit.ScrollingEditText#getCursorMode()
	 */
	public static final int CURSOR_SINGLE = 1;
	
	/**
	 * cursor is couple count and selecting some text.
	 * You can get cursor position by getCursorPosition(int index).
	 * @see com.asm.widget.codeedit.ScrollingEditText#getCursorPosition(int)
	 */
	public static final int CURSOR_SELECT = 2;
	
	/**
	 * shows the first cursor.
	 * On mode CURSOR_SINGLE, return cursor position.
	 * On mode CURSOR_SELECT, return left cursor position.
	 * @see com.asm.widget.codeedit.ScrollingEditText#getCursorMode()
	 */
	public static final int CURSOR_LEFT = 3;
	
	/**
	 * shows the second cursor.
	 * On mode CURSOR_SINGLE, throws exception.
	 * On mode CURSOR_SELECT, return right cursor position.
	 * @see com.asm.widget.codeedit.ScrollingEditText#getCursorMode()
	 */
	public static final int CURSOR_RIGHT = 4;
	
	/** the cursor blank(show where current text typing) thin. */
	private static final int BLANKTHIN = 3;
	
	/** input method manager that used on input. */
	private InputMethodManager inputMethodManager;
	
	/** the cursor blank drawable. BLANKTHIN X textSize */
	private Drawable blankDrawable;
	
	/** the main cursor drawable. show when user touch text and hide when edit. */
	private Drawable cursorDrawable;
	
	/** the left cursor drawable. show when some text selected. */
	private Drawable leftCursorDrawable;
	
	/** the right cursor drawable. show when some text selected. */
	private Drawable rightCursorDrawable;
	
	/** used on cursor bounds. */
	private int cursorWidth;
	
	/** used on cursor bounds. */
	private int cursorHeight;
	
	/** cursor tint color. applied on all cursors. */
	private int cursorTint;
	
	/** cursor tint mode. applied on all cursors. */
	private PorterDuff.Mode cursorTintMode;
	
	/** tell whether text is editable and not disabled. */
	private boolean editable = true;
	
	/** determines that shows keyboard or not. */
	private boolean showSoftInputOnFocus = true;
	
	/** current cursor mode. one of CURSOR_INVISIBLE, CURSOR_SINGLE, CURSOR_SELECT. */
	private int cursorMode = CURSOR_INVISIBLE;
	
	/** will setted after touch down. one of CURSOR_INVISIBLE, CURSOR_SINGLE, CURSOR_LEFT, CURSOR_RIGHT. */
	private int touchingCursor = CURSOR_INVISIBLE;
	
	/** the position setted when touch down. */
	private float lastx, lasty;
	
	
	public ScrollingEditText(Context context) {
		this(context, null);
	}
	
	public ScrollingEditText(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	
	public ScrollingEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		this(context, attrs, defStyleAttr, 0);
	}
	
	/**
	 * Public constructor.
	 * Attributes can be: cursorTint: cursor tint color.
	 * 					  cursorTintMode: cursor tint mode / string. (ex:src_in)
	 */
	public ScrollingEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		
		setLongClickable(true);
		setFocusable(true);
		setFocusableInTouchMode(true);
		
		inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		
		//super.setOnLongClickListener(this);
		
		//UPDATE JAVADOC WHEN UPDATE!
		TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ScrollingTextView, defStyleAttr, defStyleRes);
		if(a.hasValue(R.styleable.ScrollingTextView_cursorTint)) setCursorTint(a.getColor(R.styleable.ScrollingTextView_cursorTint, 0));
		if(a.hasValue(R.styleable.ScrollingTextView_cursorTintMode)) setCursorTintMode(PorterDuff.Mode.valueOf(a.getString(R.styleable.ScrollingTextView_cursorTintMode).toUpperCase()));
		a.recycle();
	}
	
	/** set the cursor tint mode. applied to all shape cursor. */
	public void setCursorTintMode(PorterDuff.Mode mode) {
		this.cursorTintMode = mode;
		blankDrawable.setTintMode(mode);
		cursorDrawable.setTintMode(mode);
		leftCursorDrawable.setTintMode(mode);
		rightCursorDrawable.setTintMode(mode);
	}
	
	/** return the cursor tint mode. */
	public PorterDuff.Mode getCursorTintMode() { return cursorTintMode; }
	
	/** set cursor tint color. */
	public void setCursorTint(int color) {
		this.cursorTint = color;
		blankDrawable.setTint(color);
		cursorDrawable.setTint(color);
		leftCursorDrawable.setTint(color);
		rightCursorDrawable.setTint(color);
	}
	
	/** return cursor tint color. */
	public int getCursorTint() { return cursorTint; }
	
	/**return the cursor drawable. mode is one of: CURSOR_SINGLE, CURSOR_LEFT, CURSOR_RIGHT. */
	public Drawable getCursorDrawable(int mode) {
		switch(touchingCursor) {
			case CURSOR_LEFT: return leftCursorDrawable;
			case CURSOR_RIGHT: return rightCursorDrawable;
			case CURSOR_SINGLE: return cursorDrawable;
			default: throw new IllegalArgumentException("unknown mode");
		}
	}
	
	/** set the cursor drawable. mode is one of: CURSOR_SINGLE, CURSOR_LEFT, CURSOR_RIGHT. */
	public void setCursorDrawable(int mode, Drawable cursor) {
		switch(touchingCursor) {
			case CURSOR_LEFT: leftCursorDrawable = cursor;
			case CURSOR_RIGHT: rightCursorDrawable = cursor;
			case CURSOR_SINGLE: cursorDrawable = cursor;
			default: throw new IllegalArgumentException("unknown mode");
		}
	}
	
	/** return the cursor drawable. mode is one of: CURSOR_SINGLE, CURSOR_LEFT, CURSOR_RIGHT. */
	public int getCursorMode() { return cursorMode; }
	
	/** set whether it is editable, shows cursor(, and show soft keyboard) or not.  */
	public void setIsEditable(boolean able) { editable = able; }
	
	/** return whether it is editable, shows cursor(, and show soft keyboard)  */
	public boolean isEditable() { return editable; }
	
	/** set whether it shows soft input on focus or not. */
	public void setIsShowSoftInputOnFocus(boolean shows) { showSoftInputOnFocus = shows; }
	
	/** set whether it shows soft input on focus and apply it. */
	public void setIsShowSoftInputOnFocusAndUpdate(boolean shows) {
		if(showSoftInputOnFocus != shows) {
			showSoftInputOnFocus = shows;
			if(isFocused()) {
				if(shows) {
					if(!inputMethodManager.isActive(this))
						inputMethodManager.showSoftInput(this, 0);
				} else {
					if(inputMethodManager.isActive(this))
						inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
				}
			}
		}
	}
	
	/** return whether it shows soft input on focus or not. */
	public boolean isShowSoftInputOnFocus() { return showSoftInputOnFocus; }
	
	/** {@hide} */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(touchingCursor != CURSOR_INVISIBLE) {
			Drawable cursor = getCursorDrawable(touchingCursor);
			int x = (int) event.getX() - cursorWidth / 2;
			int y = (int) event.getY() - cursorHeight;
			cursor.setBounds(x, y, cursorWidth, cursorHeight);
			return true;
		} else {
			boolean handled = super.onTouchEvent(event);
			switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					lastx = event.getX();
					lasty = event.getY();
					break;
					
				case MotionEvent.ACTION_MOVE:
					Scroller scroller = getScroller();
					break;
					
				
			}
			return handled;
		}
	}
	
	@Override
	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
		return new ScrollingEditTextInputConnection(this, true);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
	}
	
	/** {@hide}*/
	@Override
	public float onDrawCharacter(Canvas canvas, int index, int line, int col, float x, float y) {
		return super.onDrawCharacter(canvas, index, line, col, x, y);
	}
	
	private void solveCursorPosition(int dx, int dy) {
		switch(cursorMode) {
			case CURSOR_SINGLE:
				cursorDrawable.getBounds().offset(dx, dy);
				break;

			case CURSOR_SELECT:
				leftCursorDrawable.getBounds().offset(dx, dy);
				rightCursorDrawable.getBounds().offset(dx, dy);
				break;
		}
	}
	
	private void solveCursorPosition() {
		TextData.Cache cache = getText().getCache();
		cache.updateBothIfNeeded();
		TextData.PositionData data = getText().getPositionData(cache.cursorPosition);
		int width = cursorWidth;
		int height = cursorHeight;
		int x = (int) data.x - width / 2;
		int y = (int) data.y - width / 2 + (int) getTextSize();
		switch(cursorMode) {
			case CURSOR_SINGLE:
				cursorDrawable.setBounds(x, y, x + width, y + height);
				break;
				
			case CURSOR_SELECT:
				leftCursorDrawable.setBounds(x, y, x + width, y + height);
				rightCursorDrawable.setBounds(x, y, x + width, y + height);
				break;
		}
	}
}
