package com.asm;

import android.app.Activity;
import android.app.Service;
import android.content.Context;


public class ASM
{
	private static Context sContext;
	private static boolean sIsLocal; // true to activity
	
	
	public static void initOnActivity(Activity activity) {
		sContext = activity;
		sIsLocal = true;
		init();
	}
	
	public static void initOnService(Service service) {
		sContext = service;
		sIsLocal = false;
		init();
	}
	
	private static void init() {
		Settings.loadSettingIfNotLoaded(sContext);
	}
	
	
}
