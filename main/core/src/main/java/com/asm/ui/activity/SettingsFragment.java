package com.asm.ui.activity;

import com.asm.R;

import android.support.v7.preference.PreferenceFragmentCompat;
import android.os.Bundle;


public class SettingsFragment extends PreferenceFragmentCompat
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
