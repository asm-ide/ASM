package com.lhw.util;

import java.util.ArrayList;


public class FinalizePool
{
	private static ArrayList<Object> locks = new ArrayList<Object>();
	
	
	public static void add(Object obj) {
		locks.add(obj);
	}
	
	public static void cleanUp() {
		for(Object obj : locks) {
			if(obj instanceof Finalizable) {
				if(((Finalizable) obj).canFinalize())
					locks.remove(obj);
				
			}
		}
	}
}
