package com.asm.coreui.activity;

import android.content.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.widget.*;
import com.asm.language.*;
import com.asm.widget.codeedit.*;
import java.io.*;
import java.util.*;

import android.support.v7.widget.Toolbar;


public class CodeEditActivity extends AppCompatActivity
{
	private Toolbar toolbar;
	private ScrollingTextView edit;
	private EditText dbg;
	private FloatingActionButton fab;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
				@Override
				public void uncaughtException(Thread p1, Throwable th)
				{
					Intent i = new Intent(CodeEditActivity.this, DebugActivity.class);
					Writer stringWriter = new StringWriter();
					PrintWriter printWriter = new PrintWriter(stringWriter);
					while (th != null) {
						th.printStackTrace(printWriter);
						th = th.getCause();
					}
					String obj = stringWriter.toString();
					i.putExtra("error", obj);
					startActivity(i);
					//try {
					//	Thread.sleep(100L);
					//} catch(InterruptedException e) {}
					finish();
					android.os.Process.killProcess(android.os.Process.myPid());
				}
			});
		
		super.onCreate(savedInstanceState);
		Log.d(".", "before set view");
		final EditText e = new EditText(this);
		e.setTextColor(0xffcccccc);
		e.addTextChangedListener(new TextWatcher() {
				LanguageInfo info = new JavaLanguage.JavaInfo();
			
			
				@Override
				public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					// TODO: Implement this method
				}

				@Override
				public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					info.comments();
					
					e.getText().clearSpans();
					Random r = new Random();
					CodeIterator i = new CodeIterator();
					
					i.setCode(e.getText());
					i.setInfo(info);
					Log.d("ce", "start " + e.getText());
					
					while(i.hasNext()) {
						CodeIterator.CodePart p = i.next();
						if(p.text == null) break;
						Log.d("ce", "codeIterator step " + p.type + " index = " + p.index + ", text = " + p.text);
						e.getText().setSpan(new ForegroundColorSpan(r.nextInt() | 0xff000000), p.index, p.index + p.text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
					Log.d("ce", "end");
				}

				@Override
				public void afterTextChanged(Editable p1)
				{
					// TODO: Implement this method
				}
			});
		setContentView(e);
//		setContentView(R.layout.codeedit);
		Log.d(".", "after set view");
		
//		toolbar = (Toolbar) findViewById(R.id.toolbar);
//		edit = (ScrollingTextView) findViewById(R.id.codeedit);
//		dbg = (EditText) findViewById(R.id.edit);
//		fab = (FloatingActionButton) findViewById(R.id.fab);
//		Toast.makeText(this, "index" + TextUtils.indexOf("abcdefg", "e", 0, 7), Toast.LENGTH_SHORT).show();
//		dbg = (TextView) findViewById(R.id.dbg);
//		edit.setDbg(dbg);
//		setSupportActionBar(toolbar);
		
//		dbg.addTextChangedListener(new TextWatcher() {
//
//				@Override
//				public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
//				{
//					// TODO: Implement this method
//				}
//
//				@Override
//				public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
//				{
//					edit.setText(p1);
//				}
//
//				@Override
//				public void afterTextChanged(Editable p1)
//				{
//					// TODO: Implement this method
//				}
//			});
		
//		Log.d(".", "log" + edit.getText().getPositionData(0).line);
		//toolbar.setVisibility(View.INVISIBLE);
		//getSupportActionBar().hide();
//		TextData data = TextData.valueOf("abcde\ndh un");
//		TextDraw draw = new TextDraw(data);
//		draw.setTextSize(16);
//		Log.d("lp", "" + data.getLinePosition(1));
//		TextData.PositionData pd = data.getPositionData(7);
//		Log.d("pd", "col" +  pd.col + "line" + pd.line);
		//edit.setText("hihiirirororoororoororoorororoorooro4o4o4kk4kk4o4o4oni");
//		final Handler h = new Handler();
//		final Random r = new Random();
//		final Runnable run = new Runnable() {
//			@Override
//			public void run() {
//				int col = r.nextInt() | 0xff000000;
//				toolbar.setBackgroundColor(col);
//				getWindow().setStatusBarColor(Colors.darken(col, 0.9f));
//				fab.setBackgroundTintList(ColorStateList.valueOf(r.nextInt() | 0xff000000));
//				StringBuilder sb = new StringBuilder();
//				for(int i = 0; i < 50; i++) {
//					sb.append(TextUtils.randText(r, 20)).append("\n");
//				}
//				edit.setText(sb.toString());
//				if(!isDestroyed()) h.postDelayed(this, 100);
//			}
//		};
//		h.post(run);
	}
}
