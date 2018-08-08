package com.asm.gongbj.gradle;
import android.app.*;
import com.asm.ASMT.*;
import com.asm.gongbj.gradle.sync.*;
import com.asm.gongbj.tools.*;
import com.asm.lib.io.*;
import java.io.*;
import java.util.*;


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
		
		//Start Dxing
		{
			Dx dx = new Dx();
			progL.onProgressChange("dxing...");

			String desPath = mainGradlePath + "/build/bin/dex/";
			{
				File f = new File(desPath);
				if(!(f.exists())) {
					f.mkdirs();
				}else{
					if(f.isFile()){
						f.delete();
						f.mkdirs();
					}
				}
			}

			//dx jars
			for(String jarPath : syncD.getScanedJar()){
				progL.onProgressChange("dexing : " + new File(jarPath).getName() + "...");
				AnalysisData ad2 = DxResultAnalyze.analysis(dx.jar2dex(jarPath,desPath + new File(jarPath).getName() + ".dex"));
				if(ad2.exitValue!=0){
					resultValue = 0;
					ProgressFail pf = new ProgressFail("dexing failed",jarPath,"Gradle");
					pf.analysisData = ad2;
					errorL.onError(pf);
				}
			}

			//dx classes
			progL.onProgressChange("dxing : projects...");
			{
				AnalysisData ad2 = DxResultAnalyze.analysis(dx.class2dex(new String[]{mainGradlePath+"/build/bin/class"},desPath + "main.dex"));
				if(ad2.exitValue!=0){
					resultValue = 0;
					ProgressFail pf = new ProgressFail("dexing failed",mainGradlePath+"/build/bin/class","Gradle");
					pf.analysisData = ad2;
					errorL.onError(pf);

				}
			}
		}
		
		
		if(resultValue == 0){
			return;
		}
		
		//Start Merge
		{
			progL.onProgressChange("dx merge...");
			DexMerge dxm = new DexMerge();
			String dexPath = mainGradlePath + "/build/bin/dex/";
			String desPath = mainGradlePath + "/build/bin/classes.dex";
			
			String dexes[] = new String[syncD.getScanedJar().length+1];
			for(int i = 0; i < syncD.getScanedJar().length; i++){
				
				dexes[i] = dexPath + new File(syncD.getScanedJar()[i]).getName() + ".dex";
			}
			dexes[dexes.length-1] = dexPath + "main.dex";
			
			if(dexes.length>1){
				progL.onProgressChange("dx merge : main...");
				AnalysisData ad2 = dxm.Merge(desPath,dexes[0],dexes[dexes.length-1]);
				if(ad2.exitValue!=0){
					resultValue = 0;
					ProgressFail pf = new ProgressFail("dex merge failed",dexes[dexes.length-1],"Gradle");
					pf.analysisData = ad2;
					errorL.onError(pf);

				}
				for(int i = 1; i < dexes.length-1; i++){
					String name = dexes[i].substring(dexes[i].lastIndexOf("/")+1,dexes[i].lastIndexOf(".jar.dex"));
					progL.onProgressChange("dx merge : " + name + "...");
					AnalysisData ad3 = dxm.Merge(desPath,desPath,dexes[i]);
					if(ad2.exitValue!=0){
						resultValue = 0;
						ProgressFail pf = new ProgressFail("dex merge failed",dexes[dexes.length-1],"Gradle");
						pf.analysisData = ad3;
						errorL.onError(pf);

					}
				}
			}else{
				progL.onProgressChange("dx merge : main...");
				AnalysisData ad2 = dxm.Merge(desPath,dexes[0],dexes[0]);
				if(ad2.exitValue!=0){
					resultValue = 0;
					ProgressFail pf = new ProgressFail("dex merge failed",dexes[0],"Gradle");
					pf.analysisData = ad2;
					errorL.onError(pf);
					
				}
			}
			
		}
		
		if(resultValue==0)return;
		
		
		//Start aapt
		Aapt aapt = new Aapt(androidJar);
		
		String apkPath = mainGradlePath + "/build/bin/app.apk";
		String manifestPath = mainGradlePath+"/src/main/AndroidManifest.xml";
		String resPath[] =  new String[syncD.getSyncedProjectPath().length];
		for(int i = 0; i < syncD.getSyncedProjectPath().length; i++){
			resPath[i] = syncD.getSyncedProjectPath()[i] + "/src/main/res";
		}
		//resPath[resPath.length-1] = mainGradlePath+"/src/main/res";
		progL.onProgressChange("APK building...");
		{
			AnalysisData ad2 = AaptResultAnalyze.analysis(aapt.generateApk(apkPath,manifestPath,resPath,syncD.getScanedJar()));
			if(ad2.exitValue != 0){
				resultValue = 0;
				ProgressFail pf = new ProgressFail("Apk building failed",apkPath,"Gradle");
				pf.analysisData = ad2;
				errorL.onError(pf);
			}
			if(resultValue==0)return;
		}
		{
			AnalysisData ad2 = AaptResultAnalyze.analysis(aapt.addFileInApk(apkPath,mainGradlePath + "/build/bin/classes.dex"));
			if(ad2.exitValue != 0){
				resultValue = 0;
				ProgressFail pf = new ProgressFail("Apk building failed",apkPath,"Gradle");
				pf.analysisData = ad2;
				errorL.onError(pf);
			}
			if(resultValue==0)return;
		}
		
		//sign apk
		{
			progL.onProgressChange("Apk sign...");
			String cmd = "-M auto-testkey -I " + mainGradlePath + "/build/bin/app.apk -O " + mainGradlePath + "/build/bin/appSigned.apk";
			long start=0;
			int i, rc = 99;
			AnalysisData ad2 = new AnalysisData();
			ad2.cmd = IDE.fnTokenize(cmd);
			start = System.currentTimeMillis();
			try
			{
				StringWriterOutputStream swos = new StringWriterOutputStream();
				G.ide.fnRedirectOutput(swos);
				
				// start SignApk
				rc = SignApk.main(IDE.fnTokenize(cmd));
				ad2.fullLog = swos.toString();
			}
			catch (Throwable t)
			{
				rc = 99;
				ad2.fullLog = t.toString();
				t.printStackTrace();
			}finally{
				ad2.exitValue = rc;
				ad2.time = (int)(System.currentTimeMillis()-start)/1000;
			}
			
			if(ad2.exitValue != 0){
				resultValue = 0;
				ProgressFail pf = new ProgressFail("Apk Sign failed",apkPath,"Gradle");
				pf.analysisData = ad2;
				errorL.onError(pf);
			}
			if(resultValue != 0){
				return;
			}
		}
		
		progL.onprogressFinish();
		
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
