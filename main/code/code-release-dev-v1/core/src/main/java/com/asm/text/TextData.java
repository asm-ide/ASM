package com.asm.text;

import com.asm.annotation.Nullable;
import com.asm.annotation.NonNull;

import com.lhw.util.TextUtils;

import android.graphics.Canvas;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.InputFilter;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;



/**
 * Storage text and highlight data for ScrollingEditText.
 * Supports feature that text storage.
 * Also features for editing.
 * I REALLY worked hard for this...
 * @author LHW
 * @version 1.0
 */
public class TextData implements Editable, Serializable
{
	/**
	 * @hide
	 * A cache class for less legging.
	 */
	public static class Cache implements Serializable
	{
		/** log tag */
		private static final String TAG = "TextData.Cache";
		
		/** parent {@code TextData}. */
		transient public TextData data;
		
		/** cursor positions. */
		public int cursorPosition;
		
		/** cursor positions. */
		transient public int cursorLine, cursorCol, cursorEnd;
		
		/** all lines count */
		transient public int lines = 0;
		
		/** shows whether line count is old or not. */
		transient private boolean isLineCountOlds = true, isWidthOlds = true, isHeightOlds = true;
		
		/** cache */
		transient private int scrollableWidth;
		
		/** cache */
		transient private int scrollableHeight;
		
		
		public Cache(@NonNull TextData data) {
			this.data = data;
		}
		
		/** called before insert text to update cache */
		public void onInsert(int where, CharSequence text, int start, int end) {
			if(isLineCountOlds) updateIfNeeded();
			int newLineCount = TextUtils.countOf(String.valueOf(text.subSequence(start, end)), "\n", 0);
			lines += newLineCount;
			
			if(cursorPosition >= where) {
				addCursorPosition(end - start);
			}
			isWidthOlds = true;
		}
		
		/** called before delete text to update cache */
		public void onDelete(int start, int end) {
			if(isLineCountOlds) updateIfNeeded();
			int subtractLineCount = TextUtils.countOf(data.substring(start, end), "\n", 0);
			lines -= subtractLineCount;
			
			if(cursorPosition > end) {
				addCursorPosition(-(end - start));
			} else if(cursorPosition > start) {
				setCursorPosition(start);
			}
			isWidthOlds = true;
		}
		
		/** move cursor position */
		public void addCursorPosition(int add) {
			int newPosition = Math.max(0, Math.min(add + cursorPosition, data.length()));
			int newLineCount = TextUtils.countOf(data.substring(newPosition, cursorPosition), "\n", 0);
			if(add > 0) cursorLine += newLineCount;
			else cursorLine -=  newLineCount;
			int addCol = data.lastIndexOf("\n", newPosition);
			if(addCol == -1) cursorCol = newPosition;
			else cursorCol += addCol;
			cursorPosition += add;
		}
		
		/** set cursof position */
		public void setCursorPosition(int pos) {
			addCursorPosition(pos - cursorPosition);
		}
		
		/** update width / height if needed. */
		public void updateIfNeeded() {
			if(isWidthOlds) updateScrollableSize();
			else if(isHeightOlds) lines();
		}
		
		/** update width AND height if needed. */
		public void updateBothIfNeeded() {
			if(isWidthOlds || isHeightOlds) updateScrollableSize();
		}
		
		/** update width and height. */
		public void updateScrollableSize() {
			if(data.mDraw == null) return;
			
			int lineCount = 0;
			float curW = 1;
			int maxW = 20;
			for(int i = 0; i < data.length(); i++) {
				if(data.charAt(i) == '\n') {
					if(maxW < curW) maxW = (int) curW;
					curW = 0;
					lineCount++;
				} else {
					curW += data.mDraw.getPaint().measureText(new char[]{data.charAt(i)}, 0, 1);
				}
			}
			this.scrollableWidth = maxW;
			this.scrollableHeight = (int) (lineCount * data.mDraw.getLineSpacing());
			this.lines = lineCount;
			this.isWidthOlds = false;
			this.isHeightOlds = false;
			this.isLineCountOlds = false;
		}
		
		/** update if needed and return scrollable width */
		public int getScrollableWidth() {
			if(isWidthOlds) updateScrollableSize();
			return scrollableWidth;
		}
		
