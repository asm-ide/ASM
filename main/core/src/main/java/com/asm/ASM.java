package com.asm;

import com.asm.plugin.PluginLoader;
import com.asm.troubleshoot.ASMExceptionHandler;

import android.app.Activity;
import android.app.Service;
import android.content.Context;

import java.lang.ref.WeakReference;


public class ASM
{
	private static boolean sIsLocal; // true to activity
	
	
	public static void init(Activity activity) {
		AppContext.attachAppContext(activity);
		sIsLocal = true;
		init();
		
		ASMExceptionHandler.init(activity);
	}
	private static void init() {
		Context context = AppContext.get();
		
		Settings.loadSettingIfNotLoaded(context);
		
		//PluginLoader.setClassLoader(null);
		
		
	}
	
	private static void initRes() {
		Res.attachResources(AppContext.get().getResources());
		Res.APP_NAME = R.string.app_name;
	}
}
