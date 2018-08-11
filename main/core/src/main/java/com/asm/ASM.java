package com.asm;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import com.asm.troubleshoot.ASMExceptionHandler;

import java.lang.ref.WeakReference;


public class ASM
{
	private static WeakReference<Context> sContext;
	private static boolean sIsLocal; // true to activity
	
	
	public static void initOnActivity(Activity activity) {
		sContext = new WeakReference<Context>(activity);
		sIsLocal = true;
		init();
		
		ASMExceptionHandler.init(activity);
	}
	
	public static void initOnService(Service service) {
		sContext = new WeakReference<Context>(service);
		sIsLocal = false;
		init();
		
		// TODO: ASMExceptionHandler
	}
	
	private static void init() {
		Context context = sContext.get();
		
		Settings.loadSettingIfNotLoaded(context);
		
	}
	
	
}