		/** update if needed and return scrollable height. */
		public int getScrollableHeight() {
			if(isHeightOlds) lines();
			return scrollableHeight;
		}
		
		/** update if needed and return line count. */
		public int lines() {
			if(isLineCountOlds) {
				lines = data.countOf("\n");
			}
			isLineCountOlds = false;
			if(data.mDraw != null) {
				scrollableHeight = (int) (lines * data.mDraw.getLineSpacing());
				isWidthOlds = false;
			}
			return lines;
		}
		
		/**
		* return the first text forward position of line.
		* You can type startLine and startPos with start
		* position to calculate it.
		*/
		public int getLinePosition(int lines, int startPos, int startLine) {
			if(startPos < 0 || startPos >= data.length())
				throw new IllegalArgumentException("startPos overed index " + startPos);
			if(startLine > lines)
				throw new IllegalArgumentException("startLine overed lines " + lines + ", " + startPos + ", " + startLine);
			
			int linesCur = startLine;
			for(int i = startPos; i < data.length(); i++) {
				if(linesCur == lines) return i;
				if(data.charAt(i) == '\n') linesCur++;
			}
			return -1;
		}
		
		public Cache clone(TextData newParent) {
			Cache cache = new Cache(newParent);
			cache.cursorCol = cursorCol;
			cache.cursorEnd = cursorEnd;
			cache.cursorLine = cursorLine;
			cache.cursorPosition = cursorPosition;
			cache.isHeightOlds = isHeightOlds;
			cache.isWidthOlds = isWidthOlds;
			cache.isLineCountOlds = isHeightOlds;
			cache.lines = lines;
			cache.scrollableHeight = scrollableHeight;
			cache.scrollableWidth = scrollableWidth;
			return cache;
		}
	}
	
	public static class Factory extends Editable.Factory
	{
		@Override
		public TextData newEditable(CharSequence source) {
			return TextData.valueOf(source);
		}
	}
	
	/**
	 * position data when returns to {@code getPositionData}.
	 */
	public static class PositionData
	{
		public int position;
		public int line, col;
		public float x, y;
		
		
		public PositionData() {}
		
		public PositionData(int p, int l, int c, float x, float y) {
			position = p;
			line = l;
			col = c;
			this.x = x;
			this.y = y;
		}
	}
	
	
	/** tag for Log. */
	private static final String TAG = "TextData";
	
	/** max array size. */
	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
	
	/**
	 * Undo mode and marks that do not use undo.
	 * If this mode was setted, all undo history will be destoryed and
	 * set to null.
	 */
	public static final int UNDO_MODE_OFF = 0;
	
	/**
	 * Undo mode and marks that undo was disenabled for a while.
	 * 
	 * UNDO_MODE_OFF does mean no not use undo manager forever and
	 * destory all undo history but this does mean not mark on undo
	 * stack while this mode was set.
	 * 
	 * This can be used during edit text without history.
	 */
	public static final int UNDO_MODE_DISENABLED = 1;
	
	/**
	 * Default undo mode.
	 * 
	 * This mode automaticly sence that insert, replace,
	 * or delete texts and add to history.
	 */
	public static final int UNDO_MODE_AUTOMARK = 2;
	
	/** 
	 * Undo mode and marks that do not use undo.
	 * 
	 * This is the mode that put all actions while this mode was set
	 * in one action. So, if undo() or redo() called, all actions
	 * with this mode will be edited.
	 */
	public static final int UNDO_MODE_OPENED = 3;
	
	/** default capacity. */
	public static int DEFAULTCAPACITY = 16;
	
	/** current capacity. */
	//public int mCapacity = DEFAULTCAPACITY;
	
	/** the main string storage. */
	//StringBuilder str;
	char[] mText;
	
	/** the length of text. */
	int mCount = 0;
	
	/**
	 * Undo manager for undo/redo.
	 * May be null, because if undo mode is UNDO_MODE_OFD,
	 * all undo history will be destoryed.
	 */
	private @Nullable UndoManager mUndoManager;
	
	/** undo mode. can select in UNDO_MODE_... */
	private int mUndoMode = UNDO_MODE_AUTOMARK;
	
