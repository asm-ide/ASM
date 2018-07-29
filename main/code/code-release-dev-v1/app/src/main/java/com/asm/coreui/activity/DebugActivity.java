package com.asm.coreui.activity;

import com.asm.ui.R;
import android.app.*;
import android.os.*;
import java.io.*;


public class DebugActivity extends Activity
{
	protected void onCreate(Bundle savedInstanceState)
	{
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.default_layout);
			new AlertDialog.Builder(this).setTitle("error").setMessage(getIntent().getStringExtra("error")).show();
		} catch(Exception e) {}
	}
}
