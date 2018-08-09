package com.asm.ui.activity;

import com.asm.R;

import com.asm.ui.base.BaseActivity;

import android.widget.ImageView;
import android.os.Bundle;


public class LaunchActivity extends BaseActivity
{
	@SuppressWarnings("UnusedDeclaration") // will removed later
	private ImageView mAppIcon;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		setContentView(R.layout.launch);
		
		
		mAppIcon = (ImageView) findViewById(R.id.appicon);
		//Glide.with(this).load(R.drawable.app_icon).into(mAppIcon);
	}
}
