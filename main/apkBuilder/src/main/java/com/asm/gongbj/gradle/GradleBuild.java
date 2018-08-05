package com.asm.gongbj.gradle;
import android.content.*;
import android.app.*;
import com.asm.gongbj.tools.*;
import com.asm.gongbj.gradle.sync.*;
import java.util.*;
import java.io.*;

public class GradleBuild
{
	private Activity ac;
	private int resultValue;
	private ProgressListener progL;
	private ErrorListener errorL;
	public GradleBuild(Activity con){
		ac = con;
	}
	public void setProgressListener(ProgressListener prog){
		progL = prog;
	}
	public void setErrorListener(ErrorListener error){
		errorL = error;
	}
	public void run(String androidGradlePath, String mainGradlePath){
		//Ready
		if(progL==null){
			throw new RuntimeException("Cannot start building because ProgressListener is null");
		}
		if(errorL==null){
			throw new RuntimeException("Cannot start building because ErorrListener is null");
		}
		resultValue = 1;
		//Prepare
		String androidJar = Aapt.getAndroidJarPath();
		if(androidJar==null){
			progL.onProgressChange("Preparing android.jar...");
			try
			{
				androidJar = Aapt.requestAndroidJar(ac);
			}
			catch (Exception e)
			{
				errorL.onError(new ProgressFail("Error while preparing android.jar","","Gradle"));
				resultValue=0;
			}

		}if(resultValue==0)return;
		
		//Setting for Sync
		Syncer s = new Syncer(ac);
		
		s.setProgressListener(new Syncer.ProgressListener(){
				@Override
				public void onProgressStart(){
					progL.onProgressStart();
				}
				@Override
				public void onProgressChange(String progressName){
					progL.onProgressChange(progressName);
				}
				@Override
				public void onprogressFinish(){
				}
			});
		s.setErrorListener(new Syncer.ErrorListener(){
				@Override
				public boolean onError(ProgressFail progressFail){
					resultValue=0;
					errorL.onError(progressFail);
					return true;
				}
			});
		
		//Start Sync
		SyncData syncD = null;
		try{
			syncD = s.sync(androidGradlePath,mainGradlePath);
		}catch(Exception e){
			errorL.onError(new ProgressFail("Error while syncing...",androidGradlePath,"sync"));
			resultValue=0;
		}
		
		
		//Start Ecj
		Ecj ecj = new Ecj(androidJar);
		progL.onProgressChange("Starting compiler...");
		ArrayList<String> projects = new ArrayList<String>();
		String ms = mainGradlePath+"/src/main/java";
		for(String str : syncD.getSyncedProjectPath()){
			String p1 = str + "/src/main/java";
			String p2 = str + "/build/gen";
			if(new File(p1).exists()&&!p1.equals(ms))projects.add(p1);
			if(new File(p2).exists())projects.add(p2);
		}
		progL.onProgressChange("Java compiling...");
		String log = ecj.compile(ms,projects.toArray(new String[projects.size()]),mainGradlePath+"/build/bin/class",syncD.getScanedJar());
		AnalysisData ad = EcjResultAnalyze.analysis(log);
		if(ad.exitValue==0){
			
		}else{
			resultValue = 0;
			ProgressFail pf = new ProgressFail("Java compile failed",mainGradlePath,"Gradle");
			pf.analysisData = ad;
			errorL.onError(pf);
		}
		
		//Quit Gradle Build if there were some errors.
		if(resultValue==0||syncD==null)return;
		
		//If there were no error, Continue...
		
		//Start aapt
		Aapt aapt = new Aapt(androidJar);
		
		
		
	}
	public static interface ProgressListener{

		public void onProgressStart();
		public void onProgressChange(String progressName);
		public void onprogressFinish();
	}
	public static interface ErrorListener{
		public void onError(ProgressFail progressFail);
	}
}
