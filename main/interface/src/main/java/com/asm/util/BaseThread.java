package com.asm.util;

import com.asm.annotation.NonNull;


public class BaseThread extends Thread
{
	public static interface OnDataChangedListener
	{
		public void onProgressChanged(BaseThread thread);
	}
	
	
	private String mDescription;
	private int mProgress = -1;
	private OnDataChangedListener mListener;
	
	
	public BaseThread(String name) {
		super(ThreadUtils.getRootGroup(), name);
	}
	
	public BaseThread(Runnable runnable, String name) {
		super(ThreadUtils.getRootGroup(), runnable, name);
	}
	
	public BaseThread(Runnable runnable) {
		super(ThreadUtils.getRootGroup(), runnable);
	}
	
	public BaseThread(ThreadGroup group, Runnable runnable, String name) {
		super(group, runnable, name);
	}
	
	
	@Override
	public void start() {
		
	}
	
	
	public static BaseThread currentThread() {
		Thread thread = Thread.currentThread();
		if(thread instanceof BaseThread)
			return (BaseThread) thread;
		
		return null;
	}
	
	public void setDescription(String desc) {
		mDescription = desc;
		
		callListener();
	}
	
	public @NonNull String getDescription() {
		return mDescription == null? "" : mDescription;
	}
	
	public int getProgress() {
		return mProgress;
	}
	
	public void publishProgress(int prog) {
		mProgress = prog;
		
		callListener();
	}
	
	private void callListener() {
		if(mListener != null)
			mListener.onProgressChanged(this);
	}
}
