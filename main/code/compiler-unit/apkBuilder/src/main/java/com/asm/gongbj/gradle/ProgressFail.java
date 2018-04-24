package com.asm.gongbj.gradle;
import java.io.*;
import com.asm.gongbj.tools.*;

public class ProgressFail extends Exception
{
	String explane;
	String filePath;
	String toolName;
	AnalysisData analysisData;
	public ProgressFail(String explane,String filePath, String toolName){
		this.explane=explane;
		this.filePath=filePath;
		this.toolName=toolName;
		this.analysisData = null;
	}
	public void setAnalysisData(AnalysisData a){
		this.analysisData = a;
	}
	public AnalysisData getAnalysisData(){
		return analysisData;
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
