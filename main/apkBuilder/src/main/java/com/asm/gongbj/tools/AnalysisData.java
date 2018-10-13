package com.asm.gongbj.tools;


import java.util.*;

public class AnalysisData
{
	public String fullLog;
	public int exitValue;
	public String[] cmd;
	public int time;
	public List<ErrData> errData = new ArrayList<ErrData>();
	public static class ErrData
	{
		public ErrData()
		{

		}
		public static final int ERROR = 1;
		public static final int WARNING = 2;
		public static final int INFO = 3;


		public String filePath;
		//for ManifestMerger
		public String filePath2;
		//
		public int lineNumber;
		public String comment;

		//for Ecj
		public int type; // Also for ManifestMerger
		public String line;
		public String errorCode;
		//
	}
	public String toString()
	{
		String str= "";
		str += " exitValue : ";

		str += String.valueOf(exitValue);
		str += "\n time : ";
		str += String.valueOf(time);
		str += " \nfullLog : " + fullLog + "\n\n";
		for (ErrData e : this.errData)
		{
			if (e != null){
				str += "\n\ncomment : ";
				str +=  e.comment;
				str += "\nfilePath : " + e.filePath;
				str += "\nlineNumber : " + String.valueOf(e.lineNumber);
			}
		}
		return str;

	}
}
