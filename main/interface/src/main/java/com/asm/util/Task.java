package com.asm.util;

import java.util.concurrent.FutureTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;


public abstract class Task<T>
{
	public static final int STATUS_NEW = 0;
	public static final int STATUS_RUNNING = 1;
	public static final int STATUS_CANCELED = 2;
	public static final int STATUS_DONE = 3;
	
	private boolean mHaveEverStarted = false;
	
	
	public static interface OnTaskCompletedListener<T>
	{
		public void onTaskComplete(T result);
	}
	
	
	private class MyFutureTask extends FutureTask<T>
	{
		public MyFutureTask(Callable<T> callable) {
			super(callable);
		}
		
		
		
		@Override
		protected void done() {
			super.done();
			
			if(mListener != null)
				callListener();
		}
	}
	
	
	private MyFutureTask mFuture;
	private OnTaskCompletedListener<T> mListener;
	
	
	public Task() {
		super();
		
		mFuture = new MyFutureTask(new Callable<T>() {
				@Override
				public T call() throws Exception {
					return process();
				}
		});
	}
	
	
	public T run() {
		mHaveEverStarted = true;
		
		try {
			mFuture.run();
			return mFuture.get();
		} catch(Exception e) {
			return null;
		}
	}
	
	public int getStatus() {
		return mFuture.isDone()? STATUS_DONE : (mFuture.isCancelled()? STATUS_CANCELED : (mHaveEverStarted? STATUS_RUNNING : STATUS_NEW));
	}
	
	
	public abstract T process();
	
	
	public void setListener(OnTaskCompletedListener<T> listener) {
		mListener = listener;
	}
	
	
	public void callListener() {
		if(mListener != null)
			mListener.onTaskComplete(get());
	}
	
	public boolean stop(boolean mayInterruptIfRunning) {
		return mFuture.cancel(mayInterruptIfRunning);
	}
	
	
	@SuppressWarnings("unsafe")
	public T get() {
		try {
			return mFuture.get();
		} catch(ExecutionException e) {
			throw new RuntimeException(e.getCause());
		} catch (InterruptedException e) {
			System.err.println("Task#get() failed: " + e.getMessage());
			e.printStackTrace();
			
			return null;
		}
	}
	
	
	
	public static <T> Task<T> getTaskWithCallable(Callable<T> callable) {
		return new CallableTask<T>(callable);
	}
}



class CallableTask<T> extends Task<T>
{
	private Callable<T> mCallable;
	
	
	public CallableTask(Callable<T> callable) {
		mCallable = callable;
	}
	
	
	@Override
	public T process() {
		try {
			return mCallable.call();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}



