package com.asm.gongbj.tools;

import com.android.dx.io.*;
import com.android.dx.merge.*;
import java.io.*;

public class DexMerge
{
	public AnalysisData Merge(String out,String dex1Path, String dex2Path){
		AnalysisData ad = new AnalysisData();
		
		long start = System.currentTimeMillis();
		
		try{
			DexBuffer dexA = new DexBuffer(new File(dex1Path));
			DexBuffer dexB = new DexBuffer(new File(dex2Path));
			DexBuffer merged = new DexMerger(dexA, dexB, CollisionPolicy.KEEP_FIRST).merge();
			merged.writeTo(new File(out));
			ad.exitValue = 0;
		}catch(IOException e){
			ad.exitValue = 1;
			ad.fullLog = e.toString();
		}
		int time = (int)(System.currentTimeMillis()-start)/1000;
		ad.time = time;
		return ad;
	}
}
