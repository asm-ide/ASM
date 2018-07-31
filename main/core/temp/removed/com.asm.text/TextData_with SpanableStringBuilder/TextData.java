package com.asm.text;

import com.asm.annotation.Nullable;
import com.asm.annotation.NonNull;

import com.lhw.util.TextUtils;

import android.graphics.Canvas;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.Matcher;



/**
 * Storage text and highlight data for ScrollingEditText.
 * Supports editing, drawing utilities, undo/redo, and cache.
 * I REALLY worked hard for this...
 * @author LHW
 * @version 1.0
 */
public class TextData extends SpannableStringBuilder implements Editable, Parcelable, Serializable
{
	/**
	 * @hide
	 * A cache class for less legging.
	 */
	public static class Cache implements Parcelable, Serializable
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
		
		public Cache(Parcel p) {
			cursorPosition = p.readInt();
		}
		
		@Override
		public int describeContents() {
			return 0;
		}

		
		public void writeToParcel(Parcel p, int flags) {
			p.writeInt(cursorPosition);
		}
		
		/**
		 * called before insert text to update cache
		 */
		public void onInsert(int where, CharSequence text, int start, int end) {
			if(isLineCountOlds) updateIfNeeded();
			int newLineCount = TextUtils.countOf(String.valueOf(text.subSequence(start, end)), "\n", 0);
			lines += newLineCount;
			
			if(cursorPosition >= where) {
				addCursorPosition(end - start);
			}
			isWidthOlds = true;
		}
		
		/**
		 * called before delete text to update cache
		 */
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
		
		/**
		 * move cursor position
		 */
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
		
		/**
		 * set cursof position
		 */
		public void setCursorPosition(int pos) {
			addCursorPosition(pos - cursorPosition);
		}
		
		/**
		 * update width / height if needed.
		 */
		public void updateIfNeeded() {
			if(isWidthOlds) updateScrollableSize();
			else if(isHeightOlds) lines();
		}
		
		/**
		 * update width AND height if needed.
		 */
		public void updateBothIfNeeded() {
			if(isWidthOlds || isHeightOlds) updateScrollableSize();
		}
		
		/**
		 * update width and height.
		 */
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
		
		/**
		 * update if needed and return scrollable width
		 */
		public int getScrollableWidth() {
			if(isWidthOlds) updateScrollableSize();
			return scrollableWidth;
		}
		
		/**
		 * update if needed and return scrollable height.
		 */
		public int getScrollableHeight() {
			if(isHeightOlds) lines();
			return scrollableHeight;
		}
		
		/**
		 * update if needed and return line count.
		 */
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
		
