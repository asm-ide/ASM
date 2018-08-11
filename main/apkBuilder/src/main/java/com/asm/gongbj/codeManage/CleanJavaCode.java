package com.asm.gongbj.codeManage;

public class CleanJavaCode
{
	public static String clean(String str, String tap){

		String lines[] = clean1(str).split("\n");
		int h = 0;
		for(int i = 0; i<lines.length; i++){
			lines[i] = lines[i].trim();
			
			if(lines[i].startsWith("}")){
				int cf = countFormFront(lines[i],"}");
				h -= cf;if(h<0)h=0;
				String t = multiplyStr(tap,h);
				String temp = lines[i];
				lines[i] = t;// + lines[i];

				h += count(temp,"{");
				h -= count(temp,"}") - cf;
				if(h<0)h=0;

			}else{
				String t = multiplyStr(tap,h);
				String temp = lines[i];
				lines[i] = t;// + lines[i];
				
				h += count(temp,"{");
				h -= count(temp,"}");
				if(h<0)h=0;
			}

		}
		
		String rLines[] = str.split("\n");
		
		String rS="";
		for(int i=0; i < rLines.length; i++){
			rS += lines[i] + rLines[i].trim() + "\n";
		}
		
		return rS.trim();
	}
	private static String clean1(String str){
		str.replace("*/\\","●");
		str.replace("\\\\\"","<cleancodeTempString**>");
		str.replace("\\\"","^");
		str.replace("<cleancodeTempString**>","\\\\\"");
		
		while(str.contains("/*")&&str.contains("*/")){
			int i = str.indexOf("/*");
			int i2 = str.indexOf("*/",i);
			if(i2==-1)break;
			String s = str.substring(i,i2+2);
			str = str.replace(s,"^"+multiplyStr("^\n^",count(s,"\n"))+"^");
		}
		while(str.contains("//")){
			int i = str.indexOf("//");
			int i2 = str.indexOf("\n",i);
			if(i2==-1)break;
			str = str.replace(str.substring(i,i2),"<r>");
			
		}
		while(count(str,"\"")>1){
			int i = str.indexOf("\"");
			/*
			while(str.substring(i-1,i)!="\\"){
				i = str.indexOf("\"",i+1);
			}
			*/
			int i2 = str.indexOf("\"",i+1);
			/*
			while(str.substring(i2-1,i2)!="\\"){
				i2 = str.indexOf("\"",i+1);
			}
			*/
			if(i2<0)break;
			String s = str.substring(i,i2+1);
			str = str.replace(s,"^"+multiplyStr("^\n^",count(s,"\n"))+"^");
		}
		return str;
	}
	private static int count(String str,String c) {
		return str.length() - str.replace(c,"").length();
	}

	private static int countFormFront(String str, String c){
		int count = 0;
		str = str.trim();
		while(str.startsWith(c)){
			count++;
			str = str.substring(1,str.length()).trim();
		}
		return count;
	}
	private static String multiplyStr(String str, int i){
		String r = "";
		if(i<=0)return "";
		for(int c = 0; c<i; c++){
			r = r+str;
		}
		return r;
	}
}
