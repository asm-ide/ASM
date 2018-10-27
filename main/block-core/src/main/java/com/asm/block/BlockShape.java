package com.asm.block;

import android.graphics.*;
import java.util.logging.*;

public class BlockShape
{
	public static int measureNumberBlockWidth(int innerWidth){
		return (int)(innerWidth + BlockView.START_MARGIN * 2 + BlockView.ELEMENTS_MARGIN * 2);
	}
	public static int measureNumberBlockHeight(int innerHeight){
		return (int)(innerHeight + BlockView.TOPBOTTOM_MARGIN * 2);
	}
	public static Path drawNumberBlock(int w, int h)
	{
		Path path = new Path();
		path.moveTo(BlockView.START_MARGIN+BlockView.ELEMENTS_MARGIN, 0);
		path.cubicTo(BlockView.START_MARGIN+BlockView.ELEMENTS_MARGIN,0,0,h/2,BlockView.START_MARGIN + BlockView.ELEMENTS_MARGIN,h);
		path.lineTo(w-BlockView.START_MARGIN-BlockView.ELEMENTS_MARGIN, h);
		path.cubicTo(w-BlockView.START_MARGIN-BlockView.ELEMENTS_MARGIN,h,w,h/2,w-BlockView.START_MARGIN-BlockView.ELEMENTS_MARGIN,0);
		path.lineTo(BlockView.START_MARGIN+BlockView.ELEMENTS_MARGIN, 0);

		return path;
	}
	
}
