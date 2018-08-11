package com.asm.gongbj.gradle;
import java.io.*;

import android.content.res.*;
import android.app.*;
import java.util.*;

import com.asm.gongbj.gradle.info.*;

/**
*Class ProjectManager : 
* -> You can do these things with this class : 
* -> 1. Can create android project and library project
* -> 2. Can get GardleInfo of gradle project in android project.
* -> 3. Can get list of gradle projects in android project. it reads top-level settings.gradle
*
*@author GongBJ
*/
public class ProjectManager{
	Activity activity;
	public ProjectManager(Activity ac){
		activity = ac;
		
	}
	/**
	*Create Android project.
	*@param appName : Android Project's app name
	*@param packageName : Android Project's pakageName
	*@param Path : folder path that Android Project will be generated in.
	*/
	public void createAndroidProject(String appName, String packageName,String Path)throws Exception{
		//Create Project Folder
		File p = new File(Path+"/"+appName);
		if(p.exists())throw new Exception("project is already exist");
		p.mkdirs();
		//Create .gitignore File
		File f = new File(p.getAbsolutePath()+"/.gitignore");
		f.createNewFile();
		write("/build",f);
		//Create Top Level build.gradle
		f = new File(p.getAbsolutePath()+"/build.gradle");
		f.createNewFile();
		write("// Top-level build file where you can add configuration options common to all sub-projects/modules." + "\n" + "\nbuildscript {" + "\n    repositories {" + "\n        jcenter()" + "\n    }" + "\n    dependencies {" + "\n        classpath 'com.android.tools.build:gradle:1.+'" + "\n" + "\n        // NOTE: Do not place your application dependencies here; they belong" + "\n        // in the individual module build.gradle files" + "\n    }" + "\n}" + "\n" + "\nallprojects {" + "\n    repositories {" + "\n        jcenter()" + "\n    }" + "\n}" + "\n",f);
		//Create settings.gradle
		f = new File(p.getAbsolutePath() + "/settings.gradle");
		f.createNewFile();
		write("include ':app'",f);

		//Create app Folder
		p = new File(p.getAbsolutePath()+"/app");
		p.mkdirs();
		//Create build.gradle
		f = new File(p.getAbsolutePath()+"/build.gradle");
		f.createNewFile();
		write("apply plugin: 'com.android.application'" + "\n" + "\nandroid {" + "\n    compileSdkVersion 21" + "\n    buildToolsVersion \"21.1.0\"" + "\n" + "\n    defaultConfig {" + "\n        applicationId \"" + packageName +"\"" + "\n        minSdkVersion 14" + "\n        targetSdkVersion 21" + "\n        versionCode 1" + "\n        versionName \"1.0\"" + "\n    }" + "\n    buildTypes {" + "\n        release {" + "\n            minifyEnabled false" + "\n            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'" + "\n        }" + "\n    }" + "\n}" + "\n" + "\ndependencies {" + "\n    compile fileTree(dir: 'libs', include: ['*.jar'])" + "\n}" + "\n",f);
		//Create proguard-rules.pro
		f = new File(p.getAbsolutePath()+"/proguard-rules.pro");
		f.createNewFile();
		write("# Add project specific ProGuard rules here"+"\n# By default, the flags in this file are appended to flags specified"+"\n# in C:\\tools\\adt-bundle-windows-x86_64-20131030\\sdk/tools/proguard/proguard-android.txt"+"\n# You can edit the include path and order by changing the proguardFiles"+"\n# directive in build.gradle."+"\n#"+"\n# For more details, see"+"\n#   http://developer.android.com/guide/developing/tools/proguard.html"+"\n"+"\n# Add any project specific keep options here"+"\n"+"\n# If your project uses WebView with JS, uncomment the following"+"\n# and specify the fully qualified class name to the JavaScript interface"+"\n# class:"+"\n#-keepclassmembers class fqcn.of.javascript.interface.for.webview {"+"\n#   public"+"\n#}"+"\n",f);
		//Create src Folder
		p = new File(p.getAbsolutePath()+"/src");
		p.mkdirs();
		//Create main Folder
		p = new File(p.getAbsolutePath()+"/main");
		p.mkdirs();
		//Create AndroidManifest.xml
		f = new File(p.getAbsolutePath()+"/AndroidManifest.xml");
		f.createNewFile();
		String mani = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + "\n<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\"" + "\n    package=\""+ packageName+"\" >" + "\n" + "\n    <application" + "\n        android:allowBackup=\"true\"" + "\n        android:icon=\"@drawable/ic_launcher\"" + "\n        android:label=\"@string/app_name\"" + "\n        android:theme=\"@style/AppTheme\"" + "\n		android:resizeableActivity = \"true\">" + "\n        <activity" + "\n            android:name=\".MainActivity\"" + "\n            android:label=\"@string/app_name\" >" + "\n            <intent-filter>" + "\n                <action android:name=\"android.intent.action.MAIN\" />" + "\n" + "\n                <category android:name=\"android.intent.category.LAUNCHER\" />" + "\n            </intent-filter>" + "\n        </activity>" + "\n    </application>" + "\n" + "\n</manifest>" + "\n";
		write(mani,f);
		//Create java Folder

		File j = new File(p.getAbsolutePath()+"/java/"+packageName.replace(".","/"));
		j.mkdirs();
		//Create MainActivity.java File
		f = new File(j.getAbsolutePath()+"/MainActivity.java");
		f.createNewFile();
		String ma = "package " + packageName + ";" + "\n" + "\nimport android.app.*;" + "\nimport android.os.*;" + "\n" + "\npublic class MainActivity extends Activity " + "\n{" + "\n    @Override" + "\n    protected void onCreate(Bundle savedInstanceState)" + "\n    {" + "\n        super.onCreate(savedInstanceState);" + "\n        setContentView(R.layout.main);" + "\n    }" + "\n}" + "\n";
		write(ma,f);
		//Create res Folder
		File r = new File(p.getAbsolutePath()+"/res");
		r.mkdirs();
		//Create icon Files
		String dpi[] = {"drawable-hdpi","drawable-mdpi","drawable-xhdpi","drawable-xxhdpi"};
		for(String dS : dpi){
			f = new File(r.getAbsolutePath()+"/" + dS);
			f.mkdirs();
			f = new File(f.getAbsolutePath()+"/ic_launcher.png");
			f.createNewFile();
			AssetManager assetMgr = activity.getAssets();
			InputStream in = assetMgr.open("ic_launcher.png");
			OutputStream out = new FileOutputStream(f);
			byte[] buffer = new byte[1024]; int read; while ((read = in.read(buffer)) != -1) { out.write(buffer, 0, read); }

		}
		//Create Layout
		File l = new File(r.getAbsolutePath()+"/layout");
		l.mkdirs();
		//Create main.xml File
		f = new File(l.getAbsolutePath()+"/main.xml");
		write("<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\""+"\n    android:layout_width=\"match_parent\""+"\n    android:layout_height=\"match_parent\""+"\n    android:gravity=\"center\">"+"\n"+"\n    <TextView"+"\n        android:text=\"@string/hello_world\""+"\n        android:layout_width=\"wrap_content\""+"\n        android:layout_height=\"wrap_content\" />"+"\n"+"\n</LinearLayout>"+"\n",f);
		//Create values Folder
		File v = new File(r.getAbsolutePath()+"/values");
		v.mkdirs();
		//Create styles.xml File
		f = new File(v.getAbsolutePath()+"/styles.xml");
		f.createNewFile();
		write("<?xml version=\"1.0\" encoding=\"utf-8\"?>"+"\n<resources>"+"\n    <style name=\"AppTheme\" parent=\"@android:style/Theme.Holo.Light\">"+"\n	</style>"+"\n</resources>",f);
		//Create strings.xml File
		f = new File(v.getAbsolutePath()+"/strings.xml");
		f.createNewFile();
		write("<?xml version=\"1.0\" encoding=\"utf-8\"?>"+"\n<resources>"+"\n"+"\n    <string name=\"app_name\">MyApp</string>"+"\n    <string name=\"hello_world\">Hello world!</string>"+"\n"+"\n</resources>"+"\n",f);

	}
	/**
	*It creates Library Project.
	*When you call this method, top-level settins.gradle file will be outomatically modified.
	*@param name : Library Project's name
	*@param packageName : Library Project's pakageName
	*@param androidProjectPath : folder path of android project that you want to create in.
	*/
	public void createLibraryProject(String name, String packageName, String androidProjectPath)throws Exception{
		//Create Library Project Folder
		File p = new File(androidProjectPath + "/" + name);
		p.mkdirs();
		//Create build.gradle File
		File f = new File(p.getAbsolutePath()+"/build.gradle");
		f.createNewFile();
		write("apply plugin: 'com.android.library'"+"\n"+"\nandroid {"+"\n    compileSdkVersion 21"+"\n    buildToolsVersion \n21.1.0\n"+"\n"+"\n    defaultConfig {"+"\n        minSdkVersion 14"+"\n        targetSdkVersion 21"+"\n        versionCode 1"+"\n        versionName \n1.0\n"+"\n    }"+"\n    buildTypes {"+"\n        release {"+"\n            minifyEnabled false"+"\n            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'"+"\n        }"+"\n    }"+"\n}"+"\n"+"\ndependencies {"+"\n    compile fileTree(dir: 'libs', include: ['*.jar'])"+"\n}"+"\n",f);
		//Create proguard-rules.pro
		f = new File(p.getAbsolutePath()+"/proguard-rules.pro");
		f.createNewFile();
		write("# Add project specific ProGuard rules here"+"\n# By default, the flags in this file are appended to flags specified"+"\n# in C:\\tools\\adt-bundle-windows-x86_64-20131030\\sdk/tools/proguard/proguard-android.txt"+"\n# You can edit the include path and order by changing the proguardFiles"+"\n# directive in build.gradle."+"\n#"+"\n# For more details, see"+"\n#   http://developer.android.com/guide/developing/tools/proguard.html"+"\n"+"\n# Add any project specific keep options here"+"\n"+"\n# If your project uses WebView with JS, uncomment the following"+"\n# and specify the fully qualified class name to the JavaScript interface"+"\n# class:"+"\n#-keepclassmembers class fqcn.of.javascript.interface.for.webview {"+"\n#   public"+"\n#}"+"\n",f);
		//Create .gitignore File
		f = new File(p.getAbsolutePath()+"/.gitignore");
		f.createNewFile();
		write("/build",f);
		//Create src/maij Folder
		p = new File(p.getAbsolutePath()+"/src/main");
		p.mkdirs();
		//Create AndroidManifest.xml File
		f = new File(p.getAbsolutePath()+"/AndroidManifest.xml");
		f.createNewFile();
		write("<?xml version=\"1.0\" encoding=\"utf-8\"?>"+"\n<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\""+"\n    package=\""+packageName+"\" >"+"\n"+"\n    <application>"+"\n        "+"\n    </application>"+"\n"+"\n</manifest>"+"\n"+"\n",f);
		//Create java Folder
		File j = new File(p.getAbsolutePath()+"/java/"+packageName.replace(".","/"));
		j.mkdirs();
		//Create java File
		j = new File(j.getAbsolutePath()+"/library.java");
		j.createNewFile();
		write("package "+packageName+";"+"\n"+"\npublic class library"+"\n{"+"\n}",j);

		//Write Library Info In settings.gradle
		String str = ReadFile(androidProjectPath+"/settings.gradle");
		str = str + "\ninclude ':"+name+"'";
		write(str, new File(androidProjectPath+"/settings.gradle"));
	}
	private String ReadFile(String path)throws Exception{
		String line;
		File file = new File(path);
		FileInputStream fileInputStream = new FileInputStream (file);
		InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		StringBuilder stringBuilder = new StringBuilder();
		while ( (line = bufferedReader.readLine()) != null ){
			stringBuilder.append(line).append(System.getProperty("line.separator"));
		}
		fileInputStream.close();
		line = stringBuilder.toString();
		bufferedReader.close();
		
		return line;
	}
	
