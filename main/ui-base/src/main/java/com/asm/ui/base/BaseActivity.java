package com.asm.ui.base;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


public class BaseActivity extends AppCompatActivity
{
	private Toolbar mToolbar;
	
	
	protected void initToolbar() {
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		
	}
	
	public Toolbar getToolbar() {
		return mToolbar;
	}
}
