package com.asm.gongbj.gradle;

public abstract class GradleVisiter
{
	public GradleVisiter(){
		onVisit("","");
	}
	public void visit(String code){
		//trim code
		{
			code = code.trim();
			code = code.replace("*/\\","●");
			code = code.replace("\\\\\"","<cleancodeTempString**>");
			code = code.replace("\\\"","^");
			code = code.replace("<cleancodeTempString**>","\\\\\"");

			while(code.contains("/*")&&code.contains("*/")){
				int i = code.indexOf("/*");
				int i2 = code.indexOf("*/",i);
				if(i2==-1)break;
				String s = code.substring(i,i2+2);
				code = code.replace(s,"");
			}
			while(code.contains("//")){
				int i = code.indexOf("//");
				int i2 = code.indexOf("\n",i);
				if(i2==-1)break;
				code = code.replace(code.substring(i,i2),"");
			}
		}
		count = 0;
		code = code.replaceAll("(\\n+)\\{","{");
		code = code.replace("{","{\n");
		code = code.replace("}","}\n");
		String[] str = code.split("\n");
		readBuildGradle(str,"");
	}
	public abstract void onVisit(String tree, String line)
	
	private int count;
	private void readBuildGradle(String[] str,String tree){
		
		String sr = tree;

		while(count<str.length){
			if("}".equals(str[count].trim())){
				count++;
				return;
			}else if(str[count].contains("{")){
				count++;
				readBuildGradle(str,sr+"."+str[count-1].replace("{","").trim());
			}else{
				if(!(str[count].trim().equals(""))){
					onVisit(tree,str[count].trim());
				}
				count++;
			}
			
		}

		return;
	}
	
}
