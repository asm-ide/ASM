package com.asm.gongbj;
import android.app.*;
import com.asm.ASMT.*;
import java.io.*;
import android.os.*;
import android.widget.*;

public class checkErr
{
	Activity activity;
	public checkErr(Activity ac){
		activity=ac;
		if(!("com.asm".equals(ac.getPackageName()))){
			Toast.makeText(ac,"CheckErr is Only For ASM",Toast.LENGTH_SHORT).show();
		}
	}
	
	public errList[] checkErr(String projectPath,String targrtPath)throws Exception{
		return checkErr(projectPath,targrtPath,null);
	}
	public errList[] checkErr(String projectPath)throws Exception{
		return checkErr(projectPath,projectPath,null);
	}
	public errList[] checkErr(String projectPath,com.asm.gongbj.tempData tempdata)throws Exception{
		return checkErr(projectPath,projectPath,tempdata);
	}
	public errList[] checkErr(String projectPath,String targetPath,com.asm.gongbj.tempData tempdata) throws Exception{
		boolean checkHole = false;
		if(tempdata==null){
			////No String Input
			if(projectPath==targetPath)checkHole=true;
			
			com.asm.ASMT.apkBuilder ab = new com.asm.ASMT.apkBuilder(activity);
			if(ab.prepare()){
				
				if(checkHole){
					//Hole
					String logOut ="";
					Result re = ab.buildApk(projectPath,1);
					//if(re.getResult()==0){
						//logOut = re.getLogOut();
					//}
					re = ab.buildApk(projectPath,3);
					if(re.getResult()==1){
						return null;
					}
					String l = re.getLogOut();
					String lS[] = l.split("----------");
					lS[0]="";
					lS[lS.length-1]="";
					int count = 0;
					errList e[] = new errList[lS.length-2];
					for(String s : lS){
						if(s!=""){
							e[count] = get(s);
							count++;
						}
					}
					return e;
				}else{
					//Not Hole
					if(targetPath.contains(".java")){
						Result re = ab.buildApk(projectPath,1);
						if(re.getResult()==1){
							ab.moveJavas();
							re = ab.comlileCJ(targetPath.replaceAll("/java","/gen"));
							if(re.getResult()==1){
								return null;
							}
							String l = re.getLogOut();
							String lS[] = l.split("----------");
							lS[0]="";
							lS[lS.length-1]="";
							int count = 0;
							errList e[] = new errList[lS.length-2];
							for(String s : lS){
								if(s!=""){
									e[count] = get(s);
									count++;
								}
							}
							return e;
						}
					}else if(targetPath.contains(".xml")){

					}
				}
				
			}else{
				throw new Exception("cannot prepare");
			}
			
			
		}else{
			/////tempData Input.
			
			if(projectPath==targetPath)checkHole=true;

			com.asm.ASMT.apkBuilder ab = new com.asm.ASMT.apkBuilder(activity);
			if(ab.prepare()){

				if(checkHole){
					//Hole
					String logOut ="";
					Result re = ab.buildApk(projectPath,1);
					//if(re.getResult()==0){
					//logOut = re.getLogOut();
					//}
					ab.moveJavas();
					
					
					for(String str : tempdata.getFileList()){
						String save = str.replaceAll("/java","/gen");
						try{
							File file = new File(save); 
							// If file doesn't exists, then create it 
							if (!file.exists()) { 
								file.createNewFile(); 
							} 
							FileWriter fw = new FileWriter(file.getAbsoluteFile()); 
							BufferedWriter bw = new BufferedWriter(fw); 
							// Write in file 
							bw.write(tempdata.getData(str)); 
							// Close connection 
							bw.close();
						}catch(Exception e){
						}
					}
					
					re = ab.comlileCJ(projectPath);
					if(re.getResult()==1){
						return null;
					}
					String l = re.getLogOut();
					String lS[] = l.split("----------");
					lS[0]="";
					lS[lS.length-1]="";
					int count = 0;
					errList e[] = new errList[lS.length-2];
					for(String s : lS){
						if(s!=""){
							e[count] = get(s);
							count++;
						}
					}
					return e;
					
				}else{
					//Not Hole
					if(targetPath.contains(".java")){
						
						Result re = ab.buildApk(projectPath,1);
						if(re.getResult()==1){
							ab.moveJavas();
							for(String str : tempdata.getFileList()){
								String save = str.replaceAll("/java","/gen");
								try{
									File file = new File(save); 
									// If file doesn't exists, then create it 
									/*
									if (!file.exists()) { 
										file.createNewFile(); 
									} 
									*/
									FileWriter fw = new FileWriter(file.getAbsoluteFile()); 
									BufferedWriter bw = new BufferedWriter(fw); 
									// Write in file 
									bw.write(tempdata.getData(str)); 
									// Close connection 
									bw.close();
								}catch(Exception e){
									throw e;
								}
								
							}
							re = ab.comlileCJ(targetPath.replaceAll("/java","/gen"));
							if(re.getResult()==1){
								return null;
							}
							String l = re.getLogOut();
							String lS[] = l.split("----------");
							lS[0]="";
							lS[lS.length-1]="";
							int count = 0;
							errList e[] = new errList[lS.length-2];
							for(String s : lS){
								if(s!=""){
									e[count] = get(s);
									count++;
								}
							}
							return e;
							
						}
						
					}else if(targetPath.contains(".xml")){

					}
				}
				
			}else{
				throw new Exception("cannot prepare");
			}
			
		}
		
		return null;
	}
	
	
	private errList get(String data){
		String filePath="";
		String ErrLineCode="";
		String ErrCode="";
		String explane="";
		int line=0;
		int type=1; 
		
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
			
			return new errList(filePath,ErrLineCode,ErrCode,explane,line,type);
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

			return new errList(filePath,ErrLineCode,ErrCode,explane,line,type);

		}else{
			return null;
		}
	}
}
