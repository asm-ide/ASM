package com.asm.gongbj;

public class Result
{
	public static int RESULT_SUCCESS = 1;
	public static int RESULT_FAIL = 0;
	
	
	private String processName;
	private String logOut;
	private int result;
	public Result(String p,String l,int r){
		processName=p;
		logOut=l;
		result=r;
	}
	public String getProcessName(){
		return processName;
	}
	public String getLogOut(){
		return logOut;
	}
	public int getResult(){
		return result;
	}
}
