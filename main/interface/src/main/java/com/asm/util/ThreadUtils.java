package com.asm.util;

public class ThreadUtils
{
	private static ThreadGroup sRootGroup = new ThreadGroup("ASM");
	private static int sThreadCount = 0;
	
	
	public static ThreadGroup getRootGroup() {
		return sRootGroup;
	}
}
