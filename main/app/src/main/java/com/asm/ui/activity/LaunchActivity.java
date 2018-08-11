package com.asm.ui.activity;

import com.asm.R;
import com.asm.ui.base.BaseActivity;

import com.asm.task.InitializeTask;

import com.asm.troubleshoot.ASMExceptionHandler;

import android.os.Bundle;
import android.os.Handler;
import android.os.AsyncTask;
import android.app.Activity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.AlphaAnimation;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;


public class LaunchActivity extends BaseActivity implements InitializeTask.OnResultListener
{
	private FrameLayout mMasterLayout;
	private ImageView mAppIcon;
	
	private AlphaAnimation mSplash;
	
	private Intent mIntent;
	private InitializeTask mTask;
	
	private Handler mHandler;
	
	private Boolean mOvered = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.launch);
		
		// find views
		mMasterLayout = (FrameLayout) findViewById(R.id.masterlayout);
		mAppIcon = (ImageView) findViewById(R.id.appicon);
		
		// load images
		RequestManager glide = Glide.with(this);
		glide.load(R.drawable.app_icon).into(mAppIcon);
		
		// splash!
		mIntent = new Intent(this, MainActivity.class);
		mHandler = new Handler();
		
		mTask = new InitializeTask();
		mTask.setIntent(mIntent);
		mTask.execute(new Activity[] {this});
		
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		mSplash = new AlphaAnimation(0f, 1f);
		mSplash.setInterpolator(new AccelerateDecelerateInterpolator());
		mSplash.setDuration(700L);
		mSplash.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(Animation anim) {}
				
				@Override
				public void onAnimationEnd(Animation anim) {
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							if(!mTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
								synchronized(mOvered) {
									mOvered = true;
								}
							} else {
								startMain();
							}
						}
					}, 1500L);
				}
				
				@Override
				public void onAnimationRepeat(Animation anim) {}
			});
		
		mMasterLayout.startAnimation(mSplash);
	}
	
	private void startMain() {
		startActivity(mIntent);
		finish();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		mHandler.removeCallbacks(null, null);
		mTask.cancel(true);
	}
	
	@Override
	public void onFinishedTask(boolean success) {
		if(!success) {
			mHandler.removeCallbacks(null, null);
			
			Exception e = mTask.getException();
			
			// TODO: on error
			ASMExceptionHandler.getInstance().uncaughtException(Thread.currentThread(), e);
			return;
		}
		
		synchronized(mOvered) {
			if(mOvered) {
				startMain();
			}
		}
	}
}
