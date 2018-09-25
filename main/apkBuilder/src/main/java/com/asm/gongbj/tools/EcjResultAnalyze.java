package com.asm.gongbj.tools;

public class EcjResultAnalyze
{
	public static AnalysisData analysis(String result){
		AnalysisData data = new AnalysisData();
		
		String lS[] = result.split("----------");
		int count = 0;
		data.fullLog = result;
		for(String line : lS){
			if(line.equals(lS[0])||line.equals(lS[lS.length-1])){
				
				if(line.contains("Compilation arguments:")){
					line.trim();
					line.substring("Compilation arguments:".length()+1);
					data.cmd=line.split("\n");
				}
				
				if(line.contains("Done in")){
					String t = line;
					t = t.trim();
					t = t.substring(line.lastIndexOf("Done in"));
					t = t.substring("Done in".length()+1);
					t = t.substring(0,t.indexOf("sec"));
					t = t.trim();
					data.time = Integer.parseInt(t+0);
				}
				if(line.contains("ExitValue")){
					String t = line;
					t = t.trim();
					t = t.substring(t.lastIndexOf("ExitValue:"));
					t = t.substring("ExitValue:".length()+1).trim();
					data.exitValue = Integer.parseInt(t);
				}
			}else{
				AnalysisData.ErrData ed = get(line);
				if(ed!=null){
					data.errData.add(ed);
				}
			}
		}
		return data;
	}
	
	private static AnalysisData.ErrData get(String data){
		String filePath="";
		String ErrLineCode="";
		String ErrCode="";
		String explane="";
		int line=0;
		int type=1; 
		data = data.trim();
		if(data.contains("ERROR")){
			////ERROR
			filePath = data.substring(data.indexOf("ERROR in")+"ERROR in".length(),data.indexOf("(at line")).trim();
			line = Double.valueOf(data.substring(data.indexOf("(at line")+"(at line".length(),data.indexOf(")\n")).trim()).intValue();
			type = 1;
			ErrLineCode=data.substring(data.indexOf( String.valueOf(line)+")")+(String.valueOf(line)+")").length(),data.indexOf("^")).trim();
			explane = data.substring(data.lastIndexOf("^")+1).trim();

			if(data.substring(data.lastIndexOf("^")).split("\"").length>=3){
				String temp = data.substring(data.lastIndexOf("^")+1);
				ErrCode = temp.substring(temp.indexOf("\"")+1,temp.lastIndexOf("\"")).trim();
			}else{
				//ErrCode="";
			}
			AnalysisData.ErrData ed = new AnalysisData.ErrData();
			ed.comment = explane;
			ed.errorCode = ErrCode;
			ed.line = ErrLineCode;
			ed.filePath = filePath;
			ed.lineNumber = line;
			ed.type = type;
			return ed;
		}else if(data.contains("WARNING")){
			filePath = data.substring(data.indexOf("WARNING in")+"WARNING in".length(),data.indexOf("(at line")).trim();
			line = Double.valueOf(data.substring(data.indexOf("(at line")+"(at line".length(),data.indexOf(")\n")).trim()).intValue();
			type = 2;
			ErrLineCode=data.substring(data.indexOf( String.valueOf(line)+")")+(String.valueOf(line)+")").length(),data.indexOf("^")).trim();
			explane = data.substring(data.lastIndexOf("^")+1).trim();

			if(data.substring(data.lastIndexOf("^")).split("\"").length>=3){
				String temp = data.substring(data.lastIndexOf("^")+1);
				ErrCode = temp.substring(temp.indexOf("\"")+1,temp.lastIndexOf("\"")).trim();
			}else{
				//ErrCode="";
			}

			AnalysisData.ErrData ed = new AnalysisData.ErrData();
			ed.comment = explane;
			ed.errorCode = ErrCode;
			ed.line = ErrLineCode;
			ed.filePath = filePath;
			ed.lineNumber = line;
			ed.type = type;
			return ed;
		}else{
			return null;
		}
	}
}
