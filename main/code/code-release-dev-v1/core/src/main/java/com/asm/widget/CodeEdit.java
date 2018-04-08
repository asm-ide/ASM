package com.asm.widget;

import com.asm.widget.codeedit.Language;
import com.asm.widget.codeedit.CodeStyle;
import com.asm.widget.codeedit.ScrollingEditText;
import com.asm.widget.codeedit.LanguageInfo;
import com.asm.yoon2.codeedit.CodeEditInterface;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.util.DisplayMetrics;
import android.text.*;


/**
 * CodeEdit for editing codes.
 * You can use {@link com.asm.widget.codeedit.FileManager} to manage files.
 */
public class CodeEdit extends ScrollingEditText implements CodeEditInterface
{
	private class HighlightWatcher implements TextWatcher
	{
		@Override
		public void beforeTextChanged(CharSequence text, int start, int count, int after) {
			language.beforeTextChanged(text, start, count, after);
		}
		
		@Override
		public void onTextChanged(CharSequence text, int start, int before, int count) {
			
		}
		
		@Override
		public void afterTextChanged(Editable e) {
			
		}
	}
	
	
	public static final int HIGHLIGHTMODE_DIRECT = 0;
	public static final int HIGHLIGHTMODE_ASYNC = 1;
	
	
	private static final int LINENUMBERWIDTH = 40;
	
	
	private Language language;
	private CodeStyle style;
	private TextPaint lineNumberPaint;
	private float textX;
	private int highlightMode;
	
	
	public CodeEdit(Context context) {
		this(context, null);
	}
	
	public CodeEdit(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public CodeEdit(Context context, AttributeSet attrs, int defStyleAttr) {
		this(context, attrs, defStyleAttr, 0);
	}
	
	public CodeEdit(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
		
		lineNumberPaint = new TextPaint();
		lineNumberPaint.setColor(0x33000000);

		//style = CodeStyle.getDefault(getContext()).clone();
		style = new CodeStyle();
		style.setColor("base.background", 0x00ffffff);
		style.setColor("base.foreground", 0xff000000);
		
		
	}
	
	
	public void setHighlightMode(int highlightMode) {
		this.highlightMode = highlightMode;
	}
	
	public int getHighlightMode() {
		return highlightMode;
	}
	
	public void setStyle(CodeStyle style) {
		this.style = style;
	}
	
	public CodeStyle getStyle() {
		return style;
	}
	
	public void setLineNumberPaint(TextPaint lineNumberPaint) {
		this.lineNumberPaint = lineNumberPaint;
	}
	
	public TextPaint getLineNumberPaint() {
		return lineNumberPaint;
	}
	
	public void setLanguage(Language lang) {
		language = lang;
		lang.initLanguage(this);
	}
	
	public Language getLanguage() {
		return language;
	}
	
	public String getLanguageName() {
		return language.getInfo().languageName();
	}
	
	@Override
	public LanguageInfo getLanguageInfo() {
		return language.getInfo();
	}
	
	@Override
	public float setTextSize(float size, int unit) {
		float realSize = super.setTextSize(size, unit);
		lineNumberPaint.setTextSize(realSize);
		return realSize;
	}
	
	public int getColor(String name) {
		return style.getColor(name);
	}
	
	public int getColor(String lang, String name) {
		return style.getColor(lang, name);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		textX = getSp(LINENUMBERWIDTH);
		setTextColor(style.getColor(CodeStyle.COLOR_FOREGROUND));
		canvas.translate(textX, 0);
		
		super.onDraw(canvas);
	}
	
	@Override
	public void onDrawLine(Canvas canvas, int line, int col, String text, float y) {
		canvas.drawText(Integer.toString(line), -textX, y, lineNumberPaint);
		super.onDrawLine(canvas, line, col, text, y);
	}
	
	@Override
	public float onDrawCharacter(Canvas canvas, int index, int line, int col, float x, float y) {
		//getTextPaint().setTextColor
		float width = super.onDrawCharacter(canvas, index, line, col, x, y);
		return width;
	}
	
	private float getSp(int sp) {
		return TypedValue.applyDimension(sp, TypedValue.COMPLEX_UNIT_SP, getResources().getDisplayMetrics());
	}
}
