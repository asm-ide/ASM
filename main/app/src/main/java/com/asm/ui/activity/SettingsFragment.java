package com.asm.ui.activity;

import com.asm.R;

import android.support.v14.preference.PreferenceFragment;
import android.os.Bundle;


public class SettingsFragment extends PreferenceFragment
{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.pref_asm);
	}
	
	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		
	}
}