	/** last undo mode. used when return to last mode. */
	private transient int mLastUndoMode;
	
	/** the {@code TextWather} listeners */
	transient @NonNull ArrayList<TextWatcher> mListeners = new ArrayList<TextWatcher>();
	
	/** for more faster calculate. */
	private @NonNull Cache mCache;
	
	/** text encoding. */
	String mEncoding = "UTF-16"; //TODO
	
	/** parent text drawing method. */
	transient //because this is parent so not need to save
	TextDraw mDraw;
	
	
	protected TextData() {
		mText = new char[DEFAULTCAPACITY];
		init();
	}
	
	protected TextData(CharSequence initValue) {
		this();
		append(initValue);
	}
	
	protected TextData(char[] text) {
		mText = text;
		mCount = text.length;
		
		init();
	}
	
	private void init() {
		mCache = new Cache(this);
		mUndoManager = new UndoManager();
		initSecondary();
	}
	
	private void initSecondary() {
		mCache.updateScrollableSize();
		//mCache.lines();
	}
	
	/**
	 * set all datas from other. 
	 */
	public void set(TextData other) {
		mCache = other.mCache;
		mText = other.mText;
		mEncoding = other.mEncoding;
		initSecondary();
	}
	
	//draw calculate methods
	
	/**
	 * Return the scrollable width.
	 * User can scroll content x from 0 to
	 * return value of this.
	 */
	public int getScrollableWidth() {
		return mCache.getScrollableWidth();
	}
	
	/**
	 * Return the scrollable height.
	 * As same as <code>lines() * getDraw().getLineSpacing()</code>.
	 */
	public int getScrollableHeight() {
		return mCache.getScrollableHeight();
	}
	
	/**
	 * Return the cursor's line count.
	 */
	public int getCursorLines() {
		return mCache.cursorLine;
	}
	
	/**
	 * Return the cursor's column, count start from each line's head.
	 */
	public int getCursorCols() {
		return mCache.cursorCol;
	}
	
	/**
	 * Return the cursor's position start from first charactor.
	 */
	public int getCursorPosition() {
		return mCache.cursorPosition;
	}
	
	/**
	 * move the cursor position.
	 */
	public void setCursorPosition(int pos) {
		mCache.setCursorPosition(pos);
	}
	
	/**
	 * move the cursor position.
	 */
	public void addCursorPosition(int add) {
		mCache.addCursorPosition(add);
	}
	
	/**
	 * Set the drawing method {@code TextDraw}.
	 * if using {@code CodeEdit}, it will be called automatic.
	 */
	public void setDraw(TextDraw draw) {
		mDraw = draw;
		mCache.updateIfNeeded();
	}
	
	/**
	 * return the {@code TextDraw}.
	 */
	public TextDraw getDraw() {
		return mDraw;
	}
	
	/** {@hide} */
	public Cache getCache() {
		return mCache;
	}
	
	//action methods.
	
	/**
	 * Add the {@code TextWatcher} listener.
	 * @see TextWatcher
	 */
	public void addOnTextChangedListener(TextWatcher l) {
		mListeners.add(l);
	}

	/**
	 * Remove the {@code TextWatcher} listener.
	 * @see TextWatcher
	 */
	public boolean removeOnTextChangedListener(TextWatcher l) {
		return mListeners.remove(l);
	}
	
	/**
	 * Called before insert / delete texts.
	 */
	protected void beforeTextChanged(int start, int count, int after) {
		for(TextWatcher watcher : mListeners) {
			watcher.beforeTextChanged(this, start, count, after);
		}
		
		//add to library
		TextWatcher undoWatcher = mUndoManager.getAutoMarkWatcher();
		switch(mUndoMode) {
			case UNDO_MODE_OFF:
			case UNDO_MODE_DISENABLED:
				break;
				
			case UNDO_MODE_AUTOMARK:
			case UNDO_MODE_OPENED:
				undoWatcher.beforeTextChanged(this, start, count, after);
				break;
		}
	}
	
