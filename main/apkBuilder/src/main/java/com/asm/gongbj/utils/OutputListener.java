package com.asm.gongbj.utils;

import com.asm.lib.io.*;
import com.asm.ASMT.G;
public class OutputListener
{
	private static StringWriterOutputStream swos;
	public static void start(){
		swos = new StringWriterOutputStream();
		G.ide.fnRedirectOutput(swos);
		
	}
	public static String stop(){
		G.ide.fnLogOutput(swos);
		return swos.toString();
	}
}
