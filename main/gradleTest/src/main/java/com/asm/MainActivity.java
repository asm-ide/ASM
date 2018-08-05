package com.asm;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.asm.gongbj.*;
import com.asm.gongbj.gradle.ProjectManager;
import com.asm.gongbj.gradle.*;
import com.asm.gongbj.gradle.info.*;
import com.asm.gongbj.codeManage.*;

public class MainActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
	}
	public void cap(View v){
		EditText i1 = (EditText)findViewById(R.id.path1_1);
		EditText i2 = (EditText)findViewById(R.id.path1_2);
		EditText i3 = (EditText)findViewById(R.id.path1_3);
		try
		{
			new ProjectManager(MainActivity.this).createAndroidProject(i1.getText().toString(),i2.getText().toString(), i3.getText().toString());
		}
		catch (Exception e)
		{
			EditText o = (EditText)findViewById(R.id.log);
			o.setText(e.toString());
		}

		
	}
	public void alp(View v){
		EditText i1 = (EditText)findViewById(R.id.path2_1);
		EditText i2 = (EditText)findViewById(R.id.path2_2);
		EditText i3 = (EditText)findViewById(R.id.path2_3);
		try
		{
			new ProjectManager(MainActivity.this).createLibraryProject(i1.getText().toString(),i2.getText().toString(), i3.getText().toString());
		}
		catch (Exception e)
		{
			EditText o = (EditText)findViewById(R.id.log);
			o.setText(e.toString());
		}


	}
	
	public void ggi(View v){
		EditText i1 = (EditText)findViewById(R.id.path3_1);
		//EditText i2 = (EditText)findViewById(R.id.path3_2);
		
		try
		{
			GradleInfo gI = new ProjectManager(MainActivity.this).getGradleProjectInfo(i1.getText().toString());
			String log="";
			log=log+"\nplugin : "+gI.plugin;
			log=log+"\nandroid.buildToolsVersion : "+gI.android.buildToolsVersion;
			log=log+"\nandroid.compileSdkVersion : "+gI.android.compileSdkVersion;
			log=log+"\nandroid.defaultConfig.applicationId: "+gI.android.defaultConfig.applicationId;
			log=log+"\nandroid.defaultConfig.minSdkVersion: "+gI.android.defaultConfig.minSdkVersion;
			log=log+"\nandroid.defaultConfig.targetSdkVersion: "+gI.android.defaultConfig.targetSdkVersion;
			log=log+"\nandroid.defaultConfig.versionCode: "+gI.android.defaultConfig.versionCode;
			log=log+"\nandroid.defaultConfig.versionName: "+gI.android.defaultConfig.versionName;
			
			CompileInfo ci[] = gI.dependencies.compile;
			log=log+"\n###################\nCompile Info(dependencies)";
			for(CompileInfo c : ci){
				log = log+"\n\n";
				log = log+"Type : " + String.valueOf(c.type);
				log = log+"\nvalue1 : " + c.value1.toString();
				log = log+"\nvalue2 : " + c.value2;
				
			}
			EditText l = (EditText)findViewById(R.id.log);
			l.setText(log);
			
		
		}catch (Exception e){
			EditText o = (EditText)findViewById(R.id.log);
			o.setText(e.toString());
		}


	}
	
	
	String re = "";
	public void syncb(View v){
		EditText i1 = (EditText)findViewById(R.id.path4_1);
		EditText i2 = (EditText)findViewById(R.id.path4_2);
		Syncer s = new Syncer(this);
		re="";
		s.setProgressListener(new Syncer.ProgressListener(){
			@Override
			public void onProgressStart(){
				
			}
			@Override
			public void onProgressChange(String progressName){
				
			}
			@Override
			public void onprogressFinish(){
				
			}
		});
		s.setErrorListener(new Syncer.ErrorListener(){
			@Override
			public boolean onError(ProgressFail progressFail){
				
				re = re + "\n" + progressFail.getExplane();
				if(progressFail.getAnalysisData()!=null)re = re + "\n" + progressFail.getAnalysisData().toString();
				
				
				return true;
			}
		});
		try{
			s.sync(i1.getText().toString(),i2.getText().toString());
		}catch(Exception e){
			Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
		}finally{
			EditText o = (EditText)findViewById(R.id.log);
			o.setText(re);
		}
		
		
		
	}
	
	public void gradleBuildb(View v){
		EditText i1 = (EditText)findViewById(R.id.path5_1);
		EditText i2 = (EditText)findViewById(R.id.path5_2);
		GradleBuild gb = new GradleBuild(this);
		re="";
		gb.setErrorListener(new GradleBuild.ErrorListener(){
			public void onError(ProgressFail pf){
				re += "\n\nonError : \n";
				re += pf.toString();
			}
		});
		gb.setProgressListener(new GradleBuild.ProgressListener(){
			public void onProgressStart(){
				re += "onProgressStart";
			}
			public void onProgressChange(String str){
				re += "\n\nOnProgressChange : " + "\n" + str;
			}
			public void onprogressFinish(){
				re += "\n\nOnProgresFinish : ";
			}
		});
		try{
			gb.run(i1.getText().toString(),i2.getText().toString());
		}catch(Exception e){
			re += "\n\n" + e.toString();
		}finally{
			EditText o = (EditText)findViewById(R.id.log);
			o.setText(re);
		}
	}
	
	public void cleanb(View v){
		EditText i1 = (EditText)findViewById(R.id.path6_1);
		EditText o = (EditText)findViewById(R.id.log);
		
		try{
			o.setText(CleanJavaCode.clean(i1.getText().toString(),"       "));
		}catch(Exception e){
			Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
			o.setText(e.toString());
		}



	}
	public void class2codeb(View v){
		
		EditText i1 = (EditText)findViewById(R.id.path7_1);
		EditText i2 = (EditText)findViewById(R.id.path7_2);
		
		EditText o = (EditText)findViewById(R.id.log);

		try{
			o.setText(CodeFromClass.get(i1.getText().toString(),i2.getText().toString(),this));
		}catch(Exception e){
			Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
			o.setText(e.toString());
		}
	}
	
}
