package com.asm.ui.base;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.os.Bundle;


public class PlaceHolderFragment extends Fragment
{
	private int mId;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(mId, container, false);
	}
	
	public void setViewId(int id) {
		mId = id;
	}
	
	public static PlaceHolderFragment newFragment(int id) {
		PlaceHolderFragment fragment = new PlaceHolderFragment();
		fragment.setViewId(id);
		return fragment;
	}
}
