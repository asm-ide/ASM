package com.asm.block.elements;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;

import com.asm.block.BlockElement;
import com.asm.block.Theme;

import static com.asm.block.BlockView.ELEMENTS_MARGIN;
import static com.asm.block.BlockView.START_MARGIN;
import static com.asm.block.BlockView.TOPBOTTOM_MARGIN;

public class NumberElement extends BlockElement
{
	Theme theme;
	private String mText;
	private Paint tPaint;
    private Paint paint;
    private Paint borderPaint;


	public NumberElement(Theme theme){
		super(theme);
		this.isBlockAttachable = true;

		this.theme = theme;
		mText = "0121";

		tPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		tPaint.setColor(theme.secondForeground);
		tPaint.setTextSize(theme.textSize);

        paint = new Paint();
        paint.setColor(Color.parseColor("#ffffff"));
        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.FILL);

        borderPaint = new Paint();
        borderPaint.setColor(Color.parseColor("#000000"));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(4);
	}

	@Override
	public void onDraw(Canvas canvas, int x, int y1, int y2)
	{
		if(!isAttached){
			int yPos = (int) ((y1+y2)/2 - ((tPaint.descent() + tPaint.ascent()) / 2));
			int posY = (y1 + y2) / 2 - (int)(measuredY / 2);
			int posX = x;
			int height = (int)measuredY;//y2 - y1;
			int width = (int)measuredX;
			//Log.d("Number Element", "onDraw :  y1 - " + y1);
			Path path = new Path();
			path.moveTo(posX + START_MARGIN+ELEMENTS_MARGIN, posY);
			path.cubicTo(posX + START_MARGIN+ELEMENTS_MARGIN,posY,posX,posY + height/2,posX + START_MARGIN + ELEMENTS_MARGIN,posY + height);
			path.lineTo(posX + width-START_MARGIN - ELEMENTS_MARGIN, posY + height);
			path.cubicTo(posX + width - START_MARGIN - ELEMENTS_MARGIN,posY + height,posX + width,posY + height/2,posX + width - START_MARGIN - ELEMENTS_MARGIN, posY);
			path.lineTo(posX + START_MARGIN + ELEMENTS_MARGIN, posY);


			canvas.drawPath(path, paint);
			canvas.drawPath(path, borderPaint);

			canvas.drawText(mText, x + START_MARGIN + ELEMENTS_MARGIN , yPos, tPaint);
		}

	}

	@Override
	public void onMeasure(PointF out)
	{
		if(!isAttached){
			float width = tPaint.measureText(mText);
			float height = tPaint.getTextSize();
			out.set(width + START_MARGIN * 2 + ELEMENTS_MARGIN * 2,
					height + TOPBOTTOM_MARGIN * 2);
        }else{
			//Todo : catch attached
		}

	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d("BlockElement", "Clicked");

		if(event.getAction() == MotionEvent.ACTION_DOWN){
			//Toast.makeText(theme.getContext(), "touched", Toast.LENGTH_SHORT).show();
			showDialog();
		}
		return true;
	}

	public void showDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(superElements.superView.getContext());
		builder.setTitle("Input Value");

		// Set up the input
		final EditText input = new EditText(superElements.superView.getContext());
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mText = input.getText().toString();
				superElements.superView.invalidate();
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		builder.show();
	}

}
