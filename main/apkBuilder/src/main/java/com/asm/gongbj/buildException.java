package com.asm.gongbj;

public class buildException extends Exception
{
	private String str = "";
	public static int JAR_NOT_FOUND = 1 ;
	public static int UNKNOWN_SEQUENCE_NAME = 2;
	
	public buildException(String strr,int exceptionType){
		str=strr;
		
	}
	public String toString(){
		if(!"".equals(str)){
			return str;
		}else{
			return null;
		}
	}
}