	/**
	 * Called when insert / delete texts.
	 */
	protected void onTextChanged(int start, int before, int count) {
		for(TextWatcher watcher : mListeners) {
			watcher.onTextChanged(this, start, before, count);
		}
		
		//add to history
		switch(mUndoMode) {
			case UNDO_MODE_OFF:
			case UNDO_MODE_DISENABLED:
				break;
				
			case UNDO_MODE_AUTOMARK:
				TextWatcher undoWatcher = mUndoManager.getAutoMarkWatcher();
				undoWatcher.onTextChanged(this, start, before, count);
				break;
				
			case UNDO_MODE_OPENED:
				mUndoManager.addOpenAction(this, start, before, count);
				break;
		}
	}
	
	/**
	 * Called after insert / delete texts.
	 */
	protected void afterTextChanged() {
		for(TextWatcher watcher : mListeners) {
			watcher.afterTextChanged(this);
		}
		
		//add to history
		switch(mUndoMode) {
			case UNDO_MODE_OFF:
			case UNDO_MODE_DISENABLED:
				break;
				
			case UNDO_MODE_AUTOMARK:
				TextWatcher undoWatcher = mUndoManager.getAutoMarkWatcher();
				undoWatcher.afterTextChanged(this);
				break;

			case UNDO_MODE_OPENED:
				break;
		}
	}
	
	
	//undo / redo methods.
	
	/**
	 * Set the undo / redo stack mode.
	 * If mode setted to UNDO_MODE_OFF, all history will be destoryed.
	 */
	public int undoMode(int mode) {
		if(mLastUndoMode == mode) return mLastUndoMode;
		mLastUndoMode = mUndoMode;
		switch(mode) {
			case UNDO_MODE_OFF:
			case UNDO_MODE_DISENABLED:
				switch(mLastUndoMode) {
					case UNDO_MODE_OPENED:
						closeUndoActions();
						break;
				}
				break;
				
			case UNDO_MODE_OPENED:
				if(mLastUndoMode != UNDO_MODE_OPENED) {
					openUndoActions();
				}
				break;
				
			case UNDO_MODE_AUTOMARK:
				break;
				
			default:
				throw new IllegalArgumentException("unknown mode: " + Integer.toHexString(mode));
		}
		mUndoMode = mode;
		if(mode == UNDO_MODE_OFF) {
			mUndoManager = null;
		}
		return mLastUndoMode;
	}
	
	public void openUndoActions() {
		mUndoMode = UNDO_MODE_OPENED;
		mUndoManager.close();
		mUndoManager.openActions();
	}
	
	public void closeUndoActions() {
		mUndoManager.close();
	}
	
	/**
	 * Used when tempory disable undo mode.
	 */
	public int disableUndo() {
		return undoMode(UNDO_MODE_DISENABLED);
	}
	
	/**
	 * Used when current mode is UNDO_MODE_DISABLED or called disableUndo();
	 * this re-enables undo mode.
	 * 
	 * For instance, when mode is a and called disableUndo() and later called
	 * this method, actions between calling disableUndo() and calling this will
	 * be not marked on undo history.
	 */
	public void turnOnUndo() {
		if(mUndoMode == UNDO_MODE_DISENABLED) {
			if(mLastUndoMode != UNDO_MODE_DISENABLED) {
				undoMode(mLastUndoMode);
			}
		} else throw new IllegalStateException("this only can used when last mode is UNDO_MODE_DISABLED");
	}
	
	
	//text methods.
	
	public void setEncoding(String encoding) {
		mEncoding = encoding;
	}
	
	public String getEncoding() {
		return mEncoding;
	}
	
	public void setText(TextData data) {
		mText = data.mText;
		mCount = data.mCount;
		if(data.mDraw != null) mDraw = data.mDraw;
		mCache.updateScrollableSize();
	}
	
	public void setText(CharSequence data) {
		mCount = data.length();
		mText = TextUtils.getChars(data, 0, mCount);
		mCache.updateScrollableSize();
	}
	
	/**
	 * Return the line count. 
	 */
	public int lines() {
		return mCache.lines();
	}
	
	/**
	 * Returns the position of the line and col 0.
	 * You should make sure that first line is line 0.
	 * @param lines line number that eant to get position
	 * @return the index start from first charactor, or return -1 when failed.
	 */
	public int getLinePosition(int lines) {
		return getLinePosition(lines, 0, 0);
	}
	
