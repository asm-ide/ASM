package com.asm.text;

import com.asm.widget.codeedit.ScrollingTextView;
import com.asm.widget.codeedit.TextsDrawingInterface;
import com.asm.annotation.NonNull;
import com.asm.annotation.Nullable;

import android.view.View;
import android.view.KeyEvent;
import android.text.TextPaint;
import android.graphics.Canvas;
import android.os.Parcel;
import android.os.Parcelable;


public class TextDraw implements View.OnKeyListener, Parcelable
{
	/** parent view */
	@NonNull ScrollingTextView text;
	
	
	/** text paint that parent uses on draw it */
	@NonNull private TextPaint paint;
	
	/** insert mode. not used much and switch on/off on key {@code KeyEvent.KEYCODE_INSERT} */
	private boolean insert = false;
	
	/** last pressed key. I donno where it used.. Why I made this? OTL */
	private int lastKey;
	
	/**
	 * Margin between lines. Not applied twice
	 * <code>
	 * Line 1
	 *  ||| <- line margin
	 * Line 2
	 *  ...
	 * </code>
	 */
	private float lineMargin = 5f;
	
	
	private TextDraw() {
		paint = new TextPaint();
		paint.setAntiAlias(true);
		paint.setColor(0xff000000);
	}
	
	/**
	 * set the line margin. Not applied twice.
	 * <code>
	 * Line 1
	 *  ||| <- line margin
	 * Line 2
	 *  ...
	 * </code>
	 */
	public void setLineMargin(int lineMargin) {
		this.lineMargin = lineMargin;
	}
	
	/**
	 * set the text size.
	 */
	public void setTextSize(float size) {
		paint.setTextSize(size);
	}
	
	/**
	 * return the text size.
	 */
	public float getTextSize() {
		return paint.getTextSize();
	}
	
	/**
	 * return the line margin.
	 * @see setLineMargin()
	 */
	public float getLineMargin() {
		return lineMargin;
	}
	
	/**
	 * return the line spacing.
	 * equals with lineMargin + getTextSize().
	 */
	public float getLineSpacing() {
		return lineMargin + paint.getTextSize();
	}
	
	
	//public TextData getData() { return text.getText(); }
	//public void setData(TextData data) { if(data == null) throw new NullPointerException("argument data is null"); data.draw = this; data.getCache().updateIfNeeded(); }
	
	/**
	 * add this object on {@code View.setOnKeyListener}.
	 * 
	 * When (on {@code ScrollableEditText})q user type on
	 * view, text will be typed on cursor position.
	 * 
	 */
	@Override
	public boolean onKey(View view, int key, KeyEvent event)
	{
		boolean result = true;
		TextData data = text.getText();
		if(event.isSystem()) result = false;
		else if(event.isCtrlPressed()) {
			
		} else {
			int curPos = data.getCursorPosition();
			
			switch(key) {
				case KeyEvent.KEYCODE_DEL: data.delete(curPos - 1, curPos); break;
				case KeyEvent.KEYCODE_FORWARD_DEL: data.delete(curPos, curPos + 1); break;
				case KeyEvent.KEYCODE_FORWARD: data.addCursorPosition(1); break;
				case KeyEvent.KEYCODE_MOVE_HOME: data.setCursorPosition(0); break;
				case KeyEvent.KEYCODE_MOVE_END: data.setCursorPosition(data.length() - 1); break;
				case KeyEvent.KEYCODE_INSERT: insert = !insert;  break;
				
			}
			lastKey = key;
		}
		return result;
	}
	
	/**
	 * draw texts on canvas.
	 * This method only draw texts, not about edit.
	 */
	public void onDraw(Canvas canvas, TextsDrawingInterface draw)
	{
		// TODO
	}
	
	/**
	 * return the using paint when itself draws the texts.
	 */
	public @NonNull TextPaint getPaint() {
		return paint;
	}
	
	/**
	 * set the using paint when itself draws the texts.
	 * must not be null.
	 */
	public void setPaint(@NonNull TextPaint paint) {
		if(paint == null) throw new NullPointerException("argument data is null");
		this.paint = paint;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel p, int flags) {
		// TODO : write paint
		p.writeFloat(paint.getTextSize());
		p.writeInt(paint.getColor());
		p.writeByte((byte) (insert? 1 : 0));
		p.writeInt(lastKey);
		p.writeFloat(lineMargin);
	}
	
	/**
	 * create {@code TextDraw} from parcel.
	 */
	public static TextDraw fromParcel(Parcel p, ScrollingTextView parent) {
		TextDraw draw = new TextDraw();
		draw.text = parent;
		draw.paint.setTextSize(p.readFloat());
		draw.paint.setColor(p.readInt());
		draw.insert = (p.readByte() == 1);
		draw.lastKey = p.readInt();
		draw.lineMargin = p.readFloat();
		return draw;
	}
	
	public static TextDraw create(ScrollingTextView text) {
		TextDraw draw = new TextDraw();
		draw.text = text;
		return draw;
	}
}
