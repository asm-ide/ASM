package com.asm.gongbj.tools;

import android.support.annotation.*;
import java.util.*;

public class AnalysisData
{
	public String fullLog;
	public int exitValue;
	public String[] cmd;
	public int time;
	public List<errData> errData = new ArrayList<errData>();
	public static class errData{
		public errData(){
			
		}
		public String filePath;
		public int lineNumber;
		public String comment;
		//for Ecj
		public int type;
		public String line;
		public String errorCode;
		//
	}
	public String toString(){
		String str= "";
		try{
			str+= " exitValue : ";
			str += String.valueOf(exitValue);
			str += "\n time : ";
			str += String.valueOf(time);
			str += " \nfullLog : " + fullLog + "\n\n";
			for(errData e : this.errData){
				if(e!=null)str = str + "\n\ncomment : " + e.comment.toString() + "\nfilePath : " + e.filePath.toString() + "\nlineNumber : "+ String.valueOf(e.lineNumber);
			}
			
		}catch(Exception e){
			
		}
		
		return str;
	}
}