	/**
	 * Returns the position of the line and col 0.
	 * @param lines line number that eant to get position
	 * @param startPos start position that measure lines.
	 * @param startLine start line index that start to search. Start from 0.
	 * @return the index start from first charactor, or return -1 when failed.
	 */
	public int getLinePosition(int lines, int startPos, int startLine) {
		return mCache.getLinePosition(lines, startPos, startLine); 
	}
	
	/**
	 * Returns the lines and cols of position.
	 * @return <code>int[] { lines, cols, x, y }</code>.
	 */
	public PositionData getPositionData(int position) {
		return getPositionData(position, 0, 0);
	}
	
	/**
	 * Returns the lines and cols of position.
	 * @return <code>int[] { lines, cols, x, y }</code>.
	 */
	public PositionData getPositionData(int position, int startLine, int startPos) {
		if(position > mCount) throw new IllegalArgumentException("position is bigger than length");
		if(startPos > mCount) throw new IllegalArgumentException("startPos is bigger than length");
		
		int lines = startLine;
		int lastLinePos = 0;
		int curPos = startPos;
		float x = 0;
		while(curPos <= position) {
			if(mText[curPos] == '\n') {
				lines++;
				lastLinePos = curPos;
				x = 0;
			} else
				x += mDraw.getPaint().measureText(new char[] {mText[curPos]}, 0, 1);
			curPos++;
		}
		
		return new PositionData(position, lines, curPos - lastLinePos, x, lines * mDraw.getLineSpacing() - mDraw.getLineMargin());
	}
	
	
	//text data methods
	
	/**
	 * Ensure the capacity for this text.
	 */
	public void ensureCapacity(int minimumCapacity) {
		if(minimumCapacity - mText.length > 0) {
			mText = Arrays.copyOf(mText, newCapacity(minimumCapacity));
		}
	}
	
	private int newCapacity(int minCapacity) {
		int newCapacity = (mText.length << 1) + 2;
		if(newCapacity - minCapacity < 0) {
			newCapacity = minCapacity;
		}
		return (newCapacity <= 0 || MAX_ARRAY_SIZE - newCapacity < 0)
			? hugeCapacity(minCapacity) 
			: newCapacity;
	}
	
	private int hugeCapacity(int minCapacity) {
		if(Integer.MAX_VALUE - minCapacity < 0) {
			// overflow
			throw new OutOfMemoryError();
		}
		return (minCapacity > MAX_ARRAY_SIZE)
			? minCapacity 
			: MAX_ARRAY_SIZE; 
	}
	
	/**
	* insert the text at <code>where</code>.
	*/
	public TextData insert(int where, char text) {
		return insert(where, String.valueOf(new char[]{text}), 0, 1); 
	}
	
	/**
	* insert the text at <code>where</code>.
	*/
	@Override public TextData insert(int where, CharSequence text) {
		return insert(where, text, 0, text.length());
	}
	
	/**
	 * insert the text at <code>where</code>, from <code>start</code> to <code>end</code>. 
	 */
	public TextData insert(int where, CharSequence text, int start, int end) {
		if(where < 0)
			throw new StringIndexOutOfBoundsException(where);
		if(start < 0)
			throw new StringIndexOutOfBoundsException(start);
		if(start > end)
			throw new StringIndexOutOfBoundsException("start > end");
		
		//before text changed
		beforeTextChanged(where, 0, end - start);
		mCache.onInsert(where, text, start, end);
		
		onInsert(where, text, start, end);
		
		//on text changed
		onTextChanged(where, 0, end - start);
		
		//after text changed
		afterTextChanged();
		
		return this;
	}
	
	/**
	 * Overrideable function for insert().
	 * 
	 * When you override this method, call super.onInsert() will
	 * actually insert the texts. And, before this method called,
	 * beforeTextChanged() was called, and when returns, onTextChanged()
	 * and afterTextChanged() will called.
	 * You should call super because all the event on the insert
	 * will be called.
	 */
	protected void onInsert(int where, CharSequence text, int start, int end) {
		char[] t = new char[text.length()];
		text.toString().getChars(start, end, t, 0);
		
		int len = end - start;
		
		//insert text
		ensureCapacity(mCount + len);
		System.arraycopy(mText, where, mText, where + len, mCount - where);
		System.arraycopy(t, 0, mText, where, len);
	}
	
