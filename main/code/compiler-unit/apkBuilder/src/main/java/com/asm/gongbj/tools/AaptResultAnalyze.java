package com.asm.gongbj.tools;

public class AaptResultAnalyze
{
	public static AnalysisData analysis(String result){
		AnalysisData data  = new AnalysisData();
		String cmdStr = result.substring(0,result.indexOf("\n\n")).trim();
		String elseStr = result.substring(result.indexOf("\n\n")).trim();
		data.cmd = cmdStr.split("\n");
		String split[] = elseStr.split("\n");
		for(String line : split){
			if(line.contains("Done in")){
				line = line.trim();
				line = line.substring("Done in".length()+1);
				line = line.substring(0,line.indexOf("sec"));
				line = line.trim();
				data.time = Integer.parseInt(line+0);
			}else if(line.contains("ExitValue")){
				line = line.trim();
				line = line.substring("ExitValue:".length()).trim();
				data.exitValue = Integer.parseInt(line);//line.equals("0") ? 0 : 1;
			}else if(line.toLowerCase().contains("error")){
				line = line.trim();
				if(line.startsWith("ERROR:")){
					AnalysisData.errData ed = new AnalysisData.errData();
					ed.filePath = null;
					ed.lineNumber = -1;
					ed.comment = line.substring("ERROR:".length(),line.length());
					data.errData.add(ed);
					
				}else if(line.contains("error: Error:")){
					AnalysisData.errData ed = new AnalysisData.errData();
					ed.filePath = line.substring(0,line.indexOf(":")).trim();
					ed.lineNumber = Integer.parseInt(line.substring(line.indexOf(":")+1,line.indexOf(":",line.indexOf(":")+1)).trim()+0);
					ed.comment = line.substring(line.indexOf("Error:")+"Error:".length());
					data.errData.add(ed);
				}else if(line.contains("error:")){
					AnalysisData.errData ed = new AnalysisData.errData();
					ed.filePath = line.substring(0,line.indexOf(":")).trim();
					ed.lineNumber = -1;
					ed.comment = line.substring(line.indexOf("error:")+"error:".length());
					data.errData.add(ed);
				}
			}
		}
		return data;
	}
}
