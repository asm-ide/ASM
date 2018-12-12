package com.asm.gongbj.tools;


import com.android.dx.io.*;
import com.android.dx.merge.*;
import java.io.*;

public class DexMerge
{
	public AnalysisData Merge(String out,String dex1Path, String dex2Path){
		AnalysisData ad = new AnalysisData();
		
		//long start = System.currentTimeMillis();
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);
			// IMPORTANT: Save the old System.out!
			PrintStream old = System.out;
			// Tell Java to use your special stream
			System.setOut(ps);
			System.setErr(ps);
			
			DexMerger.main(new String[]{out,dex1Path,dex2Path});
			//Dex2jarCmd.main(inputStrr);
			// Put things back
			System.out.flush();
			System.setOut(old);
			System.setErr(old);
			// Show what happened
			ad.fullLog = baos.toString();
			if(ad.fullLog.contains("Merged dex A")){
				ad.exitValue = 0;
			}else{
				ad.exitValue = 1;
			}
		}catch(Throwable e){
			ad.exitValue = 1;
			ad.fullLog = e.toString();
		}
		/*
		try{
			DexBuffer dexA = new DexBuffer(new File(dex1Path));
			DexBuffer dexB = new DexBuffer(new File(dex2Path));
			DexBuffer merged = new DexMerger(dexA, dexB, CollisionPolicy.KEEP_FIRST).merge();
			merged.writeTo(new File(out));
			ad.exitValue = 0;
		}catch(Exception e){
			
		}*/
		//int time = (int)(System.currentTimeMillis()-start)/1000;
		//ad.time = time;
		return ad;
	}
}
