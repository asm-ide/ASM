package com.asm.gongbj.gradle.info;
/**
*This class can contain all informations of build.gradle
*/
public class GradleInfo
{
	public String fullPath;
	public android android;
	public dependencies dependencies;
	public String plugin;
	public GradleInfo(){
		android=new android();
		dependencies=new dependencies();
	}
	public class android{
		public defaultConfig defaultConfig;
		public android(){
			defaultConfig=new defaultConfig();
		}
		public String compileSdkVersion;
		public String buildToolsVersion;
		public class defaultConfig{
			public defaultConfig(){
				
			}
			public String applicationId;
			public String minSdkVersion;
			public String targetSdkVersion;
			public String versionCode;
			public String versionName;
		}
	}
	public class dependencies{
		public dependencies(){
			
		}
		public CompileInfo[] compile;
		
		
	}
	
}
