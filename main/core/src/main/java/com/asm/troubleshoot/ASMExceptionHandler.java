package com.asm.troubleshoot;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.os.Process;

import java.io.Writer;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;


public class ASMExceptionHandler implements Thread.UncaughtExceptionHandler
{
	private static WeakReference<Activity> sActivity;
	private static final ASMExceptionHandler THE_ONE = new ASMExceptionHandler();
	
	
	public static void init(Activity act) {
		sActivity = new WeakReference<Activity>(act);
		Thread.setDefaultUncaughtExceptionHandler(THE_ONE);
	}
	
	public static ASMExceptionHandler getInstance() {
		return THE_ONE;
	}
	
	
	private ASMExceptionHandler() {}
	
	
	@Override
	public void uncaughtException(Thread thread, Throwable throwable) {
		Activity activity = sActivity.get();
		Intent intent = new Intent(activity, DebugActivity.class);
		Writer stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		while (throwable != null) {
			throwable.printStackTrace(printWriter);
			throwable = throwable.getCause();
		}
		String obj = stringWriter.toString();
		Log.d("err", obj);
		intent.putExtra("error", obj);
		PendingIntent pIntent = PendingIntent.getActivity(activity, 10, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_CANCEL_CURRENT);
		((AlarmManager) activity.getSystemService(Context.ALARM_SERVICE)).set(AlarmManager.ELAPSED_REALTIME, 500L, pIntent);
		Process.killProcess(android.os.Process.myPid());
	}
}
