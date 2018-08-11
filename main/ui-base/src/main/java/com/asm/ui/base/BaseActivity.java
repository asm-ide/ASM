package com.asm.ui.base;

import com.asm.ASM;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;


public class BaseActivity extends AppCompatActivity
{
	private Toolbar mToolbar;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ASM.initOnActivity(this);
	}
	
	
	protected void initToolbar() {
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		
	}
	
	public Toolbar getToolbar() {
		return mToolbar;
	}
}
