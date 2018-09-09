package com.asm.util;


public class ThreadFactory implements java.util.concurrent.ThreadFactory
{
	private static final String FORMAT = "ASM/%1$s@%2$s";
	
	
	private String mTitle = "root";
	private String mName = null;
	private ThreadGroup mGroup = ThreadUtils.getRootGroup();
	
	
	private ThreadFactory() {}
	
	
	public ThreadFactory group(ThreadGroup group) {
		mGroup = group;
		return this;
	}
	
	@Override
	public Thread newThread(Runnable runnable) {
		BaseThread thread = new BaseThread(
			mGroup,
			runnable,
			String.format(FORMAT, mTitle, mName == null? "thread" : mName));
		thread.setDescription(mName);
		return thread;
	}
	
	
	public static ThreadFactory getInstanceForClass(Class clazz) {
		ThreadFactory st = new ThreadFactory();
		st.mTitle = clazz.getName();
		return st;
	}
}
