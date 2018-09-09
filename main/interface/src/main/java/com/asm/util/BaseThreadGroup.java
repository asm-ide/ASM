package com.asm.util;


public class BaseThreadGroup extends ThreadGroup
{
	private String mTitle = "";
	private String mDescription = "";
	
	
	public BaseThreadGroup(String name) {
		super(name);
	}
	
	public BaseThreadGroup(ThreadGroup parent, String name) {
		super(parent, name);
	}
	
	
	public void setTitle(String title) {
		mTitle = title;
	}
	
	public String getTitle() {
		return mTitle;
	}
	
	public void setDescription(String desc) {
		mDescription = desc;
	}
	
	public String getDescription() {
		return mDescription;
	}
	
	
	public static BaseThreadGroup currentGroup() {
		return (BaseThreadGroup) Thread.currentThread().getThreadGroup();
	}
}
