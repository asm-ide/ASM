package com.asm.gongbj.gradle;
/**
 *This class can contain dependencies's compile info.
 *GradleInfo.dependencies.compile is it.
*/
public class CompileInfo{
	public CompileInfo(){

	}
	public static int TYPE_MAVEN=0;
	public static int TYPE_LIB=1;
	public static int TYPE_PROJECT=2;
	public static int TYPE_FILETREE=3;
	public int type;
	public String value1;
	public String value2;

}
