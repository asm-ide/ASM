package com.asm.gongbj.tools;

import android.support.annotation.*;
import java.util.*;

public class AnalysisData
{
	
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
		public String type;
		public String line;
		public String errorCode;
		//
	}
	public String toString(){
		String str = "exitValue : ";
		str += String.valueOf(exitValue);
		str += "\n time : ";
		str += String.valueOf(time);
		str += "";
		for(errData e : this.errData){
			str = str + "\n\ncomment : " + e.comment.toString() + "\nfilePath : " + e.filePath.toString() + "\nlineNumber : "+ String.valueOf(e.lineNumber);
		}
		return str;
	}
}
