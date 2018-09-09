package com.asm;

import com.asm.util.Reflection;
import com.asm.util.ProtectedAccess;
import com.asm.util.WeakProtectedValue;

import android.content.Context;
import android.test.mock.MockContext;
import java.io.File;
import com.asm.annotation.RequiresModule;

/**
 * Provide all in-module classes with context.
 * 
 */
public final class AppContext
{
	private static final WeakProtectedValue<Context> mContext = new WeakProtectedValue<Context>(null);
	private static ContextStub sStub = new ContextStub();
	
	
	@RequiresModule
	@ProtectedAccess
	public static Context get() {
		checkPermission();

		return mContext.get();
	}
	
	public static Context getStub() {
		return sStub;
	}
	
	@ProtectedAccess
	public static void attachAppContext(Context context) {
		checkPermission();

		mContext.set(context);
	}
	
	private static void checkPermission() {
		ModulePermission.enforcePermission(3);
	}
}


class ContextStub extends MockContext
{
	@Override
	public File getDataDir() {
		return AppContext.get().getDataDir();
	}
	
	@Override
	public File getFilesDir() {
		return AppContext.get().getFilesDir();
	}
	
	@Override
	public File getCacheDir() {
		return AppContext.get().getCacheDir();
	}
}
