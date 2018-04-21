package com.asm.gongbj.gradle;
import java.io.*;

public class ProgressFail extends Exception
{
	String explane;
	String filePath;
	String toolName;
	public ProgressFail(String explane,String filePath, String toolName){
		this.explane=explane;
		this.filePath=filePath;
		this.toolName=toolName;
	}
	public String getToolName(){
		return toolName;
	}
	public String getExplane(){
		return explane;
	}
	public File getFile(){
		return new File(filePath);
	}
	public String getfilePath(){
		return filePath;
	}
	public String toString(){
		return explane;
	}
}
