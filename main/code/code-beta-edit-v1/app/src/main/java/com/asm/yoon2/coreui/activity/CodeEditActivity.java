package com.asm.yoon2.coreui.activity;

import android.app.*;
import android.os.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;

import com.asm.yoon2.widget.*;
//tempory import
import com.asm.yoon2.coreui.*;



public class CodeEditActivity extends AppCompatActivity
{
	private Toolbar toolbar;
	private CodeEdit edit;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.codeedit);
		
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		edit = (CodeEdit) findViewById(R.id.codeedit);
		setSupportActionBar(toolbar);
	}


}
