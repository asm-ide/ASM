package com.asm.ui.activity;

import android.content.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.widget.*;
import com.asm.analysis.*;
import com.asm.language.*;
import com.asm.widget.codeedit.*;
import java.io.*;
import java.util.*;

import android.support.v7.widget.Toolbar;
import com.asm.ui.*;
import com.asm.io.*;
import android.support.v4.app.*;
import android.Manifest;
import android.app.*;
import android.content.pm.*;


public class CodeEditActivity extends AppCompatActivity
{
	private class MyServiceConnection implements ServiceConnection {
		@Override
		public void onServiceConnected(ComponentName p1, IBinder binder)
		{
			Toast.makeText(CodeEditActivity.this, "connected", Toast.LENGTH_LONG).show();
			ITestService svb = ITestService.Stub.asInterface(binder);
			try {
				File f = new File("/sdcard/myfile.txt");
				if(!f.exists()) f.mkdirs();
				FileStream fs = new FileStream("/sdcard/myfile.txt", Stream.MODE_READ | Stream.MODE_WRITE);
				RemoteStream rs = new RemoteStream(fs);
				svb.work("1", rs);
			} catch(Exception e) {
				Toast.makeText(CodeEditActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
		@Override
		public void onServiceDisconnected(ComponentName p1)
		{
			// TODO: Implement this method
		}
	}  
	
	
	private static final int REQUIRE_PERMISSION_CODE = 100;
	
	private Toolbar toolbar;
	private ScrollingTextView edit;
	private EditText dbg;
	private FloatingActionButton fab;
	private MyServiceConnection svc;
	
	

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
					Log.d("err", obj);
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
		
		if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
			onCreatePrimary();
		} else {
			ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUIRE_PERMISSION_CODE);
		}
	}
	
	private void onCreatePrimary() {
		//Toast.makeText(this, todo, Toast.LENGTH_LONG).show();
		Log.d(".", "before set view");
		final EditText e = new EditText(this);
		e.setTextColor(0xffcccccc);
		e.addTextChangedListener(new TextWatcher() {
				LanguageInfo info = LanguageLoader.fromXml(getAssets().open("language/java.xml")).getInfo();
			
			
				@Override
				public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					// TODO: Implement this method
				}

				@Override
				public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					//info.comments();
					Log.d("ce", "onTextChanged");
					e.getText().clearSpans();
					Random r = new Random();
					CodeIterator i = new CodeIterator();
					
					i.setCode(e.getText());
					i.setInfo(info);
					Log.d("ce", "start " + e.getText());
					
					//try {
					//	Thread.sleep(100L);
					//} catch(InterruptedException e) {}
					
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
		//setContentView(e);
		
		setContentView(R.layout.default_layout);
		svc = new MyServiceConnection();
		bindService(new Intent(this, TestService.class), svc, BIND_AUTO_CREATE);
		//Toast.makeText(this, "" + (), Toast.LENGTH_SHORT).show();
//		setContentView(R.layout.default_layout);
//		setContentView(R.layout.codeedit);
//		Log.d(".", "after set view");
//		
//		
//		toolbar = (Toolbar) findViewById(R.id.toolbar);
//		edit = (ScrollingTextView) findViewById(R.id.codeedit);
//		dbg = (EditText) findViewById(R.id.edit);
//		fab = (FloatingActionButton) findViewById(R.id.fab);
////		Toast.makeText(this, "index" + TextUtils.indexOf("abcdefg", "e", 0, 7), Toast.LENGTH_SHORT).show();
////		dbg = (TextView) findViewById(R.id.dbg);
////		edit.setDbg(dbg);
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode) {
			case REQUIRE_PERMISSION_CODE:
				if(resultCode == PackageManager.PERMISSION_GRANTED) {
					onCreatePrimary();
				} else {
					Toast.makeText(this, "permission not granted", Toast.LENGTH_LONG).show();
				}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(svc != null) unbindService(svc);
	}
}
