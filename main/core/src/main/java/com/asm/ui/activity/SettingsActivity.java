package com.asm.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class SettingsActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SettingsFragment fragment = new SettingsFragment();
		
		getSupportFragmentManager()
		.beginTransaction()
		.replace(android.R.id.content, fragment)
		.commit();
	}
}