	/** 
	 * return the string from <code>start</code> to <code>end</code>. 
	 */
	public String substring(int start, int end) {
		return subSequence(start, end).toString();
	}
	
	/** 
	 * index of <code>text</code> in this text. 
	 */
	public int indexOf(String text) { 
		return indexOf(text, 0, mCount); 
	}
	
	/**
	 * index of <code>text</code> in this text, start from <code>fromIndex</code>. 
	 */
	public int indexOf(String text, int fromIndex) { 
		return indexOf(text, fromIndex, mCount); 
	}
	
	/** 
	 * index of <code>text</code> from <code>fromIndex</code> to <code>endIndex</code>. 
	 * same as <code>substring(startIndex, endIndex).indexOf(text)</code> but more faster.
	 */
	public int indexOf(String text, int fromindex, int endIndex) { 
		return TextUtils.indexOf(this, text, fromindex, endIndex);
	}
	
	/**
	 * last index of <code>text</code>. 
	 */
	public int lastIndexOf(String text) {
		return lastIndexOf(text, mCount, 0);
	}
	
	/** 
	* last index of <code>text</code>. 
	*/
	public int lastIndexOf(String text, int fromIndex) {
		return lastIndexOf(text, fromIndex, 0);
	}
	
	/**
	 * last index of text from to limit.
	 * same as substring(limit, fromIndex).lastIndexOf(text) but faster.
	 */
	public int lastIndexOf(String text, int fromIndex, int limit) {
		return TextUtils.lastIndexOf(this, text, fromIndex, limit);
	}
	
	/**
	 * Check this text is matches to regex.
	 * @see Pattern
	 */
	public boolean matches(String regex) {
		return Pattern.matches(regex, this);
	}
	
	/** 
	 * return the count in this text witch equals target.
	 */
	public int countOf(String target) {
		return this.countOf(target, 0); 
	}
	
	/** 
	 * return the count in this text witch equals target.
	 */
	public int countOf(String target, int fromindex) {
		return TextUtils.countOf(this, target, fromindex, length());
	}
	
	/** 
	 * return the count in this text witch equals target.
	 */
	public int countOf(String target, int fromindex, int endIndex) {
		return TextUtils.countOf(this, target, fromindex, endIndex); 
	}
	
	
	
	/**
	 * convert {@code TextData} object to {@code String}.
	 */
	@Override
	public String toString() {
		return String.valueOf(mText, 0, mCount);
	}
	
	/**
	 * length of all texts.
	 */
	@Override
	public int length() {
		return mCount;
	}
	
	/**
	 * return character at position.
	 */
	@Override
	public char charAt(int position) {
		return mText[position];
	}
	
	/**
	 * @see CharSequence.subString()
	 */
	@Override
	public CharSequence subSequence(int start, int end) {
		return TextUtils.subSequence(this, start, end);
	}
	
	/**
	 * same as <code>str.delete(st, en); str.insert(st, source, start, end);</code>
	 */
	@Override
	public TextData replace(int start, int end, CharSequence text) {
		replace(start, end, text, 0, text.length());
		return this;
	}
	
	/**
	 * same as <code>str.delete(st, en); str.insert(st, source, start, end);</code>
	 */
	@Override
	public TextData replace(int st, int en, CharSequence str, int start, int end) {
		if(st < 0)
			throw new StringIndexOutOfBoundsException(st);
		if(st > mCount)
			throw new StringIndexOutOfBoundsException("st > length");
		if(st > en)
			throw new StringIndexOutOfBoundsException("st > en");
		if(start < 0)
			throw new StringIndexOutOfBoundsException(start);
		if(start > end)
			throw new StringIndexOutOfBoundsException("start > end");
		
		if (en > mCount) en = mCount;
		int len = end - start;
		int lastLen = en - st;
		
		//before text changed
		beforeTextChanged(st, lastLen, len);
		
		//replace text
		mCache.onDelete(st, en);
		mCache.onInsert(st, str, start, end);
		onReplace(st, en, str, start, end);
		
		//on text changed
		
		onTextChanged(st, lastLen, len);
		
		//after text changed
		afterTextChanged();
		
		return this;
	}
	