	public GradleProjectInfo[] getGradleProjectsList(String androidProjectPath)throws ProgressFail{
		File f = new File(androidProjectPath+"/settings.gradle");
		if(!f.exists()){
			throw new ProgressFail("'settings.gradle'file is not found in \n"+f.getAbsolutePath(),f.getAbsolutePath(),"Gradle");
		}
		String str;
		try{str = ReadFile(f.getAbsolutePath());}catch(Exception e){throw new ProgressFail("Cannot read 'settins.gradle' file : \n"+e.toString(),f.getAbsolutePath(),"Gradle");}
		String st[] = str.split("\n");
		ArrayList<GradleProjectInfo> list = new ArrayList<>();
		for(String sss : st){
			sss = sss.trim().replace(" ","");
			if(sss.contains("include")){
				if(sss.contains(",")){
						String ss[] = sss.replace("include","").split(",");
						for(String s : ss){
							s = s.replace("'","").replace(":","").trim();
							File fi = new File(androidProjectPath+"/"+s);
							if(!fi.exists()){
								throw new ProgressFail("project "+s+" not found",fi.getAbsolutePath(),"Gradle");
							}
							list.add(new GradleProjectInfo(f.getAbsolutePath(),s));
						}
				}else{
					sss=sss.replace("include","").replace("'","").replace(":","");
					File fi = new File(androidProjectPath+"/"+sss);
					if(!fi.exists()){
						throw new ProgressFail("project "+sss+" not found",fi.getAbsolutePath(),"Gradle");
					}
					list.add(new GradleProjectInfo(f.getAbsolutePath(),sss));
					
				}
			}
		}
		GradleProjectInfo gr[] = new GradleProjectInfo[list.size()];
		int c =0;
		for(GradleProjectInfo ob : list){
			gr[c]=ob;
			c++;
		}
		return gr;
	}
	/*
	public gradleProjectInfo createGradleProjectInfo(String projectName,String androidProjectPath){
		File f=new File(androidProjectPath+"/"+projectName);
		if(!f.exists()){
			return null;
		}
		return new gradleProjectInfo(f.getAbsolutePath(),projectName);
		
	}
	*/
	private int count = 0;
	private String gpp;
	/**
	 *It analyzes build.gradle file in gradle project
	 *@return It returns GradleInfo with full infomations
	 */
	public GradleInfo getGradleProjectInfo(String gradleProjectPath)throws ProgressFail{
		gpp=gradleProjectPath;
		GradleInfo g = new GradleInfo();
		g.fullPath = gradleProjectPath;
		File f=new File(gradleProjectPath+"/build.gradle");
		if(!f.exists())throw new ProgressFail("'build.gradle'is not found in \n"+f.getAbsolutePath(),f.getAbsolutePath(),"Gradle");
		String str;
		try
		{
			str = ReadFile(f.getAbsolutePath());
			
		}
		catch (Exception e)
		{throw new ProgressFail("Cannot read 'build.gradle' file at : \n"+e.toString(),f.getAbsolutePath(),"Gradle");}
		String lines[] = str.trim().split("\n");
		String in = null;
		count=0;
		ci=new ArrayList<>();
		g = readBuildGradle(g,lines,"");
		CompileInfo infos[] = new CompileInfo[ci.size()];
		for(int co =0;co<ci.size();co++){
			infos[co]=ci.get(co);
		}
		g.dependencies.compile=infos;
		
		return g;
	}
	
	
	
	
	private ArrayList<CompileInfo> ci;
	private GradleInfo readBuildGradle(GradleInfo g, String[] str,String tree){
		GradleInfo G = g;
		String sr = tree;
		
		w : while(count<str.length){
			
			////////
			if("".equals(tree)){
				if(str[count].contains("apply plugin")){
					g.plugin=str[count].replace("apply plugin","").replace(":","").replace("'","").trim();
				}
			}else if(".android".equals(tree)){
				
				if(str[count].contains("compileSdkVersion")){
					g.android.compileSdkVersion=str[count].replace("compileSdkVersion","").replace("\"","").replace("'","").trim();
				}else if(str[count].contains("buildToolsVersion")){
					g.android.buildToolsVersion=str[count].replace("buildToolsVersion","").replace("\"","").replace("'","").trim();
				}
			}else if(".android.defaultConfig".equals(tree)){
				if(str[count].contains("applicationId")){
					g.android.defaultConfig.applicationId=str[count].replace("applicationId","").replace("\"","").replace("'","").trim();
				}else if(str[count].contains("minSdkVersion")){
					g.android.defaultConfig.minSdkVersion=str[count].replace("minSdkVersion","").replace("\"","").replace("'","").trim();
				}else if(str[count].contains("targetSdkVersion")){
					g.android.defaultConfig.targetSdkVersion=str[count].replace("targetSdkVersion","").replace("\"","").replace("'","").trim();
				}else if(str[count].contains("versionCode")){
					g.android.defaultConfig.versionCode=str[count].replace("versionCode","").replace("\"","").replace("'","").trim();
				}else if(str[count].contains("versionName")){
					g.android.defaultConfig.versionName=str[count].replace("versionName","").replace("\"","").replace("'","").trim();
				}
			}else if(".dependencies".equals(tree)){
				CompileInfo c = new CompileInfo();
				String line = str[count];
				//Toast.makeText(activity,line,Toast.LENGTH_SHORT).show();
				
				if(line.contains("compile")){
					line = line.replace("compile","").trim();
					if(line.startsWith("'")&&line.endsWith("'")){
						//Maven
						c.type=CompileInfo.TYPE_MAVEN;
						c.value1=line.replace("'","").trim();
						ci.add(c);
					}else if(line.startsWith("files(")&&line.endsWith(")")){
						//File
						c.type=CompileInfo.TYPE_LIB;
						c.value1=line.replace("files(","").replace(")","").replace("'","").replace("\"","").trim();
						c.value2=c.value1.substring(c.value1.lastIndexOf("/")+1);
						ci.add(c);
					}else if(line.startsWith("project(")&&line.endsWith(")")){
						//Project
						c.type=CompileInfo.TYPE_PROJECT;
						c.value1=line.replace("project(","").replace(")","").replace("'","").replace(":","").trim();
						ci.add(c);
					}else if(line.startsWith("fileTree(")&&line.endsWith(")")){
						//FileTreee
						String tmp[] = line.replace("fileTree(","").replace(")","").trim().split(",");
						String dir,include;
						dir="";include="";
						for(String sss:tmp){
							if(sss.trim().startsWith("dir")){
								dir=sss.replace("dir","").replace(":","").replace("'","").replace("\"","").trim();
							}else if(sss.trim().startsWith("include")){
								include=sss.replace("include","").replace(":","").replace("'","").replace("\"","").replace("[","").replace("]","").trim();
							}
						}
						c.type = CompileInfo.TYPE_FILETREE;
						c.value1 = dir;
						c.value2 = include;
						ci.add(c);
					}
				}
				
			}
			//////////////
			if("}".equals(str[count].trim())){
				count++;
				//Toast.makeText(activity,"exist : "+tree,Toast.LENGTH_SHORT).show();
				return G;
			}else if(str[count].contains("{")){
				count++;
				G = readBuildGradle(G,str,sr+"."+str[count-1].replace("{","").trim());
			}else{
				count++;
			}
			
		}
		
		return G;
	}
	private void write(String str,File file)throws Exception{
		FileWriter fw = new FileWriter(file.getAbsoluteFile()); 
		BufferedWriter bw = new BufferedWriter(fw); 
		bw.write(str);
		bw.close();
	}
}
