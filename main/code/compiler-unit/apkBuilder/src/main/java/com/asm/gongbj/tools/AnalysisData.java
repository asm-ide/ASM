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
	}
}
