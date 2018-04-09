package com.lhw.util;


public class Pair<T>
{
	public T a, b;
	
	
	public Pair() {}
	
	public Pair(T a, T b) {
		this.a = a;
		this.b = b;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Pair)) return false;
		Pair pair = (Pair) obj;
		return a == pair.a && b == pair.b;
	}
	
	public Object equalerA() {
		return new EqualerWrapperA(this);
	}
	
	public Object equalerB() {
		return new EqualerWrapperB(this);
	}
	
	public static Object[] arrayEqualerA(Pair[] pairs) {
		Object[] objs = new Object[pairs.length];
		for(int i = 0; i < pairs.length; i++)
			objs[i] = new EqualerWrapperA(pairs[i]);
		return objs;
	}
	
	public static Object[] arrayEqualerB(Pair[] pairs) {
		Object[] objs = new Object[pairs.length];
		for(int i = 0; i < pairs.length; i++)
			objs[i] = new EqualerWrapperB(pairs[i]);
		return objs;
	}
	
	private static class EqualerWrapperA
	{
		private Pair mPair;
		
		
		public EqualerWrapperA(Pair pair) {
			mPair = pair;
		}

		@Override
		public boolean equals(Object obj) {
			if(obj instanceof Pair) {
				return mPair.equalerA().equals((Pair) obj);
			} return false;
		}
	}
	
	private static class EqualerWrapperB
	{
		private Pair mPair;
		
		
		public EqualerWrapperB(Pair pair) {
			mPair = pair;
		} 
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof Pair) {
				return mPair.equalerB().equals((Pair) obj);
			} return false;
		}
	}
}
