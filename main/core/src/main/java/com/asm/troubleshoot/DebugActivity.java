package com.asm.troubleshoot;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;



public class DebugActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// temp
		new AlertDialog.Builder(this)
			.setTitle("Error")
			.setMessage(getIntent().getStringExtra("error"))
			.show();
	}
}
