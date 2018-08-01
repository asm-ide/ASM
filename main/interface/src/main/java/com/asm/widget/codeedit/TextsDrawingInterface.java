package com.asm.widget.codeedit;

import android.graphics.*;


/** {@hide} */
public interface TextsDrawingInterface
{
	public void onDrawLine(Canvas canvas, int index, int line, String text, float y);
	
	public float onDrawCharacter(Canvas canvas, int index, int line, int col, float x, float y);
}
