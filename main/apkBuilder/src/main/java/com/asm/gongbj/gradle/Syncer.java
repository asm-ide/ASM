package com.asm.gongbj.gradle;
import com.asm.gongbj.*;
import java.io.*;
import com.asm.gongbj.gradle.sync.*;
import com.asm.gongbj.gradle.info.*;
import android.app.*;
import android.widget.*;
import com.asm.gongbj.tools.*;
import com.asm.ASMT.*;
import android.content.*;
/**
 @author GongBJ
 */
public class Syncer
{
	private Activity ac;
	private ProgressListener progressListener;
	private ErrorListener errorListener;
	public Syncer(Activity activity){
		ac = activity;
	}
	public void setProgressListener(ProgressListener pro){
		progressListener = pro;
	}
	public void setErrorListener(ErrorListener err){
		errorListener = err;
	}
	/**
	 *this method sync the hole android project.
	 *It analyzes build.gradle, and prepare for some operations(like real-time error analyze)
	 *It generates R file from res.
	 *When there are error while reading res and gradle files, it calls 'onError' in ErrorListener Interface.
	 *@author GongBJ
	 *@param String androidProjectPath
	 *@param String mainGradlePath : the folder path of gradle project that you are going to build
	 */
	private String androidProjectPath, mainGradlePath;
	public SyncData sync(String androidProjectPath, String mainGradlePath){
		G.fnInit(ac);
		this.androidProjectPath = androidProjectPath;
		this.mainGradlePath = mainGradlePath;
		File androidProject = new File(androidProjectPath);
		if(!androidProject.exists()){
			errorListener.onError(new ProgressFail("Android Project is not found",androidProjectPath,"sync"));
		}
		File mainGradle = new File(mainGradlePath);
		if(!mainGradle.exists()){
			errorListener.onError(new ProgressFail("Gradle Project is not found",mainGradlePath,"sync"));
		}
		syncData = new SyncData();
		progressListener.onProgressStart();
		try
		{
			scanGradleProject(mainGradlePath);
		}
		catch (ProgressFail e)
		{
			errorListener.onError(e);
		}
		progressListener.onprogressFinish();
		return syncData;
	}

	private void scanGradleProject(String path)throws ProgressFail{
		String name = path.substring(path.lastIndexOf("/")+1,path.length());

		progressListener.onProgressChange("Gradle Running : " + name);
		GradleInfo gi = (new ProjectManager(ac)).getGradleProjectInfo(path);
		syncData.addGradleInfo(name,gi);
		//Sync jar info
		for(CompileInfo ci : gi.dependencies.compile){
			if(ci.type == CompileInfo.TYPE_FILETREE){
				syncFileTree(ci,path);
			}else if(ci.type == CompileInfo.TYPE_LIB){
				String pathh = ci.value1;
				String namee = ci.value2;
				String fullPath = androidProjectPath + "/" + pathh;
				if(new File(fullPath).exists()){
					syncData.addScanedJar(fullPath);
				}else{
					errorListener.onError(new ProgressFail("file not found in : " + pathh,fullPath,"sync"));
				}
				
			}else if(ci.type == CompileInfo.TYPE_MAVEN){
				Toast.makeText(ac,"Maven Project id not supported now.",Toast.LENGTH_SHORT);
			}else if(ci.type == CompileInfo.TYPE_PROJECT){
				String pathh = ci.value1;
				String fullPath = androidProjectPath + "/" + pathh;
				try
				{
					scanGradleProject(fullPath);
				}
				catch (ProgressFail e)
				{
					errorListener.onError(new ProgressFail(e.toString(),fullPath,"sync"));
				}
			}
		}
		
		
		//############
		//Sync R
		File build = new File(path+"/build");
		if(!build.exists())build.mkdirs();
		if(build.isFile())build.delete(); build.mkdirs();
		File gen = new File(build.getAbsolutePath()+"/gen");
		if(!gen.exists())gen.mkdirs();
		if(gen.isFile())gen.delete(); gen.mkdirs();
		Aapt aapt = new Aapt(Aapt.getAndroidJarPath());
		/*
		try
		{
			aapt = new Aapt(Aapt.requestAndroidJar(ac));
			
		}
		catch (Exception e)
		{
			errorListener.onError(new ProgressFail("cannot run Aapt\n" + e.toString(),null,"sync"));
		}*/
		String manifest = path + "/src/main/AndroidManifest.xml";
		String res = path + "/src/main/res";
		
		String result = aapt.generateR(gen.getAbsolutePath(),manifest,new String[]{res},null);
		AnalysisData ad = AaptResultAnalyze.analysis(result);
		if(!(ad.exitValue==0)){
			ProgressFail f = new ProgressFail("cannot generate R.java",null,"sync");
			f.setAnalysisData(ad);
			errorListener.onError(f);
			//Toast.makeText(ac,ad.toString(),Toast.LENGTH_LONG).show();
		}
		
		
	}
	private void syncFileTree(CompileInfo ci,String mpath){
		String path = ci.value1;
		String name = ci.value2;
		String fullPath = mpath + "/" + path;
		if(!"*.jar".equals(name)) errorListener.onError(new ProgressFail("Only \".jar\" is available",fullPath,"sync"));
		File f = new File(fullPath);
		if(!(f.exists())){
			errorListener.onError(new ProgressFail("folder path is not exist in "+ path,fullPath,"sync"));
			return;
		}
		File list[] = f.listFiles();
		for(File file : list){
			if(file.getName().toLowerCase().contains(".jar")){
				syncData.addScanedJar(file.getAbsolutePath());
			}
		}
	}
	private SyncData syncData;
	public static interface ProgressListener{

		public void onProgressStart();
		public void onProgressChange(String progressName);
		public void onprogressFinish();
	}
	public static interface ErrorListener{
		public boolean onError(ProgressFail progressFail);
	}
}
