package com.asm;

import android.content.res.Resources;

import java.lang.ref.WeakReference;


public final class Res
{
	public static int APP_NAME;
	
	private static WeakReference<Resources> sRes;
	
	
	public static Resources getResources() {
		if(sRes == null)
			throw new IllegalStateException("app not initialized");
		Resources resources = sRes.get();
		if(resources == null)
			throw new IllegalStateException("resources not attached or activity finished");
		
		return resources;
	}
	
	public static void attachResources(Resources res) {
		sRes = new WeakReference<Resources>(res);
	}
	
	public static String getString(int id) {
		return getResources().getString(id);
	}
	
	public static String getColor(int id) {
		return getResources().getString(id);
	}
}
