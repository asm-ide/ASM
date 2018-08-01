package com.asm.gongbj.gradle;
import android.content.*;
import android.app.*;

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
			errorL.onError(new ProgressFail("Cannot start building because ProgressListener is null",androidGradlePath,"Gradle"));
			return;
		}
		if(errorL==null){
			errorL.onError(new ProgressFail("Cannot start building because ErorrListener is null",androidGradlePath,"Gradle"));
			return;
		}
		resultValue = 1;
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
		try{
			s.sync(androidGradlePath,mainGradlePath);
		}catch(Exception e){
			errorL.onError(new ProgressFail("Error while syncing...",androidGradlePath,"sync"));
			resultValue=0;
		}
		if(resultValue==0)return;
		
		
		
		
	}
	public static interface ProgressListener{

		public void onProgressStart();
		public void onProgressChange(String progressName);
		public void onprogressFinish();
	}
	public static interface ErrorListener{
		public boolean onError(ProgressFail progressFail);
	}
}