		public static final Creator<Cache> CREATOR = new Creator<Cache>() {
			@Override
			public Cache createFromParcel(Parcel p) {
				return new Cache(p);
			}
			
			@Override
			public Cache[] newArray(int len) {
				return new Cache[len];
			}
		};
	}
	
	public static class Factory extends Editable.Factory
	{
		@Override
		public TextData newEditable(CharSequence source) {
			return TextData.valueOf(source);
		}
	}
	
	/**
	 * Position data when returns to {@code getPositionData}.
	 * It marks position, lines, cols, x position, and y position.
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
	
	
	/** tag for Log */
	private static final String TAG = "TextData";
	
	/** max array size */
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
	public static final int UNDO_MODE_DISABLED = 1;
	
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
	
	/**
	 * Undo manager for undo/redo.
	 * May be null, because if undo mode is UNDO_MODE_OFD,
	 * all undo history will be destoryed.
	 */
	private @Nullable UndoManager mUndoManager;
	
	/** undo mode. can select in UNDO_MODE_... */
	private int mUndoMode = UNDO_MODE_AUTOMARK;
	
	/** last undo mode. used when return to last mode. */
	private int mLastUndoMode;
	
	/** for more faster calculate. */
	private @NonNull Cache mCache;
	
	/** text encoding. */
	String mEncoding = "UTF-16"; //TODO
	
	/** parent text drawing method. */
	transient //because this is parent so not need to save
	TextDraw mDraw;
	
	/**
	 * For wrap object from stream and decide it.
	 * 0 : none
	 * 1 : from parcel
	 * 2 : from serializable
	 */
	private transient int mStreamFrom = 0;
	
	
	protected TextData() {
		this("");
	}
	
	protected TextData(CharSequence initValue) {
		super(initValue);
		init();
	}
	
	protected TextData(char[] text) {
		this(String.valueOf(text));
	}
	
	protected TextData(Parcel p) {
		super(String.valueOf(p.createCharArray()));
		mUndoManager = (UndoManager) p.readSerializable();
		mUndoMode = p.readInt();
		mLastUndoMode = p.readInt();
		mEncoding = p.readString();
		mCache = (Cache) p.readSerializable();
		mCache.updateScrollableSize();
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
	 * @param other the other data which will set
	 */
	public void set(TextData other) {
		mCache = other.mCache;
		setText(other);
		mEncoding = other.mEncoding;
		initSecondary();
	}
	
	// Draw calculate methods
	
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
	 * @param add add cursor position. 0 to not change
	 */
	public void addCursorPosition(int add) {
		if(add != 0) {
			mCache.addCursorPosition(add);
			invaildate();
		}
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
	
	/**
	 * Invaildate the view if TextData is used in any view.
	 */
	public void invaildate() {
		
	}
	
	/**
	 * Invaildate the view if TextData is used in any view.
	 */
	
	/** {@hide} */
	public Cache getCache() {
		return mCache;
	}
	
	// Action methods.
	
	/**
	 * Called before insert / delete texts.
	 */
	protected void beforeTextChanged(int start, int count, int after) {
		// add to library
		TextWatcher undoWatcher = mUndoManager.getAutoMarkWatcher();
		switch(mUndoMode) {
			case UNDO_MODE_OFF:
			case UNDO_MODE_DISABLED:
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
		// add to history
		switch(mUndoMode) {
			case UNDO_MODE_OFF:
			case UNDO_MODE_DISABLED:
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
		// add to history
		switch(mUndoMode) {
			case UNDO_MODE_OFF:
			case UNDO_MODE_DISABLED:
				break;
				
			case UNDO_MODE_AUTOMARK:
				TextWatcher undoWatcher = mUndoManager.getAutoMarkWatcher();
				undoWatcher.afterTextChanged(this);
				break;

			case UNDO_MODE_OPENED:
				break;
		}
	}
	
	
	// Undo / redo methods.
	
	/**
	 * Set the undo / redo stack mode.
	 * If mode setted to UNDO_MODE_OFF, all history will be destoryed.
	 */
	public int undoMode(int mode) {
		if(mLastUndoMode == mode) return mLastUndoMode;
		mLastUndoMode = mUndoMode;
		switch(mode) {
			case UNDO_MODE_OFF:
			case UNDO_MODE_DISABLED:
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
	
	/**
	 * Open the undo actions.
	 * Used when undo mode is {@link UNDO_MODE_OPENED} and if there is any
	 * opened {@link Actions}, close it and open new {@link Actions}.
	 */
	public void openUndoActions() {
		if(mLastUndoMode == UNDO_MODE_OPENED)
			return;
		mLastUndoMode = mUndoMode;
		mUndoMode = UNDO_MODE_OPENED;
		
		mUndoManager.close();
		mUndoManager.openActions();
	}
	
	/**
	 * Open the undo actions if current undo mode marks actions.
	 * @see openUndoActions()
	 */
	public void openUndoActionsTemporary() {
		switch(mUndoMode) {
			case UNDO_MODE_AUTOMARK:
			case UNDO_MODE_OPENED:
				openUndoActions();
				break;
		}
	}
	
	/**
	 * Close the actions if when called openUndoActionsTemporary()
	 * opened the actions and restore to last mode.
	 */
	public void closeUndoActionsTemporary() {
		switch(mLastUndoMode) {
			case UNDO_MODE_AUTOMARK:
			case UNDO_MODE_OPENED:
				closeUndoActions();
				mUndoMode = mLastUndoMode;
				break;
		}
	}
	
	/**
	 * Close the undo actions.
	 * Used when undo mode is {@link UNDO_MODE_OPENED} and closes the same
	 * all {@link Actions} and doesn't open new {@link Actions}.
	 */
	public void closeUndoActions() {
		mUndoManager.close();
	}
	
	/**
	 * Used when tempory disable undo mode.
	 */
	public int disableUndo() {
		return undoMode(UNDO_MODE_DISABLED);
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
		if(mUndoMode == UNDO_MODE_DISABLED) {
			if(mLastUndoMode != UNDO_MODE_DISABLED) {
				undoMode(mLastUndoMode);
			}
		} else throw new IllegalStateException("this only can used when last mode is UNDO_MODE_DISABLED");
	}
	
	
	// Text methods.
	
	/**
	 * Set the encoding.
	 * This does not convert the text encoding; just set the code encoding.
	 * If you want to convert the text encoding, use {@link convertEncoding()}.
	 */
	public void setEncoding(String encoding) {
		mEncoding = encoding;
	}
	
	public String getEncoding() {
		return mEncoding;
	}
	
	public void setText(TextData data) {
		setText((CharSequence) data);
		if(data.mDraw != null) mDraw = data.mDraw;
		mCache.updateScrollableSize();
	}
	
	public void setText(CharSequence data) {
		replace(0, length(), data);
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
		if(position > length()) throw new IllegalArgumentException("position is bigger than length");
		if(startPos > length()) throw new IllegalArgumentException("startPos is bigger than length");
		
		int lines = startLine;
		int lastLinePos = 0;
		int curPos = startPos;
		float x = 0;
		while(curPos <= position) {
			if(charAt(curPos) == '\n') {
				lines++;
				lastLinePos = curPos;
				x = 0;
			} else
				x += mDraw.getPaint().measureText(new char[] {charAt(curPos)}, 0, 1);
			curPos++;
		}
		
		return new PositionData(position, lines, curPos - lastLinePos, x, lines * mDraw.getLineSpacing() - mDraw.getLineMargin());
	}
	
	
	//text data methods
	
	
	/**
	 * Overrideable function for insert().
	 * 
	 * When you override this method, calling super.onInsert() will
	 * actually deletes the texts and calls onTextChanged(). And,
	 * before this method called, beforeTextChanged() was called,
	 * and after returns, onTextChanged() and afterTextChanged()
	 * will called.
	 * <p>
	 * You should call super if you not want to not changing texts
	 * because all the event on the insert will be called.
	 * @returns whether actually text was changed
	 */
	protected boolean onInsert(int where, CharSequence text, int start, int end) {
		super.insert(where, text, start, end);
		
		//on text changed
		onTextChanged(where, 0, end - start);
		
		return true;
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
	public int indexOf(CharSequence text) { 
		return indexOf(text, 0, length()); 
	}
	
	/**
	 * index of <code>text</code> in this text, start from <code>fromIndex</code>. 
	 */
	public int indexOf(CharSequence text, int fromIndex) { 
		return indexOf(text, fromIndex, length()); 
	}
	
	/** 
	 * index of <code>text</code> from <code>fromIndex</code> to <code>endIndex</code>. 
	 * same as <code>substring(startIndex, endIndex).indexOf(text)</code> but more faster.
	 */
	public int indexOf(CharSequence text, int fromindex, int endIndex) { 
		return TextUtils.indexOf(this, text, fromindex, endIndex);
	}
	
	/**
	 * last index of <code>text</code>. 
	 */
	public int lastIndexOf(CharSequence text) {
		return lastIndexOf(text, length(), 0);
	}
	
	/** 
	* last index of <code>text</code>. 
	*/
	public int lastIndexOf(CharSequence text, int fromIndex) {
		return lastIndexOf(text, fromIndex, 0);
	}
	
	/**
	 * last index of text from to limit.
	 * same as substring(limit, fromIndex).lastIndexOf(text) but faster.
	 */
	public int lastIndexOf(CharSequence text, int fromIndex, int limit) {
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
	public int countOf(CharSequence target) {
		return this.countOf(target, 0); 
	}
	
	/** 
	 * return the count in this text witch equals target.
	 */
	public int countOf(CharSequence target, int fromindex) {
		return TextUtils.countOf(this, target, fromindex, length());
	}
	
	/** 
	 * return the count in this text witch equals target.
	 */
	public int countOf(CharSequence target, int fromindex, int endIndex) {
		return TextUtils.countOf(this, target, fromindex, endIndex); 
	}
	
	/** 
	 * Lightly return the charsequence.
	 * Different to subSequence(), this task is very fast and not use
	 * much memory. But if TextData's text was changed, lightly subsequenced
	 * text will be also changed.
	 */
	public CharSequence lightSubSequence(int start, int end) {
		return TextUtils.lightSubSequence(this, start, end);
	}
	
	/** 
	 * Spilt the text by given text.
	 */
	public String[] split(CharSequence by) {
		return split(by, 0, length());
	}
	
	/** 
	 * Split the text by given text, start from {@code start}.
	 * @param start start index.
	 */
	public String[] split(CharSequence by, int start) {
		return split(by, start, length());
	}
	
	/**
	 * Split the text by given text, start from {@code start}.
	 * @param start start index.
	 * @param end end index.
	 */
	public String[] split(CharSequence by, int start, int end) {
		ArrayList<String> list = new ArrayList<String>();
		int lastIndex = start;
		int i = indexOf(by, start + 1, end);
		
		do {
			list.add(substring(lastIndex, i));
		} while((i = indexOf(by, i + 1, end)) != 1);
		
		return list.toArray(new String[0]);
	}
	
	/**
	 * Find things which matches to param {@code from} in plain text and replace.
	 * @returns if replace anything, return int array, {last start index, last end index, replaced start index, replaced end index}, or return null
	 */
	public int[] replace(String from, CharSequence to) {
		return replaceRegex(Pattern.quote(from), to);
	}
	
	/**
	 * Find things which matches to param {@code regex} in regular expressions and replace.
	 * @returns if replace anything, return int array, {last start index, last end index, replaced start index, replaced end index}, or return null
	 */
	public int[] replaceRegex(String regex, CharSequence to) {
		return replaceRegex(Pattern.compile(regex), to, 0, length());
	}
	
	/**
	 * Find things which matches to param {@code regex} in regular expressions and replace.
	 * @returns if replace anything, return int array, {last start index, last end index, replaced start index, replaced end index}, or return null
	 */
	public int[] replaceRegex(Pattern regex, CharSequence to, int start, int end) {
		Matcher matcher = regex.matcher(this).region(start, end);
		matcher.find();
		if(matcher.hitEnd()) return null;
		int starts = matcher.start(), ends = matcher.end();
		replace(starts, ends, to, 0, to.length());
		return new int[] {starts, ends};
	}
	
	/**
	 * Replace all texts equals with param {@code from} to param {@code to}.
	 * @returns all replaced count. if replaced nothing, returns 0.
	 */
	public int replaceAll(String from, CharSequence to) {
		return replaceAllRegex(Pattern.quote(from), to);
	}
	
	/**
	 * Replace all texts matches with param {@code regex} to param {@code to}.
	 * @returns all replaced count. if replaced nothing, returns 0.
	 */
	public int replaceAllRegex(String regex, CharSequence to) {
		return replaceAllRegex(Pattern.compile(regex), to, 0, length());
	}
	
	/**
	 * Replace all texts matches with param {@code regex} to param {@code to}, bounds
	 * in {@code start} to {@code end}.
	 * @returns all replaced count. if replaced nothing, returns 0.
	 */
	public int replaceAllRegex(Pattern regex, CharSequence to, int start, int end) {
		if(start > end)
			throw new StringIndexOutOfBoundsException("start > end");
		else if(end > length())
			throw new StringIndexOutOfBoundsException("end > length");
		else if(start < 0 || end < 0)
			throw new StringIndexOutOfBoundsException("minus " + start + "~" + end);
		
		openUndoActionsTemporary();
		int[] lastIndex = {0, 0};
		int replacedCount = 0;
		
		while(lastIndex != null) {
			lastIndex = replaceRegex(regex, to, lastIndex[1] + 1, end);
			lastIndex[1] += lastIndex[3] - lastIndex[2] - (lastIndex[1] - lastIndex[0]);
			replacedCount++;
		}
		closeUndoActionsTemporary();
		return replacedCount;
	}
	
	/**
	 * Overrideable function for replace().
	 * 
	 * When you override this method, calling super.onReplace() will
	 * actually replaces the texts and calls onTextChanged(). And,
	 * before this method called, beforeTextChanged() was called,
	 * and after returns, onTextChanged() and afterTextChanged()
	 * will called.
	 * <p>
	 * You should call super if you not want to not changing texts
	 * because all the event on the replace will be called.
	 */
	protected boolean onReplace(int st, int en, CharSequence text, int start, int end) {
		super.replace(st, en, text.subSequence(start, end));
		
		//on text changed
		onTextChanged(st, en - st, end - start);
		
		return true;
	}
	
	/**
	 * Overrideable function for delete().
	 * 
	 * When you override this method, calling super.onDelete() will
	 * actually deletes the texts and calls onTextChanged(). And,
	 * before this method called, beforeTextChanged() was called,
	 * and after returns, onTextChanged() and afterTextChanged()
	 * will called.
	 * <p>
	 * You should call super if you not want to not changing texts
	 * because all the event on the delete will be called.
	 */
	protected boolean onDelete(int start, int end) {
		super.delete(start, end);
		//on text changed
		onTextChanged(start, end - start, 0);
		
		return true;
	}
	
	 // TODO : temp
//	public TextData clone() {
//		TextData data = new TextData();
//		data.ensureCapacity(mText.length);
//		System.arraycopy(mText, 0, data.mText, 0, mText.length);
//		data.mCache = mCache.clone(data);
//		data.mCount = mCount;
//		return data;
//	}
	
	 
	
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
	
	
	// Utilities for marshelling TextData and support aidl
	
	/** {@hide} */
	public static final Creator<TextData> CREATOR
			= new Creator<TextData>() {
		@Override
		public TextData createFromParcel(Parcel p) {
			return new TextData(p);
		}

		@Override
		public TextData[] newArray(int len) {
			return new TextData[len];
		}
	};
	
	
	/**
	 * Initialize {@code TextData} from a seriazed-object.
	 * When you got the {@code TextData} object from
	 * {@code ObjectInputStream} or other, you should
	 * initialize that object by this object.
	 * @params TextData what you want to convert
	 */
	public static TextData wrapFromDataStream(Object data, TextDraw parent) {
		if(!(data instanceof TextData)) throw new IllegalArgumentException("argument is not a TextData");
		
		TextData data2 = (TextData) data;
		if(data2 == null) throw new IllegalArgumentException("argument is null");
		data2.mDraw = parent;
		data2.wrapFromDataStream();
		return data2;
	}
	
	protected void wrapFromDataStream() {
		mCache.data = this;
		mCache.updateScrollableSize();
	}
	
	/**
	 * create the TextData from parcel.
	 */
	public static TextData fromParcel(Parcel p, TextDraw parent) {
		TextData data = new TextData(p);
		data.mDraw = parent;
		return data;
	}
	
	/** @hide */
	@Override
	public int describeContents() {
		return 0;
	}
	
	/** 
	 * save all data to parcel.
	 */
	@Override
	public void writeToParcel(Parcel p, int flags) {
		char[] text = new char[length()];
		getChars(0, length(), text, 0);
		p.writeCharArray(text);
		p.writeSerializable(mUndoManager);
		p.writeInt(mUndoMode);
		p.writeInt(mLastUndoMode);
		p.writeString(mEncoding);
		p.writeSerializable(mCache);
	}
}
