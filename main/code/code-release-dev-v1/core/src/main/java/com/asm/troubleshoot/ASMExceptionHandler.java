package com.asm.troubleshoot;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.os.Process;

import java.io.Writer;
import java.io.StringWriter;
import java.io.PrintWriter;
import android.app.*;


public class ASMExceptionHandler implements Thread.UncaughtExceptionHandler
{
	private static Activity sActivity;
	private static final ASMExceptionHandler THE_ONE = new ASMExceptionHandler();
	
	
	public static void init(Activity act) {
		sActivity = act;
		Thread.setDefaultUncaughtExceptionHandler(THE_ONE);
	}
	
	
	private ASMExceptionHandler() {}
	
	
	@Override
	public void uncaughtException(Thread thread, Throwable throwable) {
		Intent i = new Intent(sActivity, DebugActivity.class);
		Writer stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		while (throwable != null) {
			throwable.printStackTrace(printWriter);
			throwable = throwable.getCause();
		}
		String obj = stringWriter.toString();
		Log.d("err", obj);
		i.putExtra("error", obj);
		PendingIntent pIntent = PendingIntent.getActivity(sActivity, 
		sActivity.getSystemService(AlarmManager.class).set(AlarmManager.ELAPSED_REALTIME, new PendingIntent(sActivity, 
		
		sActivity.finish();
		Process.killProcess(android.os.Process.myPid());
	}
}
