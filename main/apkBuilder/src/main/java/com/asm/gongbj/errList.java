package com.asm.gongbj;

public class errList
{
	public String filePath;
	public String ErrLineCode;
	public String ErrCode;
	public String explane;
	public int line;
	public int type;
	
	public errList(String filePath,String ErrLineCode,String ErrCode,String explane, int line,int type ){
		this.filePath = filePath;
		this.ErrLineCode = ErrLineCode;
		this.ErrCode = ErrCode;
		this.explane = explane;
		this.line = line;
		this.type = type;
	}
	
}
