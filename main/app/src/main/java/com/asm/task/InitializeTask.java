package com.asm.task;

import com.asm.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;


public class InitializeTask extends AsyncTask<Activity, Integer, Boolean>
{
	public static interface OnResultListener
	{
		public void onFinishedTask(boolean success);
	}
	
	
	@NonNull
	private Intent mIntent;
	
	private Activity mActivity;
	
	private Exception mException = new NonException();
	
	
	@Override
	protected Boolean doInBackground(Activity[] args) {
		if(mIntent == null) throw new IllegalStateException("intent is null");
		mActivity = args[0];
		
		try {
			// put here some tasks which need to process on launch
			// ex: get data from server
			
		} catch(Exception e) {
			synchronized(mException) {
				mException = e;
			}
			
			return false;
		}
		
		return true;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		
		if(mActivity instanceof OnResultListener) {
			((OnResultListener) mActivity).onFinishedTask(result);
		}
	}
	
	public void setIntent(@NonNull Intent intent) {
		mIntent = intent;
	}
	
	public Intent getIntent() {
		return mIntent;
	}
	
	public Exception getException() {
		synchronized(mException) {
			if(mException instanceof NonException)
				return null;
			
			return mException;
		}
	}
}


class NonException extends Exception {
	
}