	public void onReplace(int st, int en, CharSequence text, int start, int end) {
		int len = end - start;
		int lastLen = en - st;
		int newCount = mCount + len - lastLen;
		
		//replace the text
		ensureCapacity(newCount);
		System.arraycopy(mText, en, mText, st + len, mCount - en);
		text.toString().getChars(start, end, mText, st);
		mCount = newCount;
	}
	
	/**
	 * delete the text, from start to end 
	 */
	@Override
	public TextData delete(int start, int end) {
		if(start < 0)
			throw new StringIndexOutOfBoundsException(start);
		if(start > end)
			throw new StringIndexOutOfBoundsException();
		
		if(end > mCount) end = mCount;
		int len = end - start;
		
		if(len > 0) {
			//before text changed
			beforeTextChanged(start, len, 0);

			onDelete(start, end);
			
			//on text changed
			onTextChanged(start, len, 0);
			
			//after text changed
			afterTextChanged();
		}
		
		return this;
	}
	
	/**
	 * Overrideable function for delete().
	 * 
	 * When you override this method, call super.onDelete() will
	 * actually delete the texts. And, before this method called,
	 * beforeTextChanged() was called, and when returns, onTextChanged()
	 * and afterTextChanged() will called.
	 * You should call super because all the event on the delete
	 * will be called.
	 */
	protected void onDelete(int start, int end) {
		int len = end - start;
		
		//delete text
		mCache.onDelete(start, end);
		System.arraycopy(mText, start + len, mText, start, mCount - end);
		mCount -= len;
	}
	
	/**
	 * Append text end of the text.
	 */
	@Override
	public TextData append(CharSequence text) {
		return append(text, 0, text.length() -1);
	}
	
	/**
	 * Append text cutted from start to end, end of the text.
	 */
	@Override
	public TextData append(CharSequence text, int start, int end){
		if(text == null) return append("null");
		if(start < 0)
			throw new StringIndexOutOfBoundsException(start);
		if(start > mCount)
			throw new StringIndexOutOfBoundsException(start);
		if(start > end)
			throw new StringIndexOutOfBoundsException("start > end");
		
		int len = text.length();
		int lastCount = mCount;
		
		//before text changed
		beforeTextChanged(mCount, 0, len);
		
		//append text
		ensureCapacity(mCount + len);
		text.toString().getChars(0, len, mText, mCount);
		mCount += len;
		
		//on text changed
		onTextChanged(lastCount, 0, len);
		return this;
	}
	
	/**
	 * Append one charactor end of text.
	 */
	@Override
	public TextData append(char character) {
		return append(String.valueOf(new char[]{character}));
	}
	
	/**
	 * @see GetChars
	 */
	@Override
	public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
		if(srcBegin < 0)
			throw new StringIndexOutOfBoundsException(srcBegin);
		if((srcEnd < 0) || (srcEnd > mCount))
			throw new StringIndexOutOfBoundsException(srcEnd);
		if(srcBegin > srcEnd)
			throw new StringIndexOutOfBoundsException("srcBegin > srcEnd");
		
