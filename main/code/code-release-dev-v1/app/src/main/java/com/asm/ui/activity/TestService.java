package com.asm.ui.activity;

import android.app.*;
import android.content.*;
import android.os.*;
import com.asm.io.*;
import java.io.*;
import android.util.*;
import android.widget.*;


public class TestService extends Service
{
	public class MyTestService extends ITestService.Stub
	{
		public void work(String type, final RemoteStream stream) {
			switch(type) {
				case "1":
					mHandler.post(new Runnable() {
							@Override
							public void run() {
								try {
									stream.writeUTF("abcde");
									stream.close();
								} catch(IOException e) {
									e.printStackTrace();
									Toast.makeText(TestService.this, e.getMessage(), Toast.LENGTH_LONG).show();
								}
								
								TestService.this.stopSelf();
							}
						});
					break;
			}
		}
	}
	
	
	private class MyThread extends Thread
	{
		@Override
		public void run() {
			Looper.prepare();
			mHandler = new Handler();
			
			while(!mExited) {
				Looper.loop();
			}
		}
	}
	
	private final ITestService mBinder = new MyTestService();
	private MyThread mThread;
	private Handler mHandler;
	private boolean mExited = false;
	
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder.asBinder();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		if(mThread == null) {
			mThread = new MyThread();
			mThread.start();
		}
		Toast.makeText(this, "hi", Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mExited = true;
	}
}
