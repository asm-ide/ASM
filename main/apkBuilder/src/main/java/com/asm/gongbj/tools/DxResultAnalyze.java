package com.asm.gongbj.tools;

public class DxResultAnalyze
{
	public static AnalysisData analysis(String result){
		AnalysisData ad = new AnalysisData();
		ad.cmd =  result.substring(result.trim().indexOf("Dx arguments:")+"Dx arguments:".length()+1,result.indexOf("\n\n\n")).trim().split("\n");
		ad.fullLog = result;
		
		{
			String t = result;
			t = t.trim();
			t = t.substring(t.lastIndexOf("ExitValue:"));
			t = t.substring("ExitValue:".length()+1).trim();
			ad.exitValue = Integer.parseInt(t);
		}
		
		{
			String t = result;
			t = t.trim();
			t = t.substring(t.lastIndexOf("Done in"));
			t = t.substring("Done in".length()+1);
			t = t.substring(0,t.indexOf("sec"));
			t = t.trim();
			ad.time = Integer.parseInt(t+0);
		}
		
		return ad;
	}
}
