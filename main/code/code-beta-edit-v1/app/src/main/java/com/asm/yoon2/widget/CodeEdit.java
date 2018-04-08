package com.asm.yoon2.widget;

import android.content.*;
import android.graphics.*;
import android.widget.*;
import android.text.*;
import android.util.*;
import com.asm.yoon2.widget.codeedit.*;
import com.asm.yoon2.widget.codeedit.languages.*;
import android.graphics.drawable.*;
import android.view.*;


/**
 * CodeEdit for editing codes.
 */
public class CodeEdit extends EditText
{
	private Language language;
	//private boolean dirtyHighlight = false;
	private TextPaint lineNumberPaint;
	private CodeStyle style;
	private TextWatcher watcher = new TextWatcher(){
		
		@Override
		public void beforeTextChanged(CharSequence text, int start, int count, int after)
		{
			if(language != null) {
				language.beforeTextChanged(text, start, count, after);
			}
		}
		
		@Override
		public void onTextChanged(CharSequence text, int start, int before, int count)
		{
			if(language != null) {
				language.aftetTextChanged(text, start, before, count);
			}
		}
		
		@Override
		public void afterTextChanged(Editable edit)
		{
			
		}
	};
	
	
	public CodeEdit(Context context)
	{
		this(context, null);
	}
	
	public CodeEdit(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}
	
	public CodeEdit(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init();
	}
	
	public CodeEdit(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	@Override
	public void setTextSize(int unit, float size)
	{
		super.setTextSize(unit, size);
		lineNumberPaint.setTextSize(getTextSize());
	}
	
	public void setStyle(CodeStyle style) { this.style = style; }
	public CodeStyle getStyle() { return style; }
	public int getColor(String name) { return style.getColor(name); }
	public int getColor(String lang, String name) { return style.getColor(lang, name); }
	
	public void setLanguage(Language language) { this.language = language; }
	public Language getLanguage() { return language; }
	
	private void highlight(Editable edit, int start, int end)
	{
		if(language == null) Log.e("CodeEdit", "language is null");
		else {
			language.highlight(start, end);
		}
	}
	
	public void setLineNumberPaint(TextPaint lineNumberPaint) { this.lineNumberPaint = lineNumberPaint; }
	
	private void init()
	{
		lineNumberPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		lineNumberPaint.setColor(0x55000000);
		
		addTextChangedListener(watcher);
		setTextSize(16);
		setGravity(Gravity.START);
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		float lineRealHeight = (getLineHeight() + getLineSpacingExtra()) * getLineSpacingMultiplier();
		int line = (int) (getScrollY() / lineRealHeight) + 1;
		int maxLine = line + (int) (getHeight() / lineRealHeight);
		if(getLineCount() + 1 < maxLine) maxLine = getLineCount() + 1;
		canvas.translate(spToPx(4), 0);
		for(int i = line; i < maxLine; i++) {
			canvas.drawText(Integer.toString(i), 1, i * lineRealHeight + 8, lineNumberPaint);
		}
		canvas.translate(spToPx(40), 0);
		super.onDraw(canvas);
	}
	
	private float spToPx(float sp)
	{
		DisplayMetrics metrics = new DisplayMetrics();
		getDisplay().getMetrics(metrics);
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, metrics);
	}
}