		System.arraycopy(mText, srcBegin, dst, dstBegin, srcEnd - srcBegin);
	}
	
	/**
	 * clear all texts.
	 */
	@Override
	public void clear() {
		mCount = 0;
		mText = new char[DEFAULTCAPACITY];
	}
	
	public TextData clone() {
		TextData data = new TextData();
		data.ensureCapacity(mText.length);
		System.arraycopy(mText, 0, data.mText, 0, mText.length);
		data.mCache = mCache.clone(data);
		data.mCount = mCount;
		return data;
	}
	
	
	
	/**
	 * unsupported: I'm lazy;;
	 * developer of this is saying that donno how to use it
	 * and supporting this will be complicated so not support.
	 * NOT SUPPORT..!
	 *     ...
	 * <code>
	 * ■	   ■	■■■■■	■	
	 * ■	   ■	   ■		■	
	 * ■	   ■	   ■		■	
	 * ■■■■■■	   ■		■	
	 * ■	   ■	   ■		■	
	 * ■	   ■	   ■			
	 * ■	   ■	■■■■■	■	
	 * </code> 
	 */
	
	/** not support yet */
	@Override public void setFilters(InputFilter[] p1) { throw new UnsupportedOperationException("너무 귀찮아요ㅠㅠ"); }
	
	/** not support yet */
	@Override public InputFilter[] getFilters() { throw new UnsupportedOperationException("너무 귀찮아요ㅠㅠ"); }
	
	
	//not support: CodeEdit automatically highlight
	
	/** not support: {@code CodeEdit} automatically highlight */
	@Deprecated public void clearSpans() { throw new UnsupportedOperationException(); }
	
	/** not support: {@code CodeEdit} automatically highlight */
	@Deprecated public void setSpan(Object p1, int p2, int p3, int p4) { throw new UnsupportedOperationException(); }
	
	/** not support: {@code CodeEdit} automatically highlight */
	@Deprecated public void removeSpan(Object p1) { throw new UnsupportedOperationException(); }
	
	/** not support: {@code CodeEdit} automatically highlight */
	@Deprecated public <T extends Object> T[] getSpans(int p1, int p2, Class<T> p3) { throw new UnsupportedOperationException(); }
	
	/** not support: {@code CodeEdit} automatically highlight */
	@Deprecated public int getSpanStart(Object p1) { throw new UnsupportedOperationException(); }
	
	/** not support: {@code CodeEdit} automatically highlight */
	@Deprecated public int getSpanEnd(Object p1) { throw new UnsupportedOperationException(); }
	
	/** not support: {@code CodeEdit} automatically highlight */
	@Deprecated public int getSpanFlags(Object p1) { throw new UnsupportedOperationException(); }
	
	/** not support: {@code CodeEdit} automatically highlight */
	@Deprecated public int nextSpanTransition(int p1, int p2, Class p3) { throw new UnsupportedOperationException(); }
	
	
	//static methods
	
	/**
	 * create empty TextData.
	 */
	public static TextData create() {
		return new TextData();
	}
	
	/**
	 * create TextData from texts.
	 */
	public static TextData valueOf(char[] text) {
		return new TextData(String.valueOf(text));
	}
	
	/** 
	 * create TextData from texts.
	 */
	public static TextData valueOf(CharSequence text) {
		return new TextData(text);
	}
	
	/**
	 * create TextData from {@code StringBuilder}.
	 */
	public static TextData from(StringBuilder builder) {
		return new TextData(builder);
	}
	
	/**
	 * Initialize {@code TextData} from a seriazed-object.
	 * When you got the {@code TextData} object from
	 * {@code ObjectInputStream} or other, you should
	 * initialize that object by this object.
	 * @params TextData what you want to convert
	 */
	public static TextData wrapSerializable(Object data, TextDraw parent) {
		if(!(data instanceof TextData)) throw new IllegalArgumentException("argument is not a TextData");
		
		TextData data2 = (TextData) data;
		if(data2 == null) throw new IllegalArgumentException("argument is null");
		data2.mDraw = parent;
		data2.wrapSerializable();
		return data2;
	}
	
	protected void wrapSerializable() {
		mCache.updateScrollableSize();
	}
	
	/**
	 * create the TextData from parcel.
	 */
	public static TextData fromParcel(Parcel p, TextDraw parent) {
		return fromParcel(new TextData(), p, parent);
	}
	protected static TextData fromParcel(TextData d, Parcel p, TextDraw parent) {
		p.readCharArray(d.mText);
		d.mUndoManager = (UndoManager) p.readSerializable();
		d.mEncoding = p.readString();
		d.mDraw = parent;
		d.mCache = (Cache) p.readSerializable();
		d.mCache.updateScrollableSize();
		return d;
	}
	
	/**
	 * get the parcelable object.
	 */
	public Parcelable parcel() {
		return new TextDataParcel();
	}
	
	/**
	* parcelable object for parcel.
	*/
	protected class TextDataParcel implements Parcelable
	{
		@Override
		public int describeContents() {
			return 0;
		}
		
		/** 
		 * save all data to parcel.
		 */
		@Override
		public void writeToParcel(Parcel p, int flags) {
			p.writeCharArray(mText);
			p.writeSerializable(mUndoManager);
			p.writeString(mEncoding);
			p.writeSerializable(mCache);
		}
	}
}
